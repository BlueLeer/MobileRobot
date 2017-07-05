package android.study.leer.mobilerobot.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**用于将简单的字符串加密的工具,同时将加密的数据进行加盐
 * Created by Leer on 2017/2/17.
 */

public class MD5util {
    public static String encoder(String s){
        StringBuilder sb = new StringBuilder();
        try {
            //获取MD5加密的实例
            MessageDigest digest = MessageDigest.getInstance("MD5");
            //将MD5要加密的字符串转化为byte数组,然后交给MD5加密工具进行加密
            //这一步,加密工具自动将字符串数组转化成16个字符
            byte[] bs = digest.digest(s.getBytes());
            for(byte b:bs){
                //MD5加密是将字符串转化为16个byte的元素,但是需要将16位拓展到32
                //按位"与"的操作
                int a = b & 0xff;
                String c = Integer.toHexString(a);
                if(c.length()<2){
                    c = "0" + c;
                }
                sb.append(c);
            }

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
