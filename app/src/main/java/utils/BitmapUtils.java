package utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Administrator on 2017/11/15.
 */

public class BitmapUtils {
    /**
     * 获取一个图片路径，转化为Bitmap
     * @param url
     */
     public static Bitmap getBitmap(String url){
         try {
             FileInputStream fis = new FileInputStream(url);
             return BitmapFactory.decodeStream(fis);
         } catch (FileNotFoundException e) {
             e.printStackTrace();
             return null;
         }

     }

    /**
     * 保存图片为JPEG
     *
     * @param bitmap
     * @param path
     */
    public static String saveJPGE_After(Bitmap bitmap, String path) {
        File file = new File(path);
        try {
            FileOutputStream out = new FileOutputStream(file);
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
                out.flush();
                out.close();
            }
            //回收
            bitmap.recycle();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }

    /**
     * 读取路径中的图片，然后将其转化为缩放后的bitmap
     * @param path
     * return String
     */
    public  static String saveBefore(String path) {
        Log.i("infor", "图片路径(saveBefore):"+path);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // 获取这个图片的宽和高
        try{
            Bitmap bitmap = BitmapFactory.decodeFile(path, options); // 此时返回bm为空
            options.inJustDecodeBounds = false;
            // 计算缩放比
            int be = (int) (options.outHeight / (float) 200);
            if (be <= 0)
                be = 1;
            options.inSampleSize = 8; // 图片长宽各缩小至四分之一
            bitmap = BitmapFactory.decodeFile(path, options);
            return saveJPGE_After(bitmap, path);
        }catch(OutOfMemoryError err){
            err.printStackTrace();
            return null;
        }
    }

    //将图片保存到指定目录下
    public static String savePic(String picturePath,String fullName) {
        //压缩图片
        Bitmap bit= yaSuo(picturePath);
        if(bit==null) {
            return  null;
        }
        //将压缩后的图片写入文件
        String path=savePic(bit,picturePath,fullName);
        return path;

    }

    /**
     * 图片压缩处理
     * @param path
     * @return
     */
    private static Bitmap yaSuo(String path) {
        Log.i("gallery", "图片路径：" + path);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // 获取这个图片的宽和高
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(path, options); // 此时返回bm为空
            options.inJustDecodeBounds = false;
            // 计算缩放比
            int be = (int) (options.outHeight / (float) 200);
            // 我改的缩放比
            // int be = (int) (options.outHeight / (float) 400);
            if (be <= 0)
                be = 1;

            options.inSampleSize = 8; // 图片长宽各缩小至四分之一

            bitmap = BitmapFactory.decodeFile(path, options);
            return bitmap;
        } catch (OutOfMemoryError err) {
            err.printStackTrace();
            return null;
        }
    }



    private static String savePic(Bitmap bit, String picturePath,String fullFileName) {
        // 获取图片类型
        String picType = picturePath.substring(picturePath.lastIndexOf("."), picturePath.length());
        fullFileName += picType;
        // 将bitmap写入文件
        File file = new File(fullFileName);
        FileOutputStream out = null;
        if (file.exists())
            file.delete();
        else {
            try {
                file.createNewFile();
                out = new FileOutputStream(file);
                // 判断图片类型
                if (picType.equals(".jpg")) {
                    bit.compress(Bitmap.CompressFormat.JPEG, 100, out);
                } else if (picType.equals(".png")) {
                    bit.compress(Bitmap.CompressFormat.PNG, 100, out);
                }
                out.flush();
                out.close();

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return fullFileName;
    }


}
