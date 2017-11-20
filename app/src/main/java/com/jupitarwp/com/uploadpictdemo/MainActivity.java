package com.jupitarwp.com.uploadpictdemo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.solver.Cache;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import utils.Base64yUtils;
import utils.BitmapUtils;
import utils.Constance;

public class MainActivity extends AppCompatActivity {
    private GridView gridView;
    private MyAdapter myAdapter;
    private Button takPic, choosePic, uploadPic;
    //图片路径
    private String fullFileName = "";
    private final int TAKEPICTURE = 1;
    private final int CHOOSEPICTURE = 2;
    private final int REQUESTPERMISSION = 3;
    //设置最多传递5张照片
    private final int picNumber = 10;
    //保存图片路径
    private List<String> imgPath = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iniView();
        setListener();
        checkPermission();
    }


    private void iniView() {
        gridView = (GridView) findViewById(R.id.gridView);
        myAdapter = new MyAdapter(getApplicationContext(), imgPath);
        gridView.setAdapter(myAdapter);
        takPic = (Button) findViewById(R.id.takPic);
        choosePic = (Button) findViewById(R.id.choosePic);
        uploadPic = (Button) findViewById(R.id.uploadPic);
    }

    private void setListener() {
        //拍照
        takPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imgPath.size() == picNumber) {
                    Toast.makeText(getApplicationContext(), "亲，一次最多上传10张图片!", Toast.LENGTH_SHORT).show();
                    return;
                }
                takePic();
            }
        });

        //从相册选择图片
        choosePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imgPath.size() == picNumber) {
                    Toast.makeText(getApplicationContext(), "亲，一次最多上传10张图片!", Toast.LENGTH_SHORT).show();
                    return;
                }
                chooseFromAblum();

            }
        });

        //图片上传
        uploadPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imgPath.size() == 0) {
                    Toast.makeText(getApplicationContext(), "亲，请选择图片再上传！", Toast.LENGTH_SHORT).show();
                    return;

                }
                //开启线程上传图片
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        upload(imgPath, Constance.UPLOADPATH);
                    }
                }).start();


            }
        });
    }

    /**
     * 上传图片
     */
    private void upload(List<String> imgPath, String requestURL) {
        final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/jpeg");
        File sdcache = getExternalCacheDir();
        int cacheSize = 10 * 1024 * 1024;
        //设置超时时间及缓存
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
//                .connectTimeout(15, TimeUnit.SECONDS)
//                .writeTimeout(20, TimeUnit.SECONDS)
//                .readTimeout(20, TimeUnit.SECONDS);



        OkHttpClient mOkHttpClient=builder.build();

        MultipartBody.Builder mbody=new MultipartBody.Builder().setType(MultipartBody.FORM);

  int i=0;
        for(String path:imgPath) {
            File file=new File(path);

            if(file.exists()) {
                mbody.addFormDataPart(i+"",file.getName(),RequestBody.create(MEDIA_TYPE_PNG,file));
            }
        }



        RequestBody requestBody =mbody.build();
        Request request = new Request.Builder()
                .url(requestURL)
                .post(requestBody)
                .build();

        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i("infor", response.body().string());
            }
        });
    }





    /**
     * 权限检测
     */
    private void checkPermission() {
        if (Build.VERSION.SDK_INT < 23) {
//           takePic();
            return;
        }
        List<String> permissionList = new ArrayList<String>();
        //读sd卡权限
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        //写sd卡权限
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        //检测相机权限
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.
                CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(android.Manifest.permission.CAMERA);
        }
        if (permissionList.size() > 0) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(this,
                    permissions, REQUESTPERMISSION
            );

        } else {
            Log.i("infor", "hahhahha");
//            takePic();
        }


    }

    //拍照
    private void takePic() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmssSSS");
        String curTime = sdf.format(new Date());
        String picName = "camera" + "_" + curTime + ".jpg";
        fullFileName = getPicPath() + File.separator + picName;
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_MEDIA_TITLE, "拍照");
        intent.putExtra(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        Log.i("infor", "完整图片路径为takePic():" + fullFileName);
        Uri uri;
        if (Build.VERSION.SDK_INT >= 24) {
            uri = FileProvider.getUriForFile(getApplicationContext(), "com.jupitarwp.com.testh5android.fileprovider", new File(fullFileName));
        } else {
            uri = Uri.fromFile(new File(fullFileName));
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, TAKEPICTURE);


    }

    //从相册选择图片
    private void chooseFromAblum() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmssSSS");
        String curTime = sdf.format(new Date());
        String picName = "gallery" + "_" + curTime;
        fullFileName = getPicPath() + File.separator + picName;
        // 调用android自带的图库
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, CHOOSEPICTURE);
    }

    private String getPicPath() {
        boolean sdCardExist = Environment.getExternalStorageState().
                equals(android.os.Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            String targetDir = null;
            String sdDir = Environment.getExternalStorageDirectory().getAbsolutePath();
            targetDir = sdDir + "/" + "henan";
            File file = new File(targetDir);
            if (!file.exists()) {
                file.mkdirs();
            }
            Log.i("infor", "保存文件路径为(getPicPath()):" + targetDir);
            return targetDir;
        } else {
            Toast.makeText(this, "sd卡不存在!",
                    Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                for (int request : grantResults) {
                    if (request != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(getApplicationContext(), "请授予完整的权限!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
//                takePic();
                break;

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1:
                    showImg();
                    break;
                case 2:
                    if (data != null) {
                        galleryResult(data, fullFileName);
                    }
                    break;
            }
        }
    }

    //获取拍照的图片
    private void showImg() {
        String picPath = BitmapUtils.saveBefore(fullFileName);
        imgPath.add(picPath);
        myAdapter.notifyDataSetChanged();
        for (String path : imgPath)
            Log.i("infor", "showImg()保存的图片路径有:" + path);
    }

    //获取系统相册的图片
    private void galleryResult(Intent data, String fullFileName) {
        Uri selectedImage = data.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        if (picturePath.equals(""))
            return;
        // 将图片保存到指定目录下。
        String path = BitmapUtils.savePic(picturePath, fullFileName);
        if (path != null) {
            imgPath.add(path);
            myAdapter.notifyDataSetChanged();
        }


    }


}
