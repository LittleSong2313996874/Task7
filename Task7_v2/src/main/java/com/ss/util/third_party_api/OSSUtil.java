package com.ss.util.third_party_api;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.ss.pojo.exception.UnavailableException;
import com.ss.util.DateUtil;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Date;

public class OSSUtil {

    //  endpoint见  https://oss.console.aliyun.com/bucket/oss-cn-beijing/image-ls/overview

    private static String endpoint = "oss-cn-beijing.aliyuncs.com"; // oss-cn-beijing.aliyuncs.com
    private static String accessKeyId = "LTAIVJzt1ZhVHhNU";
    private static String accessKeySecret = "444esbFgLsmWHGEZKBxHFIURCRq7r7";

    private static String bucketName = "image-ls";
    // 创建OSSClient实例。
    private static OSSClient ossClient = new OSSClient(endpoint, accessKeyId,accessKeySecret);

    // 上传图片
    public static void upload(MultipartFile file, String key){

        try {
            System.out.println(file.getOriginalFilename()); //可以获取文件原名
            ossClient.putObject(bucketName, key, new ByteArrayInputStream(file.getBytes()));
        } catch  (IOException e){
            e.getMessage();
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message: " + oe.getErrorCode());
            System.out.println("Error Code:       " + oe.getErrorCode());
            System.out.println("Request ID:      " + oe.getRequestId());
            System.out.println("Host ID:           " + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message: " + ce.getMessage());
        }
        /*finally {
            if (ossClient != null) {
                // 关闭
                ossClient.shutdown();
            }
        }*/

    }

    // 生成图片在OSS服务器上的key值  格式： 路径/用户名_时间戳 + 文件后缀
    public static String getImgKey(String userName, String suffix){
        StringBuffer sb = new StringBuffer();
        //下面拼接的相当于是key值，key对应于oss服务器中文件的路径 举个例子：portrait/user1/20180101000000.jpg
        sb.append("portrait/");
        sb.append(userName);
        sb.append("_");
        sb.append(DateUtil.longToString(System.currentTimeMillis(), "yyyyMMddHHmmss"));
        sb.append(suffix);
        return sb.toString();
    }

    //获得图片的访问地址   适用于公有bucket
    public static String getImgUrl(String key){
        StringBuffer sb = new StringBuffer();
        sb.append("http://");
        sb.append(bucketName);
        sb.append(".");
        sb.append(endpoint);
        sb.append("/");
        sb.append(key);
        //将图缩略成宽度为200，高度为200，按长边优先,拼接在url后面就行
        sb.append("?x-oss-process=image/resize,m_lfit,h_200,w_200");
        return sb.toString();

        // https://image-ls.oss-cn-beijing.aliyuncs.com/main/portrait/%E6%8D%95%E8%8E%B7.PNG?Expires=1528388552&OSSAccessKeyId=TMP.AQEyCOl7g8cK_OlhPnsAL3mrw-5GAus6JfDRbZr_DwBMvuR-V8ClFz0gJw00MC4CFQCaGMvmdcNGpRD52s1K2-6XhkPbUQIVALAWeDtjXVTcx9dI7dgQdq9auOYN&Signature=AlApE51meJBQh%2FWx1YYWFBmm0dQ%3D
    }


    //获得图片的访问地址   适用于私有bucket
    // 这里传进来的key是图片路径，不是密钥
    public static String getImgUrlPrivate(String key){

        // 图片处理样式
        //长宽200
        String style = "image/resize,m_lfit,h_200,w_200";
        //长宽100，90度旋转
        /*String style = "image/resize,m_fixed,w_100,h_100/rotate,90";*/
        // 过期时间1天
        Date expiration = new Date(new Date().getTime() + 1000 * 60 * 60 * 24 );
        GeneratePresignedUrlRequest req = new GeneratePresignedUrlRequest(bucketName, key, HttpMethod.GET);
        req.setExpiration(expiration);
        req.setProcess(style);

        URL signedUrl = ossClient.generatePresignedUrl(req);

        return signedUrl.toString();
    }

}
