package com.wenziwen.igame;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.wenziwen.igame.service.IGameService;
import com.wenziwen.igame.util.EventInject;


public class MainActivity extends ActionBarActivity {
    private String TAG = "Service";
    private int result = 0;
    private Intent intent = null;
    private int REQUEST_MEDIA_PROJECTION = 1;
    private MediaProjectionManager mMediaProjectionManager;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMediaProjectionManager = (MediaProjectionManager)getApplication().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        //startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION);
        //IGameService.mMediaProjectionManager1 = mMediaProjectionManager;
        //((ShotApplication)getApplication()).setMediaProjectionManager(mMediaProjectionManager);
        //Log.i(TAG, "start screen capture intent");

        //finish();

        //Intent intent = new Intent(getApplicationContext(), IGameService.class);
        //startService(intent);

        startIntent();

        // 初始化时间注入
        EventInject.getInstance();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void startIntent(){
        if(intent != null && result != 0){
            Log.i(TAG, "user agree the application to capture screen");
            //IGameService.mResultCode = resultCode;
            //IGameService.mResultData = data;
            ((ShotApplication)getApplication()).setResult(result);
            ((ShotApplication)getApplication()).setIntent(intent);
            Intent intent = new Intent(getApplicationContext(), IGameService.class);
            startService(intent);
            Log.i(TAG, "start service IGameService");
        }else{
            startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION);
            //IGameService.mMediaProjectionManager1 = mMediaProjectionManager;
            ((ShotApplication)getApplication()).setMediaProjectionManager(mMediaProjectionManager);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_MEDIA_PROJECTION) {
            if (resultCode != Activity.RESULT_OK) {
                return;
            }else if(data != null && resultCode != 0){
                Log.i(TAG, "user agree the application to capture screen");
                //IGameService.mResultCode = resultCode;
                //IGameService.mResultData = data;
                result = resultCode;
                intent = data;
                ((ShotApplication)getApplication()).setResult(resultCode);
                ((ShotApplication)getApplication()).setIntent(data);
                Intent intent = new Intent(getApplicationContext(), IGameService.class);
                startService(intent);
                Log.i(TAG, "start service IGameService");

                finish();
            }
        }
    }
}
