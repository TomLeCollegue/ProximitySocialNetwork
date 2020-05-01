package com.example.proximitysocialnetwork;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

public class ImageGestion {

    public void compressImageToJpeg(Bitmap bitmap, int quality, OutputStream outputStream){
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
        }

    public String imageToString(Bitmap bitmap){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        compressImageToJpeg(bitmap,35, outputStream);
        byte[] imageBytes = outputStream.toByteArray();

        String encodedImage = Base64.encodeToString(imageBytes,Base64.DEFAULT );
        return encodedImage;
    }

    public Bitmap getBitmapFromDrawable(BitmapDrawable bitmapDrawable){
       Bitmap bitmap = bitmapDrawable.getBitmap();
        return bitmap;
    }

}

