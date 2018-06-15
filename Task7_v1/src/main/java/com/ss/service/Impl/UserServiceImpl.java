package com.ss.service.Impl;

import com.ss.Dao.UserDao;
import com.ss.cache.RedisCache;
import com.ss.pojo.User;
import com.ss.pojo.exception.ForbiddenException;
import com.ss.pojo.exception.UnacceptableException;
import com.ss.service.UserService;
import com.ss.util.DataCheckUtil;
import com.ss.util.JWTUtil;
import com.ss.util.third_party_api.EmailUtil;
import com.ss.util.third_party_api.RandomUtil;
import com.ss.util.third_party_api.SMSUtil;
import org.hibernate.validator.constraints.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Service("userService")
public class UserServiceImpl implements UserService{
    @Resource
    UserDao userDao;

    @Autowired
    RedisCache redisCache;

    Map map = new HashMap();

    @Override
    public int registerUser(User user) {
        user.setCreateAt(System.currentTimeMillis());
        return userDao.registerUser(user);
    }

    @Override
    public String isLogin(User user) {
        return userDao.isLogin(user);
    }


    @Override
    public int updateObject(User user) {
        user.setUpdateAt(System.currentTimeMillis());
        return userDao.updateObject(user);
    }


    @Override
    public User getObjectById(int id) {
        return userDao.getObjectById(id);
    }

    /**
     * 该方法用于检查是否可以发送短信
     * 
     * 判断原则：
     * 
     * 同一IP 10小时内不能超过5次(可以根据业务场景改变)
     * 
     * 同一手机号码10小时不能超过5次
     * 
     * 这样，破解方法就是，用一个ip一个号码发6次，在换个ip和号码继续
     *
     * Map特性：key不能重复，后面put的会覆盖前者
     * 
     *
     * 这个方法传入一个user,返回一个map，如果业务执行正常map为空，否则在msg里放信息
     *
     */
    @Override
    public Map sendPhoneCode(User user) throws Exception {
        //代表类型是手机验证码，不是邮箱验证码
        String type = "phonecode";
        String phone = user.getPhone();
        String phoneKey = phone+type;
        String ipKey = user.getIP()+type;

        DataCheckUtil.isCellphone(phone);

        map.put("msg", "PHONEOK");

        String phoneResult = redisCache.getValString(phoneKey);
        if(phoneResult == null){
            phoneResult = "0";
            redisCache.setKeyStringWithExpireTime(phoneKey,phoneResult,36000L);
        }

        // 10小时内调用该方法大于5次，拒绝
        if(Integer.valueOf(phoneResult) >= 5){
            map.put("msg","该号码请求验证码太频繁");
            return map;
        }

        String ipResult = redisCache.getValString(ipKey);
        if(ipResult == null){
            ipResult = "0";
            redisCache.setKeyStringWithExpireTime(ipKey,ipResult,36000L);
        }

        //10小时内同一ip来了5次，拒绝
        if(Integer.valueOf(ipResult) >= 5){
            map.put("msg","该IP请求验证码太频繁");
            return map;
        }

        String code = SMSUtil.sendMessageAuto(phone);
        if("false".equals(code)){
            map.put("msg","短信发送失败,ip+phone(绑定时调用)");
            return map;
        }

        // 短信发送成功后, 将验证码存放到缓存
        redisCache.setKeyStringWithExpireTime(phone,code,600L);

        redisCache.incrOne(phoneKey);
        redisCache.incrOne(ipKey);


       return map;
    }

    /**
     *
     * @param user     需具有属性：ip email
     * @param basePath
     * @return
     * @throws Exception
     */
    @Override
    public Map sendLinkOrCodeByMail(User user, String basePath) throws Exception {
        //代表类型是手机验证码，不是邮箱验证码

        String msg_email = "msg_email";
        String type = "emailCodeOrLink";
        // 用于存邮箱验证码的Key
        String email = user.getEmail();
        DataCheckUtil.isMail(email);

        // 用于存邮箱请求次数的Key
        String emailKey = email+type;
        String ipKey = user.getIP()+type;
        if(user.getIP()==null){
            new ForbiddenException("ip不能为null:"+user.getIP());
        }


        // 由于和发送手机短信的方法共用一个map，为防止线程安全问题，map的key一定要不一样
        map.put(msg_email, "EMAILOK");

        String emailResult = redisCache.getValString(emailKey);
        if(emailResult == null){
            emailResult = "0";
            redisCache.setKeyStringWithExpireTime(emailKey,emailResult,36000L);
        }

        // 如果每次都是给同一个邮箱发邮件，10小时内大于10次，拒绝
        //这里写3次是为了测试方便，具体次数根据业务场景而定
        if(Integer.valueOf(emailResult) >= 3){
            map.put(msg_email,"该邮箱请求太频繁————检测到多次向同一邮箱发邮件");
            return map;
        }



        String ipResult = redisCache.getValString(ipKey);

        if(ipResult == null){
            ipResult = "0";
            redisCache.setKeyStringWithExpireTime(ipKey,ipResult,36000L);
        }

        //10小时内同一ip来了5次，拒绝     测试奏效
        if(Integer.valueOf(ipResult) >= 5){
            map.put(msg_email,"同一IP请求次数太频繁————频繁请求发邮件");
            return map;
        }


        String code = RandomUtil.getRandom(6);
        // 属于登录要求发送验证码的，设置 Temcode 为-1
        if("-1".equals(user.getTemcode())){

            if(!EmailUtil.sendSingleEmailCode(email,code)){
                map.put(msg_email,"邮件发送失败，用于登录时");
                return map;
            }
            // 发送成功，将验证码存入缓存。 10分钟过期
            redisCache.setKeyStringWithExpireTime(email,code,600L);

            redisCache.incrOne(emailKey);
            redisCache.incrOne(ipKey);
            return map;
        }

        System.out.println(user);
        // 这里加入一层逻辑，发送激活链接时判断邮箱是否已被注册
        User userResule = userDao.getObjectById(user.getId());

        System.out.println(userResule);
        if(null!=userResule.getEmail() && !"".equals(userResule.getEmail())){
            map.put(msg_email,"该账户已有注册邮箱，您是想换绑吗，换绑功能暂未开启");
            return map;
        }


        user.setTemcode(code);
        // 该jwt有效时间只有10分钟，因为仅用于激活邮箱
        String token = JWTUtil.sign(user,1000*60*10);
        StringBuffer sb = new StringBuffer();
        //拼接链接
        sb.append(basePath).append("/mail/activation").append("?token=").append(token);

        String link = sb.toString();

        if(!EmailUtil.sendSingleEmailLink(email,link)){
            map.put(msg_email,"邮件发送失败，用于登录");
            return map;
        }


        redisCache.incrOne(emailKey);
        redisCache.incrOne(ipKey);

        return map;

    }




}
