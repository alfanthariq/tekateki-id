package com.alfanthariq.tts.rest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Created by alfanthariq on 17/01/2018.
 */

public class RestTools {
    private static String TAG = "RestTool";
    public static String MD5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }

    public static String resizeBase64Image(String base64image){
        byte [] encodeByte= Base64.decode(base64image.getBytes(),Base64.DEFAULT);
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inPurgeable = true;
        Bitmap image = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length,options);

        if(image.getHeight() <= 400 && image.getWidth() <= 400){
            return base64image;
        }
        image = Bitmap.createScaledBitmap(image, 500, 250, false);

        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG,100, baos);

        byte [] b=baos.toByteArray();
        System.gc();
        return Base64.encodeToString(b, Base64.NO_WRAP);
    }

    public static Bitmap getImageIcon(String base64Image) {
        if(base64Image!=null) {
            byte[] image_data = Base64.decode(base64Image, Base64.NO_WRAP);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.outHeight = 200; //32 pixles
            options.outWidth = 500; //32 pixles
            options.outMimeType = "image/png"; //this could be image/jpeg, image/png, etc

            return BitmapFactory.decodeByteArray(image_data, 0, image_data.length, options);
        }
        return null;
    }
}
