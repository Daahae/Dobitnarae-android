package com.example.dobitnarae;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

public  class ServerImg  extends AsyncTask<String, Integer,Bitmap>{// 서버에 이미지를 bitmap형식으로 뿌리기
    Bitmap bmImg;

    @Override
    protected Bitmap doInBackground(String... urls) {
        // TODO Auto-generated method stub
        try{
            URL myFileUrl = new URL(urls[0]);
            HttpURLConnection conn = (HttpURLConnection)myFileUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();

            InputStream is = conn.getInputStream();
            bmImg = BitmapFactory.decodeStream(is);
        }catch(IOException e){
            e.printStackTrace();
        }
        return bmImg;
    }

    // 상점 이미지 가져오기
    public static void getStoreImageGlide(Context context, int storeID, ImageView imageView){
        Glide.with(context)
                .load("http://13.125.232.225/store/" + storeID +".jpg")
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .fitCenter()
                        .error(R.mipmap.ic_launcher))
                .into(imageView);
    }

    // 옷 이미지 가져오기
    public static void getClothesImageGlide(Context context, int clothesID, ImageView imageView){
        Glide.with(context).load("http://13.125.232.225/cloth/" + clothesID +".jpg")
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .fitCenter()
                        .error(R.mipmap.ic_launcher))
                .into(imageView);
    }

    public static Bitmap getStoreImage(int storeID){

        String str = ""+storeID;
        Bitmap BM = null;
        try {
            ServerImg SI = new ServerImg();
            BM = SI.execute("http://13.125.232.225/store/" + str+".jpg").get();

        }catch(Exception E){
            E.printStackTrace();
        }
        return BM;
    }
    public static Bitmap getClothImage(int clothID){
        String str = ""+clothID;
        Bitmap BM = null;
        try {
            ServerImg SI = new ServerImg();
            BM = SI.execute("http://13.125.232.225/cloth/" + str+".jpg").get();
        }catch(Exception E){
            E.printStackTrace();
        }
        return BM;
    }



}

