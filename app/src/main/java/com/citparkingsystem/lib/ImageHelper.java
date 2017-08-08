package com.citparkingsystem.lib;

import android.graphics.Bitmap;
import android.util.Log;

/**
 * Created by Dave Tolentin on 7/31/2017.
 */

public class ImageHelper {

    private Bitmap scaleBitmap(Bitmap bm) {
        int width = bm.getWidth();
        int height = bm.getHeight();

        Log.v("Pictures", "Width and height are " + width + "--" + height);

        if (width > height) {
            // landscape
            float ratio = (float) width / DeviceHelper.getScreenWidth();
            width = DeviceHelper.getScreenWidth();
            height = (int)(height / ratio);
        } else if (height > width) {
            // portrait
            float ratio = (float) height / DeviceHelper.getScreenHeight();
            height = DeviceHelper.getScreenHeight();
            width = (int)(width / ratio);
        } else {
            // square
            height = DeviceHelper.getScreenHeight();
            width = DeviceHelper.getScreenWidth();
        }

        Log.v("Pictures", "after scaling Width and height are " + width + "--" + height);

        bm = Bitmap.createScaledBitmap(bm, width, height, true);
        return bm;
    }
}
