package com.ss.service;

import com.ss.Dao.VocationDao;
import com.ss.cache.RedisCache;
import com.ss.pojo.PageBean;
import com.ss.pojo.Student;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class StudentServiceTest {

    @Autowired
    StudentService stuService;
    @Resource
    VocationDao vocationDao;

    @Resource
    RedisCache redisCache;

    @Autowired
    RedisTemplate redisTemplate;

    @Test
    public void getSize() {
        int size =stuService.getSize();
        System.out.println(size);
    }

    @Test
    public void findById() {
        redisCache.setKeyStringWithExpireTime("18829787856",132456+"",600L);

    }

    @Test
    public void onWorkSize() {
        System.out.println(stuService.onWorkSize());
    }


    @Test
    public void cacheTest() {
       // redisCache.setKeyStringWithExpireTime("127.0.0.1","2",600L);
        // 经测试：原子加一并不会改变有效时间
        redisCache.incrOne("127.0.0.2");
    }

    @Test
    public void findByIdPage() {

        redisCache.deleteCache("127.0.0.1phonecode");

    }

    @Test
    public void test01() {
        Student student = new Student();
        student.setINTID(2637);

        student.setP_Name("赶紧完成任务6啊");
        int i = stuService.updateStudent(student);
        System.out.println(i);


    }



}