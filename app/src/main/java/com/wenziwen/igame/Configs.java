package com.wenziwen.igame;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;

/**
 * Created by ziwen.wen on 2017/4/19.
 */
public class Configs {

    public static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss");
    public static String IMG_PATH = Environment.getExternalStorageDirectory().getPath() + "/Pictures/";
    static {
        IMG_PATH = Environment.getExternalStorageDirectory().getPath() + "/Pictures/";
        File file = new File(IMG_PATH);
        if (!file.exists()) {
            boolean ret = file.mkdir();
            Log.d("Configs", String.format("mkdir %s, result %s", IMG_PATH, ret));
        }
    }
}
