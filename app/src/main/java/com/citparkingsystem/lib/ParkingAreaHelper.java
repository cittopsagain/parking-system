package com.citparkingsystem.lib;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.citparkingsystem.R;
import com.citparkingsystem.requests.Parking;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by Dave Tolentin on 8/2/2017.
 */

public class ParkingAreaHelper extends View {

    private static String TAG = ParkingAreaHelper.class.getSimpleName();

    private SharedPreferences sharedPreferences;
    private SessionManager sessionManager;
    private int parkingArea;
    private Paint circle = new Paint();
    private Parking parking;
    private Context context;
    private AlertDialog.Builder builder;

    private int maxAcademicAreaParkingSlot = 71;
    private float []xAxis = {};
    private float []yAxis = {};
    private String []vacantSlots = {};
    private boolean pressed = true;
    private int actionBarSize = 0;

    public ParkingAreaHelper(Context context, int position) {
        super(context);
        this.context = context;
        parking = new Parking(context);
        sessionManager = new SessionManager(context);
        sharedPreferences = context.getSharedPreferences("CIT_PARKING_SYSTEM", Context.MODE_PRIVATE);
        this.parkingArea = position;
        parking.getParkingSlots();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(context);
        }

        if (this.parkingArea == 0) {
            try {
                vacantSlots = sharedPreferences.getString("keySlotsAcademic", "").split(",");
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            Log.e(TAG, "Academic Vacant slots: "+sharedPreferences.getString("keySlotsAcademic", ""));
            xAxis = new float[maxAcademicAreaParkingSlot];
            yAxis = new float[maxAcademicAreaParkingSlot];
        }
        TypedValue tv = new TypedValue();
        if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarSize = TypedValue.complexToDimensionPixelSize(tv.data,
                    getResources().getDisplayMetrics());
        }
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).
                getDefaultDisplay();
        Log.e(TAG, "Size: "+actionBarSize);
        // Hardcode all x, y and radius in all screen devices
        float deviceDensity = context.getResources().getDisplayMetrics().density;
        Log.e(TAG, "Device density: "+deviceDensity);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_academic_area_2d);
        int width = DeviceHelper.getScreenWidth();
        // int height = DeviceHelper.getScreenHeight() /*- actionBarSize*/;
        int height = DeviceHelper.getScreenHeight() - actionBarSize;
        Point size = new Point();
        display.getSize(size);
        /*int width = size.x;
        int height = size.y;*/
        Log.e(TAG, "Width: "+width+" Height: "+height);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
        canvas.drawBitmap(scaledBitmap, 0, 0, null);

        // Upper slot
        float upperStartX = 0;
        float upperStartY = 0;
        if (width == 1440 && height == (2392 - actionBarSize)) {
            upperStartX = 190;
            upperStartY = 120;
        }

        float radius = dipToPixels(this.context, 5);
        int vacant = Color.GREEN;
        int occupied = Color.RED;
        for (int i = 1; i <= 5; i++) {
            float x = upperStartX * ((float) width / (float) scaledBitmap.getWidth());
            float y = upperStartY * ((float) height / (float) scaledBitmap.getHeight());
            boolean flag = false;
            circle.setColor(occupied);

            for (int j = 0; j <= vacantSlots.length; j++) {
                try {
                    /**
                     * if flag is true it means it is available slots
                     */
                    if (vacantSlots[j] != "") {
                        if (i == Integer.parseInt(vacantSlots[j].trim())) {
                            flag = true;
                        }
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
                xAxis[i] = x;
                yAxis[i] = y;
            }
            if (flag) {
                circle.setColor(vacant);
            }

            canvas.drawCircle(x, y, radius, circle);
            upperStartX += 100;
            upperStartY += 17;
        }
        Log.e(TAG, "Last Y: "+(height - upperStartY));
        Log.e(TAG, "Upper start y: "+upperStartY);
        // Below upper slot
        float belowUpperStartX = 0;
        float belowUpperStartY = 0;
        if (width == 1440 && height == (2392 - actionBarSize)) {
            belowUpperStartX = dipToPixels(this.context, 60);
            belowUpperStartY = dipToPixels(this.context, 139);
        }

        for (int i = 6; i <= 11; i++) {
            float x = belowUpperStartX * ((float) width / (float) scaledBitmap.getWidth());
            float y = belowUpperStartY * ((float) height / (float) scaledBitmap.getHeight());
            Log.e(TAG, "Below: "+y);
            boolean flag = false;
            circle.setColor(occupied);

            for (int j = 0; j <= vacantSlots.length; j++) {
                try {
                    /**
                     * if flag is true it means it is available slots
                     */
                    if (vacantSlots[j] != "") {
                        if (i == Integer.parseInt(vacantSlots[j].trim())) {
                            flag = true;
                        }
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
                xAxis[i] = x;
                yAxis[i] = y;
            }
            if (flag) {
                circle.setColor(vacant);
            }

            canvas.drawCircle(x, y, radius, circle);
            belowUpperStartX += dipToPixels(this.context, 30);
            belowUpperStartY -= dipToPixels(this.context, (float) 10.5);
        }
        Log.e(TAG, "Last X: "+belowUpperStartX);
        // Middle slot
        float middleUpperStartX = 0;
        float middleUpperStartY = 0;
        if (width == 1440 && height == (2392 - actionBarSize)) {
            middleUpperStartX = dipToPixels(this.context, 235);
            middleUpperStartY = dipToPixels(this.context, 120);
        }

        for (int i = 12; i <= 15; i++) {
            float x = middleUpperStartX * ((float) width / (float) scaledBitmap.getWidth());
            float y = middleUpperStartY * ((float) height / (float) scaledBitmap.getHeight());
            boolean flag = false;
            circle.setColor(occupied);

            for (int j = 0; j <= vacantSlots.length; j++) {
                try {
                    /**
                     * if flag is true it means it is available slots
                     */
                    if (vacantSlots[j] != "") {
                        if (i == Integer.parseInt(vacantSlots[j].trim())) {
                            flag = true;
                        }
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
                xAxis[i] = x;
                yAxis[i] = y;
            }
            if (flag) {
                circle.setColor(vacant);
            }

            canvas.drawCircle(x, y, radius, circle);
            middleUpperStartX += dipToPixels(this.context, 7);
            middleUpperStartY += dipToPixels(this.context, 25);
        }
        if (width == 1440 && height == (2392 - actionBarSize)) {
            middleUpperStartY += 50;
            middleUpperStartX -= 24;
        }
        for (int i = 16; i <= 24; i++) {
            float x = middleUpperStartX * ((float) width / (float) scaledBitmap.getWidth());
            float y = middleUpperStartY * ((float) height / (float) scaledBitmap.getHeight());
            boolean flag = false;
            circle.setColor(occupied);

            for (int j = 0; j <= vacantSlots.length; j++) {
                try {
                    /**
                     * if flag is true it means it is available slots
                     */
                    if (vacantSlots[j] != "") {
                        if (i == Integer.parseInt(vacantSlots[j].trim())) {
                            flag = true;
                        }
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
                xAxis[i] = x;
                yAxis[i] = y;
            }
            if (flag) {
                circle.setColor(vacant);
            }

            canvas.drawCircle(x, y, radius, circle);
            middleUpperStartX += dipToPixels(this.context, 6);
            middleUpperStartY += dipToPixels(this.context, 23);
        }

        // Left side slot
        float leftUpperStartX = 0;
        float leftUpperStartY = 0;
        if (width == 1440 && height == (2392 - actionBarSize)) {
            leftUpperStartX = dipToPixels(this.context, 116);
            leftUpperStartY = dipToPixels(this.context, 510);
        }

        for (int i = 34; i <= 47; i++) {
            float x = leftUpperStartX * ((float) width / (float) scaledBitmap.getWidth());
            float y = leftUpperStartY * ((float) height / (float) scaledBitmap.getHeight());
            boolean flag = false;
            circle.setColor(occupied);

            for (int j = 0; j <= vacantSlots.length; j++) {
                try {
                    /**
                     * if flag is true it means it is available slots
                     */
                    if (vacantSlots[j] != "") {
                        if (i == Integer.parseInt(vacantSlots[j].trim())) {
                            flag = true;
                        }
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
                xAxis[i] = x;
                yAxis[i] = y;
            }
            if (flag) {
                circle.setColor(vacant);
            }

            canvas.drawCircle(x, y, radius, circle);
            leftUpperStartX += dipToPixels(this.context, 15);
            leftUpperStartY -= dipToPixels(this.context, 5);
        }

        // Left side slot
        float belowLeftUpperStartX = 0;
        float belowLeftUpperStartY = 0;
        if (width == 1440 && height == (2392 - actionBarSize)) {
            belowLeftUpperStartX = dipToPixels(this.context, 140);
            belowLeftUpperStartY = dipToPixels(this.context, 590);
        }

        for (int i = 48; i <= 61; i++) {
            float x = belowLeftUpperStartX * ((float) width / (float) scaledBitmap.getWidth());
            float y = belowLeftUpperStartY * ((float) height / (float) scaledBitmap.getHeight());
            boolean flag = false;
            circle.setColor(occupied);

            for (int j = 0; j <= vacantSlots.length; j++) {
                try {
                    /**
                     * if flag is true it means it is available slots
                     */
                    if (vacantSlots[j] != "") {
                        if (i == Integer.parseInt(vacantSlots[j].trim())) {
                            flag = true;
                        }
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
                xAxis[i] = x;
                yAxis[i] = y;
            }
            if (flag) {
                circle.setColor(vacant);
            }

            canvas.drawCircle(x, y, radius, circle);
            belowLeftUpperStartX += dipToPixels(this.context, 15);
            belowLeftUpperStartY -= dipToPixels(this.context, 5);
        }

        // Left side slot
        float rightSideUpperStartX = 0;
        float rightSideUpperStartY = 0;
        if (width == 1440 && height == (2392 - actionBarSize)) {
            rightSideUpperStartX = dipToPixels(this.context, 332);
            rightSideUpperStartY = dipToPixels(this.context, 280);
        }

        for (int i = 62; i <= 70; i++) {
            float x = rightSideUpperStartX * ((float) width / (float) scaledBitmap.getWidth());
            float y = rightSideUpperStartY * ((float) height / (float) scaledBitmap.getHeight());
            boolean flag = false;
            circle.setColor(occupied);

            for (int j = 0; j <= vacantSlots.length; j++) {
                try {
                    /**
                     * if flag is true it means it is available slots
                     */
                    if (vacantSlots[j] != "") {
                        if (i == Integer.parseInt(vacantSlots[j].trim())) {
                            flag = true;
                        }
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
                xAxis[i] = x;
                yAxis[i] = y;
            }
            if (flag) {
                circle.setColor(vacant);
            }

            canvas.drawCircle(x, y, radius, circle);
            rightSideUpperStartX += dipToPixels(this.context, 5);
            rightSideUpperStartY += dipToPixels(this.context, 23);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // Do not re query if the touch x and y does not match to slot x and y

                builder.setTitle("Alert")
                    .setMessage("Are you sure you want to ?")
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Set changeColor to true and slotIndex to n
                            // pressed = true
                        }
                    }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // pressed = false
                    }
                })/*.show()*/;

                /**
                 * for now set the pressed to true
                 */
                if (pressed) {
                    if (parkingArea == 0) {
                        Log.e(TAG, "Touch X: "+touchX);
                        for (int i = 1; i < maxAcademicAreaParkingSlot; i++) {
                            int nearestMatchXAxis = 0;
                            int nearestIndex = 0;
                            boolean insideCircle = false;
                            for (int j = 0; j < xAxis.length; j++) {
                                if (touchInsideCircle(touchX, touchY, xAxis[j], yAxis[j],
                                        dipToPixels(this.context, 5))) {
                                    insideCircle = true;
                                    nearestMatchXAxis = (int)xAxis[j];
                                    nearestIndex = j;
                                }
                            }
                            if (insideCircle) {
                                Log.e(TAG, "Inside circle: "+insideCircle+" Nearest index: " +
                                        ""+nearestIndex+" Nearest axis: "+nearestMatchXAxis);
                                // Remove the current slot index if set to occupied
                                // else add slot index if set to vacant
                                boolean found = false;
                                for (int k = 0; k < vacantSlots.length; k++) {
                                    if (vacantSlots[k] != "") {
                                        if (Integer.parseInt(
                                                vacantSlots[k].trim()) == nearestIndex) {
                                            found = true; // vacant
                                        }
                                    }
                                }
                                if (found) {
                                    // means it is vacant
                                    // set to occupied and remove it in the array
                                    Log.e(TAG, "Found: "+found+" Vacant index: "+nearestIndex);
                                    String newSlots = "";
                                    for (int l = 0; l < vacantSlots.length; l++) {
                                        if (vacantSlots[l] != "") {
                                            if (Integer.parseInt(vacantSlots[l].trim())
                                                    != nearestIndex) {
                                                newSlots += vacantSlots[l]+ " ";
                                            }
                                        }
                                    }
                                    String []x = newSlots.split(" ");
                                    vacantSlots = StringHelper.implode(", ", x).split(", ");
                                    Log.e(TAG, "Vacant Slots: "+vacantSlots);
                                    // Update now the table
                                    parking.updateParkingAreaSlot(StringHelper.implode(", ", x),
                                            parkingArea, String.valueOf(nearestIndex), "occupied");
                                } else {
                                    // means it is occupied
                                    // set to vacant and update the array with corresponding index
                                    String newSlots = "";
                                    Log.e(TAG, "Slot length: "+vacantSlots.length);

                                    for (int l = 0; l < vacantSlots.length + 1; l++) {
                                        if (l  == vacantSlots.length) {
                                            newSlots += nearestIndex;
                                        } else {
                                            newSlots += vacantSlots[l]+ " ";
                                        }
                                    }
                                    Log.e(TAG, "Vacant Slots Else: "+newSlots);
                                    Log.e(TAG, "Found: "+found+" Occupied index: "+
                                            nearestIndex+" New Slot: "+newSlots);

                                    String []x = newSlots.split(" ");
                                    vacantSlots = StringHelper.implode(", ", x).split(", ");
                                    // Update now the table
                                    parking.updateParkingAreaSlot(StringHelper.implode(", ", x),
                                            parkingArea, String.valueOf(nearestIndex), "vacant");
                                }

                                // Query again for new slots
                                parking.getParkingSlots();
                                break;
                            }
                        }
                    }
                }
                break;
        }
        // Tell the View to redraw the Canvas
        invalidate();

        // Tell the View that we handled the event
        return true;
    }

    private boolean touchInsideCircle(float x, float y, float circleCenterX,
                                      float circleCenterY, float circleRadius) {
        double dx = Math.pow(x - circleCenterX, 2);
        double dy = Math.pow(y - circleCenterY, 2);

        if ((dx + dy) < Math.pow(circleRadius, 2)) {
            return true;
        } else {
            return false;
        }
    }

    public static float dipToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }

    private Bitmap decodeFile(File f){
        try {
            // decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f),null,o);
            // Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE = 70;
            int widthTmp = o.outWidth, heightTmp = o.outHeight;
            int scale = 1;
            while(true){
                if (widthTmp / 2 < REQUIRED_SIZE || heightTmp / 2 < REQUIRED_SIZE)
                    break;
                widthTmp /= 2;
                heightTmp /= 2;
                scale++;
            }

            // decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {}
        return null;
    }
}
