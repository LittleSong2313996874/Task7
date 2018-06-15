package com.ss.util.interceptor;

import com.ss.pojo.exception.ForbiddenException;
import com.ss.util.CookieUtil;
import com.ss.util.DES.DesUtil;
import com.ss.util.testConvert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * 对于密钥的处理，我的方法是每个用户访问都产生一个随机密钥，并保存在各自的session中
 * 但是看到别的人做法，是初始化一次密钥然后保存即可，以后的加密解密都用这一个密钥
 * 这样也是一个可选的方法，声明一个全局变量报错密钥即可。
 * 也可以放在静态代码块中，在服务器启动时加载一次生成一个密钥。
 */

public class UserLoginInterceptor implements HandlerInterceptor {

    Logger logger = LoggerFactory.getLogger(UserLoginInterceptor.class);


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        /**
         * preHandle方法是进行处理器拦截用的，顾名思义，该方法将在Controller处理之前进行调用，
         * SpringMVC中的Interceptor拦截器是链式的，可以同时存在多个Interceptor，
         * 然后SpringMVC会根据声明的前后顺序一个接一个的执行，而且所有的Interceptor中的preHandle方法都会在
         * Controller方法调用之前调用。SpringMVC的这种Interceptor链式结构也是可以进行中断的，这种中断方式是
         * 令preHandle的返回值为false，当preHandle的返回值为false的时候整个请求就结束了。
         */

        logger.info("进入拦截器：preHandle");
        String path = request.getRequestURI();
        logger.info("request.getRequestURI(): "+path);
        if(path.equals(request.getContextPath()+"/u/login")){
            return true;
        }
        if (CookieUtil.checkTokenCookie(request,response)) {
            return true;
        }
        // 转发到登录页面
        request.getRequestDispatcher("/login").forward(request, response);
        return false;

    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object o, ModelAndView modelAndView) throws Exception {

        /**
         * 这个方法只会在当前这个Interceptor的preHandle方法返回值为true的时候才会执行。
         * postHandle是进行处理器拦截用的，它的执行时间是在处理器进行处理之
         *后，也就是在Controller的方法调用之后执行，但是它会在DispatcherServlet进行视图的渲染之前执行，
         * 也就是说在这个方法中你可以对ModelAndView进行操
         * 作。这个方法的链式结构跟正常访问的方向是相反的，也就是说先声明的Interceptor拦截器该方法反而会后调用，
         */


    }

    /**
     * 该方法也是需要当前对应的Interceptor的preHandle方法的返回值为true时才会执行。
     * 该方法将在整个请求完成之后，也就是DispatcherServlet渲染了视图执行，
     * 这个方法的主要作用是用于清理资源的，
     * 当然这个方法也只能在当前这个Interceptor的preHandle方法的返回值为true时才会执行。
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object o, Exception e) throws Exception {

    }
}
