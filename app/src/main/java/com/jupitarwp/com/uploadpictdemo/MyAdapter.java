package com.jupitarwp.com.uploadpictdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import utils.BitmapUtils;

/**
 * Created by Administrator on 2017/11/15.
 */

public class MyAdapter extends BaseAdapter {
    private Context context;
    private List<String> imgPath=new ArrayList<>();

    public MyAdapter(Context context, List<String> imgPath) {
        this.context=context;
        this.imgPath=imgPath;
    }

    @Override
    public int getCount() {
        return imgPath.size();
    }

    @Override
    public String  getItem(int position) {
        return imgPath.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=null;
        if(convertView==null){
            convertView =  LayoutInflater.from(context).inflate(R.layout.grid_item, null);
            viewHolder=new ViewHolder();
            viewHolder.img=(ImageView) convertView.findViewById(R.id.pic);
            viewHolder.delete_img=(ImageView)convertView.findViewById(R.id.delete_pic);
            convertView.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) convertView.getTag();
        }
        Bitmap  bitmap=BitmapUtils.getBitmap(imgPath.get(position));
        if(bitmap!=null){
            viewHolder.img.setImageBitmap(BitmapUtils.getBitmap(imgPath.get(position)));
        }else{
            Log.i("infor","kong ");
        }

        //删除图片的点击事件
        viewHolder.delete_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgPath.remove(position);
                notifyDataSetChanged();
            }
        });
        return convertView;
    }



    class ViewHolder{
        ImageView img;
        ImageView delete_img;
    }
}
