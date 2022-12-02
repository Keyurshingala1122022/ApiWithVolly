package com.example.apiwithvolley;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class BaseActivity extends AppCompatActivity {

    public static final String BASE_URL = "http://192.168.0.108/ask_question_poll/api/public/api/";

    String testEmail = "keyur_shingala3@gmail.com";
    String testPass = "123";

    Gson g = new Gson();

    public byte[] BitmapToByte(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        return stream.toByteArray();
    }

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public void tos(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    public void log(String s) {
        Log.wtf("FAT", s);
    }


    public File saveBitmap(Bitmap bitmap) {
        File myDir = new File(getFilesDir(), getString(R.string.app_name));

        if (!myDir.exists()) myDir.mkdirs();

        String fileName = System.currentTimeMillis() + ".jpeg";

        File file = new File(myDir, fileName);
        if (file.exists()) file.delete();

        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

//            MediaScannerConnection.scanFile(this, new String[]{file.getPath()}, new String[]{"image/jpeg", "image/png"}, null);
        } catch (Exception e) {
            Log.wtf("FATZ", e.getMessage());
        }
        return file;
    }
}

