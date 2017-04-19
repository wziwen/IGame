package com.wenziwen.igame.service;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import com.wenziwen.igame.Configs;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by ziwen.wen on 2017/4/19.
 */
public class SaveBitmapJob implements IServiceJob {

    private static final String TAG = "SaveBitmapJob";
    @Override
    public void onCapture(IGameService service, Bitmap bitmap) {
        try {
            String strDate = Configs.DATE_FORMAT.format(new java.util.Date());
            String nameImage = Configs.IMG_PATH + strDate + ".png";
            File fileImage = new File(nameImage);
            if (!fileImage.exists()) {
                fileImage.createNewFile();
                Log.i(TAG, "image file created");
            }
            FileOutputStream out = new FileOutputStream(fileImage);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            Intent media = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(fileImage);
            media.setData(contentUri);
            service.sendBroadcast(media);
            Log.i(TAG, "screen image saved");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
