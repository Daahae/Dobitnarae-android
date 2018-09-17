package com.example.dobitnarae.QRCode;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;

public class CreateQRCode {
    public void createQRCode(String context, String name){

        Bitmap bitmap = null ;

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            /* Encode to utf-8 */
            Hashtable hints = new Hashtable();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");

            BitMatrix bitMatrix = multiFormatWriter.encode(context, BarcodeFormat.QR_CODE,300,300, hints);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            bitmap = barcodeEncoder.createBitmap(bitMatrix);



        } catch (WriterException e) {
            e.printStackTrace();
        }
        saveBitmaptoJpeg(bitmap, name);
    }

    public void saveBitmaptoJpeg(Bitmap bitmap, String name){
        String ex_storage = Environment.getExternalStorageDirectory().getAbsolutePath();
        // Get Absolute Path in External Sdcard
        String foler_name = "/Dobitnarae/QRcode/";
        String file_name = name+".jpg";
        String string_path = ex_storage+foler_name;

        File file_path;
        try{
            file_path = new File(string_path);
            if(!file_path.isDirectory()){
                file_path.mkdirs();
            }
            FileOutputStream out = new FileOutputStream(string_path+file_name);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();

        }catch(FileNotFoundException exception){
            Log.e("FileNotFoundException", exception.getMessage());
        }catch(IOException exception){
            Log.e("IOException", exception.getMessage());
        }
    }
}
