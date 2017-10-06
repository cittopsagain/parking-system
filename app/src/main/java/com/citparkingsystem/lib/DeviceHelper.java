package com.citparkingsystem.lib;

import android.content.res.Resources;

/**
 * Created by Walter Ybanez on 7/30/2017.
 * Gets the width and height of the screen
 */

public class DeviceHelper {

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }
}
