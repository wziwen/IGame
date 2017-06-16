package com.wenziwen.igame.service;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;


import com.wenziwen.igame.R;
import com.wenziwen.igame.ShotApplication;
import com.wenziwen.igame.bean.ActionBean;
import com.wenziwen.igame.image.FileUtil;
import com.wenziwen.igame.util.EventInject;
import com.wenziwen.igame.util.SimilarPhoto;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class IGameService extends Service {
    private LinearLayout mFloatLayout = null;
    private WindowManager.LayoutParams wmParams = null;
    private WindowManager mWindowManager = null;
    private LayoutInflater inflater = null;
    private ImageView mFloatView = null;

    private static final String TAG = "MainActivity";

    private SimpleDateFormat dateFormat = null;
    private String strDate = null;
    private String pathImage = null;
    private String nameImage = null;

    private MediaProjection mMediaProjection = null;
    private VirtualDisplay mVirtualDisplay = null;

    public static int mResultCode = 0;
    public static Intent mResultData = null;
    public static MediaProjectionManager mMediaProjectionManager1 = null;

    private WindowManager mWindowManager1 = null;
    private int windowWidth = 0;
    private int windowHeight = 0;
    private ImageReader mImageReader = null;
    private DisplayMetrics metrics = null;
    private int mScreenDensity = 0;

    private static Handler handler;
    private int currentIndex;
    private ArrayList<ActionBean> dataList;

    private IServiceJob serviceJobHandler;

    @Override
    public void onCreate() {
        super.onCreate();

        createFloatView();

        createVirtualEnvironment();
        handler = new Handler();

        try {
            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(getFilesDir().getAbsolutePath() + "/cache"));
            dataList = (ArrayList<ActionBean>) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int jobType = 0;
        if (intent != null) {
            jobType = intent.getIntExtra("jobType", 0);
        }

        refreshJobType(jobType);
        return super.onStartCommand(intent, flags, startId);
    }

    private void refreshJobType(int jobType) {
        if (jobType == 0) {
            serviceJobHandler = new SaveBitmapJob();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createFloatView() {
        wmParams = new WindowManager.LayoutParams();
        mWindowManager = (WindowManager) getApplication().getSystemService(getApplication().WINDOW_SERVICE);
        wmParams.type = LayoutParams.TYPE_PHONE;
        wmParams.format = PixelFormat.RGBA_8888;
        wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        wmParams.x = 0;
        wmParams.y = 0;
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        inflater = LayoutInflater.from(getApplication());
        mFloatLayout = (LinearLayout) inflater.inflate(R.layout.float_layout, null);
        mWindowManager.addView(mFloatLayout, wmParams);
        mFloatView = (ImageView) mFloatLayout.findViewById(R.id.float_id);

        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        mFloatView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                wmParams.x = (int) event.getRawX() - mFloatView.getMeasuredWidth() / 2;
                wmParams.y = (int) event.getRawY() - mFloatView.getMeasuredHeight() / 2 - 25;
                mWindowManager.updateViewLayout(mFloatLayout, wmParams);
                return false;
            }
        });

        mFloatView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // hide the button
                mFloatView.setVisibility(View.INVISIBLE);

                Handler handler1 = new Handler();
                handler1.postDelayed(new Runnable() {
                    public void run() {
                        //start virtual
                        startVirtual();
                    }
                }, 500);

                Handler handler2 = new Handler();
                handler2.postDelayed(new Runnable() {
                    public void run() {
                        //capture the screen
                        startCapture();
                    }
                }, 1500);

                Handler handler3 = new Handler();
                handler3.postDelayed(new Runnable() {
                    public void run() {
                        mFloatView.setVisibility(View.VISIBLE);
                        stopVirtual();
                    }
                }, 1000);
            }
        });

        Log.i(TAG, "created the float sphere view");
    }

    private void createVirtualEnvironment() {
        dateFormat = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss");
        strDate = dateFormat.format(new java.util.Date());
        pathImage = Environment.getExternalStorageDirectory().getPath() + "/Pictures/";
        nameImage = pathImage + strDate + ".png";
        mMediaProjectionManager1 = (MediaProjectionManager) getApplication().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        mWindowManager1 = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        windowWidth = mWindowManager1.getDefaultDisplay().getWidth();
        windowHeight = mWindowManager1.getDefaultDisplay().getHeight();
        metrics = new DisplayMetrics();
        mWindowManager1.getDefaultDisplay().getMetrics(metrics);
        mScreenDensity = metrics.densityDpi;
        mImageReader = ImageReader.newInstance(windowWidth, windowHeight, 0x1, 2); //ImageFormat.RGB_565

        Log.i(TAG, "prepared the virtual environment");
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void startVirtual() {
        if (mMediaProjection != null) {
            Log.i(TAG, "want to display virtual");
            virtualDisplay();
        } else {
            Log.i(TAG, "start screen capture intent");
            Log.i(TAG, "want to build mediaprojection and display virtual");
            setUpMediaProjection();
            virtualDisplay();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setUpMediaProjection() {
        mResultData = ((ShotApplication) getApplication()).getIntent();
        mResultCode = ((ShotApplication) getApplication()).getResult();
        mMediaProjectionManager1 = ((ShotApplication) getApplication()).getMediaProjectionManager();
        mMediaProjection = mMediaProjectionManager1.getMediaProjection(mResultCode, mResultData);
        Log.i(TAG, "mMediaProjection defined");
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void virtualDisplay() {
        mVirtualDisplay = mMediaProjection.createVirtualDisplay("screen-mirror",
                windowWidth, windowHeight, mScreenDensity, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mImageReader.getSurface(), null, null);
        Log.i(TAG, "virtual displayed");
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void startCapture() {
        strDate = dateFormat.format(new java.util.Date());
        nameImage = pathImage + strDate + ".png";

        Image image = mImageReader.acquireLatestImage();
        if (image == null) {
            return;
        }
        int width = image.getWidth();
        int height = image.getHeight();
        final Image.Plane[] planes = image.getPlanes();
        final ByteBuffer buffer = planes[0].getBuffer();
        int pixelStride = planes[0].getPixelStride();
        int rowStride = planes[0].getRowStride();
        int rowPadding = rowStride - pixelStride * width;
        Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(buffer);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height);
        image.close();

        // 缩小图片
        int scale = 2;
        bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / scale, bitmap.getHeight() / scale, false);
        Log.i(TAG, "image data captured");

        Bitmap bitmapToFind = loadTargetBitmap(scale);


        if (bitmap != null) {
            try {
                List<Rect> list = SimilarPhoto.findTarget(bitmapToFind, bitmap);
                int x, y;
                Log.d(TAG, "find target count: " + list.size());
                for (Rect rect : list) {
                    x = ((rect.left * scale) + (rect.right * scale)) / 2;
                    y = ((rect.top * scale) + (rect.bottom * scale)) / 2;
                    EventInject.getInstance()
                            .tap(x, y);
                }
//                serviceJobHandler.onCapture(this, bitmap);
//                ActionBean actionBean = dataList.get(currentIndex);
//                ActionBean.Rectangle rectangle = actionBean.getTargetRectangle();
//                int maybeWidth = (int)(rectangle.right - rectangle.left);
//                int maybeHeight = (int)(rectangle.bottom - rectangle.top);
//                Matrix matrix = new Matrix();
//                float scale = (float) (maybeWidth) / (float) (maybeHeight);
//                matrix.postScale(scale, scale);
//
//                Bitmap maybeBitmap = Bitmap.createBitmap(bitmap, (int)rectangle.left, (int)rectangle.top, (int)(rectangle.right - rectangle.left), (int)(rectangle.bottom - rectangle.top), matrix, true);
//                Bitmap targetBitmap = BitmapFactory.decodeFile(actionBean.getTargetImgPath());
//                if (FileUtil.isSameBitmap(maybeBitmap, targetBitmap)) {
//                    Log.d(TAG, "maybeBitmap is SAME as targetBitmap");
//                    int x = (int) ((rectangle.left + rectangle.right) / 2);
//                    int y = (int) ((rectangle.top + rectangle.bottom) / 2);
//                    String command = "input tap " + x + " " + y;
//                    Log.d(TAG, command);
//                    Runtime.getRuntime().exec(command);
//                } else {
//                    Log.d(TAG, "maybeBitmap is NOT SAME as targetBitmap");
//                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Bitmap loadTargetBitmap(int scale) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inSampleSize = scale;
        Bitmap bitmap1 = BitmapFactory.decodeStream(getResources().openRawResource(R.raw.da_dishu_head), null, options);
        return bitmap1;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void tearDownMediaProjection() {
        if (mMediaProjection != null) {
            mMediaProjection.stop();
            mMediaProjection = null;
        }
        Log.i(TAG, "mMediaProjection undefined");
    }

    private void stopVirtual() {
        if (mVirtualDisplay == null) {
            return;
        }
        mVirtualDisplay.release();
        mVirtualDisplay = null;
        Log.i(TAG, "virtual display stopped");
    }

    @Override
    public void onDestroy() {
        // to remove mFloatLayout from windowManager
        super.onDestroy();
        if (mFloatLayout != null) {
            mWindowManager.removeView(mFloatLayout);
        }
        tearDownMediaProjection();
        Log.i(TAG, "application destroy");
    }
}