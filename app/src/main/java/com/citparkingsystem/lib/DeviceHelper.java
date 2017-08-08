package com.citparkingsystem.lib;

import android.content.res.Resources;

/**
 * Created by Dave Tolentin on 7/30/2017.
 */

public class DeviceHelper {

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }
}
