package com.ss.util;

import javax.servlet.http.HttpServletRequest;

/**
 * 用于获得客户端的真实IP
 *
 */
public class IPUtil {

    //经过下面的拼装，得到根目录 如 http://127.0.0.1:8080/contextpath
    public static String getBasePath(HttpServletRequest request){
        StringBuffer sb = new StringBuffer();
        sb.append(request.getScheme()); //返回当前链接使用的协议；一般返回http或https;
        sb.append("://");
        sb.append(request.getServerName()); // 获取请求URL上的主机名
        sb.append(":");
        sb.append(request.getServerPort());  //获得端口号，如8080
        sb.append(request.getContextPath());  // 获得应用名
        return sb.toString();
    }

    //反向代理下获取用户ip   这里需要你在Nginx上做相关配置的。
    public static String getIP(HttpServletRequest request){
        if (request.getHeader("x-forwarded-for") == null) {
            return request.getRemoteAddr();
        } else {
            return request.getHeader("x-forwarded-for");
        }
    }

    //判断是否是黑名单里面的ip,待完善
    public static boolean blackList(){
        return false;
    }

    //添加黑名单，待完善

}
