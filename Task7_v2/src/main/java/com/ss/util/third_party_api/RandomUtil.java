package com.ss.util.third_party_api;

import com.ss.pojo.exception.UnavailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Random是伪随机，产生的随机数会重复
 * SecureRandom更安全，它的种子(seed)是随机的。
 */
public class RandomUtil {
    private static SecureRandom secureRandom;
    private static final Logger logger = LoggerFactory.getLogger(RandomUtil.class);

    static {
        try {
            secureRandom = SecureRandom.getInstance("SHA1PRNG");
            //这个类生成随机数，首次调用性能比较差，如果条件允许最好服务启动后先调用一下nextInt()
            secureRandom.nextInt();
        } catch (NoSuchAlgorithmException e) {
            logger.error("RandomUtil did not work!");
        }
    }

    /*
     * 入参为随机数位数，应该为1到8位*/
    public static String getRandom(int size) {
        if (size < 1) {
            throw new UnavailableException("随机数位数必须大于0，本次大小：" + size);
        }
        if (size > 8) {
            throw new UnavailableException("随机数位数最大为8位，本次大小：" + size);
        }
        //bound表示最大限制
        /*
        *Math.pow(底数,几次方) double c=Math.pow(2,3); c 为 8 。
        */
        int bound = (int) Math.pow(10, size);
        // 产生一个10的N次方的随机数
        String random = String.valueOf(secureRandom.nextInt(bound));
        //如果生成的随机数位数不够，前面加0补足
        for (int i = random.length(); i < size; i++) {
            random = "0" + random;
        }
        return random;
    }
}
