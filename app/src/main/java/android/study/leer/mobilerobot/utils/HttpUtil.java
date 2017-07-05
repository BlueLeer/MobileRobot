package android.study.leer.mobilerobot.utils;

import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Leer on 2017/1/3.
 */

public class HttpUtil {
    public static final String TAG = "HttpUtil";

    /**发送一个http请求,然后返回请求所得的结果
     * @return  http请求结果
     */
    public static String getResponse(final String stringUrl) {

        HttpURLConnection connection = null;
        InputStream in = null;
        try {
            URL url = new URL(stringUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            if(connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                in = connection.getInputStream();
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();

                byte[] bytes = new byte[1024];
                while(in.read(bytes) > 0){
                    buffer.write(bytes,0,bytes.length);
                }

                return buffer.toString().trim();
            }

        }catch (Exception e){
            e.printStackTrace();
            Log.e(TAG,"连接出错啦.....");
        }finally {
            try {
                if(connection != null) {
                    connection.disconnect();
                }
                if(in != null){
                    in.close();
                }
            }catch (Exception e){
                e.printStackTrace();
                Log.e(TAG,"关闭网络连接时出错!");
            }
        }
        return null;
    }

    /**服务器端返回的字符串,从中读取版本信息
     * @param responseString
     * @return
     */
    public static String[] getVersionInfo(String responseString){

        try {
            JSONObject jsonObject = new JSONObject(responseString);
            String versionCode = jsonObject.getString("versionCode");
            String versionName = jsonObject.getString("versionName");
            String versionDes = jsonObject.getString("versionDes");
            String downloadUrl = jsonObject.getString("downloadUrl");
            String[] versionInfo = new String[]{versionCode,versionName,versionDes,downloadUrl};

            return versionInfo;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}
