package com.ss.controller;


import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.ss.cache.RedisCache;
import com.ss.pojo.Student;
import com.ss.pojo.User;
import com.ss.pojo.Vocation;
import com.ss.service.StudentService;
import com.ss.service.UserService;
import com.ss.service.VocationService;
import com.ss.util.CookieUtil;
import com.ss.util.DateUtil;
import com.ss.util.IPUtil;
import com.ss.util.JWTUtil;
import com.ss.util.third_party_api.EmailUtil;
import com.ss.util.third_party_api.OSSUtil;
import com.ss.util.third_party_api.RandomUtil;
import com.ss.util.third_party_api.SMSUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class T7Controller {

    Logger logger = LoggerFactory.getLogger(T7Controller.class);

    @Autowired
    UserService userService;

    @Autowired
    VocationService vocationService;

    @Autowired
    RedisCache redisCache;

    String message = "message";

    /**
     *   全局的MAP用于传递数据。好处是公用一个map节省内存，
     *   坏处是容易发生线程安全问题。
     */
    Map map = new HashMap();

    // 退出
    @RequestMapping(value = "/u/logout", method = RequestMethod.GET)
    public String getCompany(HttpServletResponse response, HttpServletRequest request)throws Exception{
        CookieUtil.clearTokenCookie(response, request);
        return "redirect:/login";
    }

    // 用于展示detail页面前的数据填充
    @RequestMapping(value = "/u/detail")
    public String displayInfo(Model model, HttpServletRequest request) {

        User user =  (User)request.getAttribute("tokenUser");
        User user1 = userService.getObjectById(user.getId());
        if(user1 == null){
            logger.error("数据库无此数据");
            model.addAttribute(message,"查无此人");
            return "failure";
        }
        user1.setLoginTime(DateUtil.longToString(Long.parseLong(user.getLoginTime()),"MM月dd日 HH:mm"));
        model.addAttribute("user",user1);

        return "home.detail";
    }

/************************************* 上传头像 ********************************************************/

    @RequestMapping(value = "/u/upload", method = RequestMethod.POST)
    public Object getUpload(Model model, HttpServletRequest request,
                          @RequestParam(value = "description",required=false) String description,
                          @RequestParam("file") MultipartFile file)throws Exception
    {
        System.out.println(description);
        //如果文件不为空，写入上传路径
        if(!file.isEmpty()) {
            String filename = file.getOriginalFilename();
            // 在拦截器里，将JWT里的内容解析成对象，并放到了request里
            User user =  (User)request.getAttribute("tokenUser");
            /***
             * substring(int beginIndex)
             * 返回一个新的字符串，它是此字符串的一个子字符串。该子字符串从指定索引处的字符开始，直到此字符串末尾。
             *
             * 比如 001.png  以下代码得到  .png
             */
            String suffix=filename.substring(filename.lastIndexOf("."));
            String key = OSSUtil.getImgKey(user.getUsername(),suffix);
            logger.info("图片路径(key):"+key);
            OSSUtil.upload(file,key);

            User user1 = new User();
            // 这个user1仅用于将头像路径添加到数据库，所以只赋值下面两个属性即可，为null的属性将不执行更新
            user1.setId(user.getId());
            String link = OSSUtil.getImgUrlPrivate(key);
            logger.info("获得访问链接(图片)："+link);
            user1.setPortrait(link);
            logger.info("即将发往数据库的对象："+user1);
            if(userService.updateObject(user1) == 1){

                // 成功后，转发到 /u/detail，处理页面展示前的数据填充
                return "forward:/u/detail";
            }

        }

        return "failure";
    }

 /************************************* 页面中转 ********************************************************/

 // 这个接口用来中转，根据请求里不同参数跳到不同页面
        @RequestMapping(value = "/u/bind", method = RequestMethod.GET)
        public String BindItemTEST( Model model, HttpServletRequest request,
            User user,
            @RequestParam("action") String actoin,
            @RequestParam("target")String target)throws Exception
        {

            logger.info(actoin+"  "+target);

            if("phone".equals(target)&&"do".equals(actoin)){
                    return "binding";
            }
            if("phone".equals(target) && "undo".equals(actoin)){
                    logger.info("undo something");
            }
            if("email".equals(target) && "do".equals(actoin)){
                return "bindingEmail";
            }
            if("email".equals(target) && "undo".equals(actoin)){
                logger.info("undo something");
            }
            if (logger.isWarnEnabled()){
                logger.warn("非法参数");
            }

        return "forward:/u/detail";
    }


/******************************************** 手机区域 ******************************************************************/


/*************************************** 一、手机绑定 ******************************************/


/********************* 1.获取验证码 ******************/
    /**
     *  收到ajax的请求后，发送验证码
     * 这里要加 @RequestBody ，这样才能从请求体中获取数据
     */
    // todo 测试成功    (细节bug待发现)
    @ResponseBody
    @RequestMapping(value = "/u/getcode/phone", method = RequestMethod.POST)
    public Map sign( Model model, HttpServletRequest request,
                        @RequestBody User user )throws Exception
    {
        // 由于是公用map,先清除所有数据
        map.clear();

        logger.info("进入了这里："+user);

        if(user.getPhone()==null || user.getPhone().equals("")){
            logger.warn("没有收到手机号 ！");
            map.put("msg_phone_binding","没有收到手机号 ");
            return map;
        }

        // /u的接口，能执行到这里说明request中的ip和token中的一样
        user.setIP(IPUtil.getIP(request));
        logger.info(user+"");
        Map mapRusult = userService.sendPhoneCode(user);
        if(!"PHONEOK".equals(mapRusult.get("msg"))){
            logger.warn((String) mapRusult.get("msg"));
            map.put("msg_phone_binding",mapRusult.get("msg"));
            return map;
        }

        logger.info("短信发送请求成功");
        map.put("msg_phone_binding","短信发送请求成功");
        return map;
    }

/******************* 2.对传来的验证码校验 ************************/

    // todo 测试成功    (细节bug待发现)
    @RequestMapping(value = "/u/verify", method = RequestMethod.POST)
    public String veryfiy( Model model, HttpServletRequest request,
                           User user )throws Exception
    {

        logger.info(user+"");
        if(user.getPhone()==null || user.getTemcode() ==null){
            model.addAttribute("message","<br/><font color='red' size='3'>输入为空</font><br/>");
            return "failure";
        }

        String temcode = redisCache.getValString(user.getPhone());

        if(temcode == null){
            model.addAttribute("message","验证码已过期。");
            return "failure";
        }

        logger.info("缓存取出的验证码为："+temcode);
        if(!user.getTemcode().equals(temcode)){
            model.addAttribute("message","验证码输入有误");
            return "failure";
        }
        // userService
        User tokenUser = (User)request.getAttribute("tokenUser");
        logger.info("tokenUser信息为："+tokenUser);
        user.setId(tokenUser.getId());
        logger.info("即将更新数据库，user中信息为："+user);
        if(1 != userService.updateObject(user)){
            model.addAttribute("message","更新数据库出错");
            return "failure";
        }
        // 操作成功后将缓存删除，即验证码失效
        redisCache.deleteCache(user.getPhone());
        model.addAttribute("message","<br/><font color='green' size='5'>绑定成功</font><br/>");
        return "success";

    }

 /*********************************** 二、手机验证码登录  *****************************************/


 /***************1.发送手机验证码*******************/

 // todo 测试成功    (细节bug待发现)
 @RequestMapping(value = "/getcode/phone", method = RequestMethod.POST)
 public @ResponseBody Map signCodePhone( Model model, HttpServletRequest request,
                                     @RequestBody User user )throws Exception
 {
     logger.info(""+user);

     if(user.getPhone()==null || "".equals(user.getPhone())){
         logger.warn("没有收到手机号码 ！");
         map.put("msg_phone_login","没有收到手机号码");
         return map;
     }

     // 在发送验证码前，先去数据库检查下有无此号码
     if(userService.isLogin(user) == null){
         logger.warn(" 该手机号还未绑定(就是数据库里没有) ！");
         map.put("msg_phone_login","该手机号还未绑定");
         return map;
     }

     user.setIP(IPUtil.getIP(request));

     logger.info("调用sendPhoneCode前的user:"+user);

     Map mapR = userService.sendPhoneCode(user);
     if(!"PHONEOK".equals(mapR.get("msg"))){
         logger.warn((String) mapR.get("msg"));
         map.put("msg_phone_login",(String) mapR.get("msg"));
         return map;
     }

     logger.info("短信发送请求成功");
     map.put("msg_phone_login","短信发送请求成功");
     return map;
 }


    /************2.对传来的验证码进行验证**************/
    // todo 测试成功    (细节bug待发现)
    @RequestMapping(value = "/verify/phone", method = RequestMethod.POST)
    public String veryfiyPhoneCode( Model model, HttpServletRequest request,
                                HttpServletResponse response,
                                User user )throws Exception
    {

        // 排除手机和验证码为空的情况
        logger.info("即将：处理用户输入的手机验证码 "+user);
        // String phone = user.getPhone();

        if(user.getPhone()==null || user.getTemcode() ==null ||user.getPhone().equals("") || user.getTemcode().equals("")){
            model.addAttribute("message","<br/><font color='red' size='3'>验证码或手机号输入为空</font>");
            return "failure";
        }

        //排除验证码不匹配的情况
        String temcode = redisCache.getValString(user.getPhone());
        if(temcode == null){ model.addAttribute("message","验证码过期");
            return "failure";
        }

        logger.info("从缓存中取出的验证码为："+temcode);
        if(!user.getTemcode().equals(temcode)){
            model.addAttribute("message","<br/><font color='red' size='3'>验证码输入错误，登录失败</font>");
            return "failure";
        }

        String id = userService.isLogin(user);

        if(id == null){
            model.addAttribute("message","<br/><font color='red' size='3'>手机未绑定，" +
                    "请确认在获取验证码之后，提交验证码之前，您未改动手机号的输入</font>");
            return "failure";
        }

        User userTotoken = CookieUtil.prepareUserToJwt(request,id);
        logger.info("即将签发JWT，看一下user:"+userTotoken);
        // 有效期60分钟
        String token = JWTUtil.sign(userTotoken,CookieUtil.MAX_MS);
        CookieUtil.addTokenCookie(response,request,token);

        // 登录成功后删除验证码缓存。让其失效
        redisCache.deleteCache(user.getPhone());
        model.addAttribute("message","<br/><font color='green' size='5'>登录成功</font><br/>");
        return "success";

    }



/************************************************ 邮箱区域 ***************************************************************/

/********************************* 邮箱绑定 *********************************/

// todo 测试成功    (细节bug待发现)
    //  给邮箱发送激活连接
    @RequestMapping(value = "/u/email/getLink")
    public String getLinkMail( Model model,HttpServletRequest request,User user) throws Exception {

        User tokenUser =  (User)request.getAttribute("tokenUser");
        logger.info("taskUser："+tokenUser);
        logger.info("user："+user);
        if(user.getEmail()==null || "".equals(user.getEmail())){
            model.addAttribute(message,"没有收到邮箱 ！");
            return "failure";
        }
        tokenUser.setEmail(user.getEmail());
        logger.info("调用发邮件方法前，看下User对象里的参数："+tokenUser);
        Map map = userService.sendLinkOrCodeByMail(tokenUser, IPUtil.getBasePath(request));

        if(!"EMAILOK".equals(map.get("msg_email"))){
            model.addAttribute(message,map.get("msg_email"));
            return "failure";
        }

        model.addAttribute(message,"激活链接发送成功，请关注邮箱。链接10分钟内有效");
        return "success";

    }

    // todo 测试成功    (细节bug待发现)
    /**
     * 处理   点击激活连接后的验证
     *
     * 因为不能确定用户使用同一设备进入邮箱打开连接，
     * 所以可以不对IP做判同处理
     *
     * 但是这里是练手的，就这样了
     */
    @RequestMapping(value = "/mail/activation")
    public String activationMail( Model model,HttpServletRequest request, String token) throws Exception {

        User user = JWTUtil.unsign(token,User.class);
        if(user == null){
            model.addAttribute(message,"<br/><font color='red' size='3'>链接已过期，或其他原因</font><br/>");
            return "failure";
        }

        String ip = IPUtil.getIP(request);
        if(!ip.equals(user.getIP())){
            model.addAttribute(message,"请使用同一设备打开激活连接，登录ip(jwt中ip): "+
                    user.getIP()+" <br/>打开链接的设备ip:  "+IPUtil.getIP(request));
            return "failure";
        }

        // 更新操作，只留需要属性，其余为空
        User userToDB = new User();
        userToDB.setId(user.getId());
        userToDB.setEmail(user.getEmail());

        if(!(userService.updateObject(userToDB)==1)){
            model.addAttribute(message,"向数据绑定时失败");
            return "failure";
        }

        return "redirect:/u/detail";
   }

 /********************************* 邮箱登录 ***************************************/

    // todo 测试成功    (细节bug待发现)
    //  处理   邮箱验证码的发送请求，处理的是ajax，所以没有带 JWT token的； 把/u去掉，不然ajax请求会被拦截
    @RequestMapping(value = "/getcode/email", method = RequestMethod.POST)
    public @ResponseBody Map signemail( Model model, HttpServletRequest request,
                        @RequestBody User user )throws Exception
    {
        logger.info("邮箱登录:"+user);

        map.clear();

        if(user.getEmail()==null){
            logger.warn("没有收到邮箱 ！");
            map.put("msg","没有收到邮箱");
            return map;
        }

        // 在发送验证码前，先去数据库检查下
        if(userService.isLogin(user) == null){
            logger.warn(" 该邮箱还未绑定注册(就是数据库里没有) ！");
            map.put("msg","该邮箱还未绑定帐号");
            return map;
        }


        user.setIP(IPUtil.getIP(request));
        // 邮箱验证码登录场景下，-1作为标记，否则按邮箱激活处理
        user.setTemcode("-1");
        logger.info("调用sendLinkOrCodeByMail前的user:"+user);
        // 用于登录是，basePath不需要传数据
        Map mapR = userService.sendLinkOrCodeByMail(user,"");
        if(!"EMAILOK".equals(mapR.get("msg_email"))){
            logger.warn((String) mapR.get("msg_email"));
            map.put("msg",(String) mapR.get("msg_email"));
            return map;
        }

        logger.info("短信发送请求成功");
        map.put("msg","短信发送请求成功");
        map.put("status","1");
        return map;
    }

    /**
     * 邮箱登录
     *
     * 验证码对比无误后，去数据库查找到对于ID
     * 将id loginTime ip 放入token中
     *
     */
    // todo 测试成功    (细节bug待发现)
    @RequestMapping(value = "/verify/email", method = RequestMethod.POST)
    public String veryfiyemail( Model model, HttpServletRequest request,
                                HttpServletResponse response,
                           User user )throws Exception
    {

        // 排除邮箱和验证码为空的情况
        logger.info("即将：处理用户输入的邮箱验证码 "+user);
        if(user.getEmail()==null || user.getTemcode() ==null){
            model.addAttribute("message","<br/><font color='red' size='3'>验证码或邮箱输入为空</font>");
            return "failure";
        }

        //排除验证码不匹配的情况
        String temcode = redisCache.getValString(user.getEmail());
        if(temcode == null){ model.addAttribute("message","验证码已过期");
            return "failure";}

        logger.info("从缓存中取出的验证码为："+temcode);
        if(!user.getTemcode().equals(temcode)){
            model.addAttribute("message","<br/><font color='red' size='3'>验证码输入错误，登录失败</font>");
            return "failure";
        }

        String id = userService.isLogin(user);

        if(id == null){
            model.addAttribute("message","<br/><font color='red' size='3'>邮箱未绑定，" +
                    "请确认获取验证码后，提交验证码前，您未改动邮箱</font>");
            return "failure";
        }

        User userToJwt = CookieUtil.prepareUserToJwt(request,id);
        logger.info("即将签发JWT，看一下user:"+userToJwt);
        // 有效期60分钟
        String token = JWTUtil.sign(userToJwt,CookieUtil.MAX_MS);

        CookieUtil.addTokenCookie(response,request,token);

        // 登录成功后删除验证码缓存。让其失效
        redisCache.deleteCache(user.getEmail());
        model.addAttribute("message","<br/><font color='green' size='5'>登录成功</font><br/>");
        return "success";

    }

}
