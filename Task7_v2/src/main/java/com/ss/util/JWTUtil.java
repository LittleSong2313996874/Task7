package com.ss.util;

import com.auth0.jwt.JWTSigner;

import com.auth0.jwt.JWTVerifier;

import com.auth0.jwt.internal.com.fasterxml.jackson.databind.ObjectMapper;
import com.ss.pojo.User;
import com.ss.pojo.exception.ForbiddenException;
import com.ss.pojo.exception.UnavailableException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import java.util.Map;

public class JWTUtil {

    private static final String SECRET = "XX#$%dsafag3456*&MQ/*gfLMNQNQJQK sdfkjsdrow32234545fdf>?N<:{LWPW";

    private static final String EXP = "exp";

    private static final String PAYLOAD = "payload";

    //加密，传入一个对象和有效期

    public static <T> String sign(T object, long maxAge) {

        try {

            final JWTSigner signer = new JWTSigner(SECRET);

            final Map<String, Object> claims = new HashMap<String, Object>();

            ObjectMapper mapper = new ObjectMapper();

            String jsonString = mapper.writeValueAsString(object);
            System.out.println(jsonString);
            claims.put(PAYLOAD, jsonString);

            claims.put(EXP, System.currentTimeMillis() + maxAge);

            // 如果没有指定算法，默认用 HS256
            return signer.sign(claims);

        } catch(Exception e) {

            UnavailableException e1 = new UnavailableException("JWT加密失败");
            /**
             * initCause()这个方法可以对较底层异常进行包装
             *
             * 这样的话，比如后面我们通过上层异常A无法了解到底层异常B的情况
             *
             * 可以再通过A.getCause() 得到导致A异常的原始B异常
             */
            e1.initCause(e);
            throw e1;

        }

    }

    //解密，传入一个加密后的token字符串和解密后的类型
    public static <T> T unsign(String jwt, Class<T> classT) {
        final JWTVerifier verifier = new JWTVerifier(SECRET);
        final Map<String, Object> claims;
        try {
            // 如果 token里的信息被人改动(哪怕一个字符)，将验证失败抛出异常
            claims = verifier.verify(jwt);
        } catch (Exception e) {
            throw new ForbiddenException("非法Token");
        }
        if (claims.containsKey(EXP) && claims.containsKey(PAYLOAD)) {
            long exp = (Long) claims.get(EXP);
            long currentTimeMillis = System.currentTimeMillis();
            if (exp > currentTimeMillis) {
                System.out.println("还有"+(exp-currentTimeMillis)/60000+"分钟过期");
                String json = (String) claims.get(PAYLOAD);
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    // 验证成功返回 pojo 类
                    return objectMapper.readValue(json, classT);
                } catch (IOException e) {
                    throw new UnavailableException("转化为实体类失败：" + classT.getSimpleName());
                }
            }else {
               // 如果token过期了，返回null;
               return null;
            }
        }
        throw new ForbiddenException("非法token");
    }

    // 测试
    public static void main(String[] args) {

        User user = new User();
        user.setUsername("路飞飞");
        String token = JWTUtil.sign(user,1000*60*5);
        //System.out.println(token);
        String tem = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE1Mjg1NzYwNTkxOTAsInBheWxvYWQiOiJ7XCJpZFwiOm51bGwsXCJ1c2VybmFtZVwiOlwi57u05aSa5Yip5LqaXCIsXCJwYXNzd29yZFwiOlwiXCIsXCJjcmVhdGVBdFwiOm51bGwsXCJ1cGRhdGVBdFwiOm51bGwsXCJwaG9uZVwiOm51bGwsXCJlbWFpbFwiOm51bGwsXCJwb3J0cmFpdFwiOm51bGwsXCJ0ZW1jb2RlXCI6bnVsbCxcImlwXCI6XCIxMjcuMC4wLjFcIn0ifQ.CZAvjgxFg4Gv_0If9ouoSIXorPD9TtFRkVYMmb0mVQU";
        System.out.println(JWTUtil.unsign(tem,user.getClass()));

    }


}

