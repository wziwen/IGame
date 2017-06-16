package com.wenziwen.igame.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by gavin on 2017/3/27.
 */

public class SimilarPhoto {

    private static final String TAG = SimilarPhoto.class.getSimpleName();


//    public static List<Group> find(Context context, List<Photo> photos) {
//        calculateFingerPrint(context, photos);
//
//        List<Group> groups = new ArrayList<>();
//
//        for (int i = 0; i < photos.size(); i++) {
//            Photo photo = photos.get(i);
//
//            List<Photo> temp = new ArrayList<>();
//            temp.add(photo);
//
//            for (int j = i + 1; j < photos.size(); j++) {
//
//                Photo photo2 = photos.get(j);
//
//                int dist = hamDist(photo.getFinger(), photo2.getFinger());
//
//                if (dist < 5) {
//                    temp.add(photo2);
//                    photos.remove(photo2);
//                    j--;
//                }
//            }
//
//            Group group = new Group();
//            group.setPhotos(temp);
//            groups.add(group);
//        }
//
//        return groups;
//    }
//
//    public static void calculateFingerPrint(Context context, List<Photo> photos) {
//        float scale_width, scale_height;
//
//        for (Photo p : photos) {
//            Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(context.getContentResolver(), p.getId(), MediaStore.Images.Thumbnails.MICRO_KIND, null);
//            scale_width = 8.0f / bitmap.getWidth();
//            scale_height = 8.0f / bitmap.getHeight();
//            Matrix matrix = new Matrix();
//            matrix.postScale(scale_width, scale_height);
//
//            Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
//            p.setFinger(getFingerPrint(scaledBitmap));
//
//            bitmap.recycle();
//            scaledBitmap.recycle();
//        }
//    }

    public static long getFingerPrint(Bitmap bitmap) {
        double[][] grayPixels = getGrayPixels(bitmap);
        double grayAvg = getGrayAvg(grayPixels);
        return getFingerPrint(grayPixels, grayAvg);
    }

    private static long getFingerPrint(double[][] pixels, double avg) {
        int width = pixels[0].length;
        int height = pixels.length;

        byte[] bytes = new byte[height * width];

        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (pixels[i][j] >= avg) {
                    bytes[i * height + j] = 1;
                    stringBuilder.append("1");
                } else {
                    bytes[i * height + j] = 0;
                    stringBuilder.append("0");
                }
            }
        }

//        Log.d(TAG, "getFingerPrint: " + stringBuilder.toString());

        long fingerprint1 = 0;
        long fingerprint2 = 0;
        for (int i = 0; i < 64; i++) {
            if (i < 32) {
                fingerprint1 += (bytes[63 - i] << i);
            } else {
                fingerprint2 += (bytes[63 - i] << (i - 31));
            }
        }

        return (fingerprint2 << 32) + fingerprint1;
    }

    private static double getGrayAvg(double[][] pixels) {
        int width = pixels[0].length;
        int height = pixels.length;
        int count = 0;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                count += pixels[i][j];
            }
        }
        return count / (width * height);
    }


    private static double[][] getGrayPixels(Bitmap bitmap) {
        int width = 8;
        int height = 8;
        double[][] pixels = new double[height][width];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                pixels[i][j] = computeGrayValue(bitmap.getPixel(i, j));
            }
        }
        return pixels;
    }

    private static double computeGrayValue(int pixel) {
        int red = (pixel >> 16) & 0xFF;
        int green = (pixel >> 8) & 0xFF;
        int blue = (pixel) & 255;
        return 0.3 * red + 0.59 * green + 0.11 * blue;
    }

    public static int hamDist(long finger1, long finger2) {
        int dist = 0;
        long result = finger1 ^ finger2;
        while (result != 0) {
            ++dist;
            result &= result - 1;
        }
        return dist;
    }

    public static List<Rect> findTarget(Bitmap bitmapToFind, Bitmap bitmap) throws Exception {
        long startTime = System.currentTimeMillis();
        int step = 2;
        int width = bitmapToFind.getWidth();
        int height = bitmapToFind.getHeight();
        long targetFingerPrint = SimilarPhoto.getFingerPrint(bitmapToFind);
        List<Rect> list = new ArrayList<>();
        int count = 0;
        for (int i = 0; i * step < bitmap.getWidth() - width; i ++) {
            for (int j = 0; j * step < bitmap.getHeight() - height; j ++) {
                count ++;
                int x = i * step;
                int y = j * step;
//                Log.d(TAG, String.format("x: %s, y:%s", x, y));

//                if(bitmap.getPixel(x, y) == bitmapToFind.getPixel(0, 0)) { //&& bitmap.getPixel(x + 1, y + 1) == bitmapToFind.getPixel(1, 1)) {
                boolean next = false;
                for (Rect rect : list) {
                    if (rect.contains(x, y)) {
                        next = true;
                        break;
                    }
                }
                if (next) {
                    continue;
                }
                if(almostSameColor(bitmap.getPixel(x, y), bitmapToFind.getPixel(0, 0))
                        && almostSameColor(bitmap.getPixel(x+1, y+height-1), bitmapToFind.getPixel(1, height-1))
                        && almostSameColor(bitmap.getPixel(x+width-1, y+2), bitmapToFind.getPixel(width - 1, 2))
                        && almostSameColor(bitmap.getPixel(x+width - 1, y+height -1), bitmapToFind.getPixel(width - 1, height - 1))
                        && almostSameColor(bitmap.getPixel(x+width - 2, y+height -2), bitmapToFind.getPixel(width - 3, height - 2))
                        && almostSameColor(bitmap.getPixel(x+width - 3, y+height -3), bitmapToFind.getPixel(width - 3, height - 3))
                        ) { //&& bitmap.getPixel(x + 1, y + 1) == bitmapToFind.getPixel(1, 1)) {
                    long tempPrint = getTempPrint(bitmap, width, height, x, y);
                    int dist = SimilarPhoto.hamDist(targetFingerPrint, tempPrint);
//                    int dist = ImagePHash.distance(targetFinger, tempPrint);
                    if (dist < 15) {
                        Log.d(TAG, "found target:" + dist);
                        Rect rect = new Rect(x, y, x + width, y + height);
                        list.add(rect);
                    }
                }
            }
        }
        Log.d(TAG, "findTarget time:" + (System.currentTimeMillis() - startTime));
        Log.d(TAG, "findTarget create bitmap time:" + createBitmapTime);
        Log.d(TAG, "findTarget total count:" + count);
        Log.d(TAG, "findTarget create bitmap count:" + createBitmapcount);
        return list;
    }

    private static boolean almostSameColor(int pixel, int pixel1) {
        int offset = 100;
        return (Math.abs(Color.red(pixel) - Color.red(pixel1)) < offset
                && Math.abs(Color.blue(pixel) - Color.blue(pixel1)) < offset
                && Math.abs(Color.green(pixel) - Color.green(pixel1)) < offset
        );
    }

    private static long getTempPrint(Bitmap bitmap, int width, int height, int x, int y) throws Exception {
        Log.d(TAG, String.format("getTempPrint %s, %s", x, y));
        createBitmapcount ++;
        long startTime = System.currentTimeMillis();
        Bitmap temp = Bitmap.createBitmap(bitmap, x, y, width, height);
        createBitmapTime += (System.currentTimeMillis() - startTime);
        return SimilarPhoto.getFingerPrint(temp);
    }
    static long createBitmapTime = 0;
    static long createBitmapcount = 0;
}
