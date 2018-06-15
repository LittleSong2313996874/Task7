package com.ss.util.third_party_api;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class EmailUtil {
    final static String url = "http://api.sendcloud.net/apiv2/mail/send";
    final static String apiUser = "LittleSong_test_DQwvX8";
    final static String apiKey = "rpPG1gg9459WFF0n";

    private static final Logger logger = LoggerFactory.getLogger(EmailUtil.class);

    public static boolean sendSingleEmail(String email, String msg ){

        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpPost httPost = new HttpPost(url);

        List params = new ArrayList();
        // 您需要登录SendCloud创建API_USER，使用API_USER和API_KEY才可以进行邮件的发送。
        params.add(new BasicNameValuePair("apiUser", apiUser));
        params.add(new BasicNameValuePair("apiKey", apiKey));
        params.add(new BasicNameValuePair("from", "service@sendcloud.im"));
        params.add(new BasicNameValuePair("fromName", ""));
        params.add(new BasicNameValuePair("to", email));
        params.add(new BasicNameValuePair("subject", "LS萌萌哒集团邮件"));
        // params.add(new BasicNameValuePair("html", "欢迎注册LS！您的验证码为"+code+",10分钟内输入有效。"));
        params.add(new BasicNameValuePair("html", msg));

        try {
            httPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // 请求
        HttpResponse response = null;
        try {
            response = httpclient.execute(httPost);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 处理响应
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) { // 正常返回
            // 读取xml文档
            String result = null;
            try {
                result = EntityUtils.toString(response.getEntity());
            } catch (IOException e) {
                e.printStackTrace();
            }
            //logger.info("邮件发送成功");
            //System.out.println(result);
            httPost.releaseConnection();
            return true;

        } else {
            logger.error("error");
            httPost.releaseConnection();
            return false;
        }
    }

    public static boolean sendSingleEmailCode(String email, String code ){
        String msg = "欢迎登录LS！您的验证码为 <b>  "+code+" </b> ,10分钟内输入有效。";
        return sendSingleEmail(email,msg);
    }

    public static boolean sendSingleEmailLink(String email, String link ){
        // "<h2>注册成功 点击<a href='https://www.baidu.com'>立即跳转</a></h2>"
        String msg = "<h4>您正在进行绑定邮箱操作，请点击此链接完成激活：<br/><b> <a href='" + link + "'>点我激活</a>" +
                "</b> <br/>链接10分钟内有效</h4>";
        return sendSingleEmail(email,msg);
    }


}
