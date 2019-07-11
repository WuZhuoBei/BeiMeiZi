package com.example.beimeizi.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PictureLoader {
    private ImageView loadImg;
    private String imgUrl;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.obj != null ){
                byte[] bytes = (byte[]) msg.obj;
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                loadImg.setImageBitmap(bitmap);
            }
        }
    };



    public void load(ImageView loadImg,String imgUrl){
        this.loadImg = loadImg;
        this.imgUrl = imgUrl;
        Drawable drawable = loadImg.getDrawable();
        if(drawable != null && drawable instanceof BitmapDrawable){
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            if(bitmap != null && !bitmap.isRecycled()){
                //bitmap.isRecycled();用于检查位图是否被回收l
                bitmap.recycle();;  //位图回收
            }
        }

        new Thread(runable).start();
    }

    Runnable runable = new Runnable() {
        @Override
        public void run() {
            try {
                //获取链接
                URL url = new URL(imgUrl);
                //打开网络连接
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                //设置请求参数
                conn.setRequestMethod("GET");
                conn.setReadTimeout(10000);
                conn.connect();
                //响应码为200则请求成功
                if(conn.getResponseCode() == 200){
                    //获取服务器输入流
                    InputStream in = conn.getInputStream();
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    byte[] bytes = new byte[1024];
                    int length = -1;
                    while ((length=in.read(bytes))!=-1){
                        out.write(bytes,0,length);
                    }
                    byte[] picByte = out.toByteArray();
                    in.close();
                    out.close();
                    conn.disconnect();
                    Message msg = Message.obtain();
                    msg.obj = picByte;
                    handler.sendMessage(msg);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };














}
