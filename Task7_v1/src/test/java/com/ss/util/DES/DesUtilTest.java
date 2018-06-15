package com.ss.util.DES;

import com.ss.cache.RedisCache;
import com.ss.pojo.Student;
import com.ss.util.DateUtil;
import com.ss.util.third_party_api.RandomUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class DesUtilTest {

    @Autowired
    RedisCache redisCache;

    @Test
    public void testMd5() {
        String fileName="J03.png";
        /*substring(int beginIndex)
        返回一个新的字符串，它是此字符串的一个子字符串。该子字符串从指定索引处的字符开始，直到此字符串末尾。*/
        String prefix=fileName.substring(fileName.lastIndexOf("."));
        System.out.println(prefix);

    }

    @Test
    public void testMd52() {
        String s = "";   // e8dc4081b13434b45189a720b77b6818    维多利亚123456  db68e8d80ac3ac0862770e4a8d658ed8

        System.out.println(s);
        s = Md5Util.string2MD5(s);
        System.out.println(s);


    }






    @Test
    public void testString() throws Exception{

        final Base64.Decoder decoder = Base64.getDecoder();
        final Base64.Encoder encoder = Base64.getEncoder();
        final String text = "abc";
        final byte[] textByte = text.getBytes("UTF-8");
//编码
        final String encodedText = encoder.encodeToString(textByte);
        System.out.println(encodedText);
//解码
        System.out.println(new String(decoder.decode(encodedText), "UTF-8"));



    }


    @Test
    public void test0524() throws Exception{

        JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost");

        Jedis jedis = new Jedis("39.105.68.241",8084);
        jedis.auth("woshixiaokeai");
        //jedis.set("mark1", "Thank you,感谢你");
        String value = jedis.get("mark1");
        System.out.println(value);

    }

   /* @Test
    public void test3() throws Exception{


        Jedis jedis = new Jedis("0.0.0.0",0000);
        jedis.auth("123456");
        jedis.set("foo", "你i你好哈吼啊吼啊啊啊");
        String value = jedis.get("foo");
        System.out.println(value);

    }*/

   // 测试 secureRandom
    @Test
    public void test3() throws Exception{

        System.out.println(  RandomUtil.getRandom(6)  );


    }
    @Test
    public void test4() throws Exception{
        Integer p = 10000;
        Integer q = 10000;
        System.out.println(p == q);     // false  这里比的是内存地址，因为q 和p 是两个对象，在内存中地址不一样
        System.out.println(p.equals(q));  //true


    }

}