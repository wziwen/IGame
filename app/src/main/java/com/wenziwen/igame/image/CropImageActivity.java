package com.wenziwen.igame.image;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.wenziwen.igame.R;
import com.wenziwen.igame.bean.ActionBean;

import java.util.List;

import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;

import static com.bumptech.glide.request.target.Target.SIZE_ORIGINAL;

public class CropImageActivity extends Activity {

    CropImageView4 mCropImage;
    PhotoInfo photoInfo;

    private GalleryFinal.OnHanlderResultCallback mOnHanlderResultCallback = new GalleryFinal.OnHanlderResultCallback() {
        @Override
        public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
            photoInfo = resultList.get(0);
            Glide.with(CropImageActivity.this)
                    .load(resultList.get(0).getPhotoPath())
                    .into(new SimpleTarget<GlideDrawable>(SIZE_ORIGINAL, SIZE_ORIGINAL) {
                        @Override
                        public void onResourceReady(GlideDrawable glideDrawable, GlideAnimation anim) {
                            mCropImage.setDrawable(glideDrawable, 300, 300);
                        }
                    });
        }

        @Override
        public void onHanlderFailure(int requestCode, String errorMsg) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        cropImage4();
    }

    private void cropImage4() {
        setContentView(R.layout.fragment_cropimage4);
        mCropImage = (CropImageView4) findViewById(R.id.cropImg);
        mCropImage.setDrawable(getResources().getDrawable(R.drawable.precrop), 300, 300);
        findViewById(R.id.save).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
//                new Thread(new Runnable() {
//
//                    @Override
//                    public void run() {
                String imgPath = FileUtil.SDCARD_PAHT + "/" + System.currentTimeMillis() + "crop.png";
                ActionBean actionBean = new ActionBean();
                actionBean.setTargetImgPath(imgPath);
                actionBean.setTargetRectangle(new ActionBean.Rectangle(mCropImage.getCropRect()));
                if (photoInfo != null) {
                    actionBean.setPhotoPath(photoInfo.getPhotoPath());
                }
                EditText etTime = (EditText) findViewById(R.id.et_time);
                String value = etTime.getText().toString();
                if (!TextUtils.isEmpty(value)) {
                    actionBean.setClickWaitTime(Integer.parseInt(value));
                }

                Bitmap bitmap = BitmapFactory.decodeFile(photoInfo.getPhotoPath());
                ActionBean.Rectangle rectangle = actionBean.getTargetRectangle();
                int maybeWidth = (int)(rectangle.right - rectangle.left);
                int maybeHeight = (int)(rectangle.bottom - rectangle.top);
                Matrix matrix = new Matrix();
                float scale = (float) (maybeWidth) / (float) (maybeHeight);
                matrix.postScale(scale, scale);

                Bitmap targetBitmap = Bitmap.createBitmap(bitmap, (int)rectangle.left, (int)rectangle.top, (int)(rectangle.right - rectangle.left), (int)(rectangle.bottom - rectangle.top), matrix, true);
                FileUtil.writeImage(targetBitmap, imgPath);
                Intent mIntent = new Intent();
                mIntent.putExtra("cropImagePath", imgPath);

                Bitmap saveBitmap = BitmapFactory.decodeFile(imgPath);
                targetBitmap.sameAs(saveBitmap);
                mIntent.putExtra("action", actionBean);
                setResult(RESULT_OK, mIntent);
                finish();
//                    }
//                }).start();
            }
        });

        findViewById(R.id.btn_select).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                GalleryFinal.openGallerySingle(0, mOnHanlderResultCallback);
            }
        });
    }

    public static void show(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, CropImageActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }
}
