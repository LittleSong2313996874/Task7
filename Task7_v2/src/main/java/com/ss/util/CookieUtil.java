package com.ss.util;


import com.ss.controller.T7Controller;
import com.ss.pojo.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/*有效时间为1个小时*/
public class CookieUtil {

    static Logger logger = LoggerFactory.getLogger(CookieUtil.class);

    //存活时间，单位：秒
    public static final int MAX_SEC = 60 * 60;
    //存活时间，单位：毫秒
    public static final long MAX_MS = 1000L * MAX_SEC;

    //添加存储Token的Cookie
    public static void addTokenCookie(HttpServletResponse response, HttpServletRequest request,String token) {
        Cookie cookie = new Cookie("mark", token);
        //设置为"/"时，全服务器都能访问到这个cookie。
        // 也可以具体化，使只能有一个webapp才能访问，比如添加contextPath
        cookie.setPath(request.getContextPath());
        //存活时间，删除时设置为0即可（别忘记设置vaule为null）
        cookie.setMaxAge(MAX_SEC);
        //本项目使用了JWT，就只需往session里面添加cookie，不需要添加其他东西了
        response.addCookie(cookie);
    }

    // 验证cookie的有效性： 过滤掉无用cookie，然后在我们要的cookie里解密touken验证其有效性。
    public static boolean checkTokenCookie(HttpServletRequest request, HttpServletResponse response) {
        boolean result = false;
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0)
            return false;
        for (Cookie cookie : cookies) {
            //筛选排除无关cookie
            if (!Objects.equals(cookie.getName(), "mark")) {
                continue;
            }
            //获取所有名为“mark”的Cookie的值
            String token = cookie.getValue();
            if (token == null) {
                continue;
            }
           User user;
            //可能会抛出异常，终止方法
            user = JWTUtil.unsign(token, User.class);
            if(user==null){
                // 安照JWTUtil里的规则，过期token会返回null;对于过期的cookie这里要进行删除。
                CookieUtil.clearTokenCookie(response,request);
                continue;
            }
            //如果ip不对应，拒绝访问
            if (!IPUtil.getIP(request).equals(user.getIP())) {
                /**
                 *  continue会跳出本次循环，即continue之后的代码将不再执行，但下次循环还是要执行
                 *
                 *  break是结束整个循环,比如
                 *  for(i=1;i<100:i++) if(i=10)break; ......
                 *  这段代码执行到i=10结束整个for循环，i=11后的逻辑将不再执行。
                 *
                 */
                logger.warn("JWT签发IP与此次请求中IP不符，jwt:"+ user.getIP() + "; request:" + IPUtil.getIP(request));
                continue;
            }

            //把这个user保存到request域中，后面要用到。
            request.setAttribute("tokenUser",user);

            /**
             * 直到这步才算验证成功
             * 到此我们确定了
             * 1.这个cookie值name为mark
             * 2.cookie里值不为null
             * 3.本次请求的客户端ip与解析cookie里放入的ip一样
             * 4.token没有过期
             */
            result = true;
        }
        return result;
    }

    // 删除cookie
    public static void clearTokenCookie(HttpServletResponse response, HttpServletRequest request) {
        Cookie cookie = new Cookie("mark",null);
        cookie.setMaxAge(0);
        cookie.setPath(request.getContextPath());
        response.addCookie(cookie);
    }


    // 登录验证成功，签发JWT时对User所含属性标准化    每个token需包含： id  ip  loginTime
    public static User prepareUserToJwt(HttpServletRequest request, String id) {
       if(id==null || "".equals(id)){
           logger.error("传入对象ID为空。位置CookieUtil.prepareUserToJwt()");
       }
       User prepareUser = new User();
       prepareUser.setId(Integer.parseInt(id));    //id
       prepareUser.setIP(IPUtil.getIP(request));   //ip
       prepareUser.setLoginTime(System.currentTimeMillis()+""); // loginTime
       return prepareUser;
    }


}


