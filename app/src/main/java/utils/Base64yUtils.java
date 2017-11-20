package utils;

import android.util.Base64;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by Administrator on 2017/11/17.
 */

public class Base64yUtils {
    public static String getimgString(List<String> imgPath){
        StringBuilder sb=new StringBuilder();
        byte[] data ;
        for(String path:imgPath){
            InputStream in = null;
            data = null;
        //读取图片字节数组
            try  {
                in = new FileInputStream(path);
                data = new byte[in.available()];
                in.read(data);
                in.close();
            }
            catch (IOException e){
                e.printStackTrace();
            }
            //对字节数组Base64编码
            byte[] encode = Base64.encode(data,Base64.DEFAULT);
            sb.append(new String(encode)).append(",");
        }
        return sb.toString();


    }
}
