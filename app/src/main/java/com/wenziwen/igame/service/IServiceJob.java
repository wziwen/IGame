package com.wenziwen.igame.service;

import android.graphics.Bitmap;

/**
 * Created by ziwen.wen on 2017/4/19.
 */
public interface IServiceJob {
    /**
     * call on service have capture a bitmap
     * @param bitmap
     */
    void onCapture(IGameService service, Bitmap bitmap);
}
