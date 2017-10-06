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
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.WindowManager;

import com.citparkingsystem.R;
import com.citparkingsystem.requests.Parking;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by Walter Ybanez on 8/2/2017.
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
    private String newSlots = "";
    private int nearestIndex;

    private int maxAcademicHsParkingSlot = 71;
    private float []xAxis = {};
    private float []yAxis = {};
    private String []vacantSlots = {};
    private boolean pressed = true;
    private int actionBarSize = 0;
    private Bitmap image;
    Bitmap scaledBitmap;

    private ScaleGestureDetector scaleDetector;
    private float scaleFactor = 1.f;

    public ParkingAreaHelper(Context context, int position) {
        super(context);
        this.context = context;
        parking = new Parking(context);
        sessionManager = new SessionManager(context);
        sharedPreferences = context.getSharedPreferences("CIT_PARKING_SYSTEM", Context.MODE_PRIVATE);
        this.parkingArea = position;
        parking.getParkingSlots();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(context);
        }

        if (this.parkingArea == 3) {
            try {
                vacantSlots = sharedPreferences.getString("keyHsSlots", "").split(",");
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            Log.e(TAG, "Academic Vacant slots: "+sharedPreferences.getString("keyHsSlots", ""));
            xAxis = new float[maxAcademicHsParkingSlot];
            yAxis = new float[maxAcademicHsParkingSlot];
        }
        TypedValue tv = new TypedValue();
        if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarSize = TypedValue.complexToDimensionPixelSize(tv.data,
                    getResources().getDisplayMetrics());
        }
        init(context);
    }

    private void init(Context ctx) {
        image = BitmapFactory.decodeResource(getResources(), R.drawable.ic_academic_area_2d);
        scaleDetector = new ScaleGestureDetector(ctx, new ScaleGestureDetector.OnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                scaleFactor *= detector.getScaleFactor();
                scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 10.0f));
                invalidate();
                return true;
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                return false;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {

            }
        });
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        // super.onDraw(canvas);
        canvas.save();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).
                getDefaultDisplay();
        Log.e(TAG, "Size: "+actionBarSize);
        // Hardcode all x, y and radius in all screen devices
        float deviceDensity = context.getResources().getDisplayMetrics().density;
        Log.e(TAG, "Device density: "+deviceDensity);
        // Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_academic_area_2d);
        int width = DeviceHelper.getScreenWidth();
        // int height = DeviceHelper.getScreenHeight() /*- actionBarSize*/;
        int height = DeviceHelper.getScreenHeight() - actionBarSize;
        Point size = new Point();
        display.getSize(size);
        /*int width = size.x;
        int height = size.y;*/
        Log.e(TAG, "Width: "+width+" Height: "+DeviceHelper.getScreenHeight());
        // Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
        scaledBitmap = Bitmap.createScaledBitmap(image, width, height, false);
        canvas.drawBitmap(scaledBitmap, 0, 0, null);
        canvas.restore();
        float radius = dipToPixels(this.context, 5);
        // Upper slot
        float upperStartX = 0;
        float upperStartY = 0;
        if (width == 1440 && height == (2392 - actionBarSize)) {
            upperStartX = 190;
            upperStartY = 120;
        } else if (width == 480 && height == (800 - actionBarSize)) {
            upperStartX = 70;
            upperStartY = 45;
        } else if (width == 768 && height == (1184 - actionBarSize)) {
            upperStartX = 50;
            upperStartY = 60;
        }

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
                    if (!vacantSlots[j].equals("")) {
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
            if (width == 1440 && height == (2392 - actionBarSize)) {
                upperStartX += 100;
                upperStartY += 17;
            } else if (width == 480 && height == (800 - actionBarSize)) {
                upperStartX += 40;
                upperStartY += 7;
            } else if (width == 768 && height == (1184 - actionBarSize)) {
                upperStartX += 80;
                upperStartY += 13;
            }
        }
        Log.e(TAG, "Last Y: "+(height - upperStartY));
        Log.e(TAG, "Upper start y: "+upperStartY);
        // Below upper slot
        float belowUpperStartX = 0;
        float belowUpperStartY = 0;
        if (width == 1440 && height == (2392 - actionBarSize)) {
            belowUpperStartX = dipToPixels(this.context, 60);
            belowUpperStartY = dipToPixels(this.context, 139);
        } else if (width == 480 && height == (800 - actionBarSize)) {
            belowUpperStartX = dipToPixels(this.context, 60);
            belowUpperStartY = dipToPixels(this.context, 170);
        } else if (width == 768 && height == (1184 - actionBarSize)) {
            belowUpperStartX = dipToPixels(this.context, 30);
            belowUpperStartY = dipToPixels(this.context, 124);
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
                    if (!vacantSlots[j].equals("")) {
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
            if (width == 1440 && height == (2392 - actionBarSize)) {
                belowUpperStartX += dipToPixels(this.context, 30);
                belowUpperStartY -= dipToPixels(this.context, (float) 10.5);
            } else if (width == 480 && height == (800 - actionBarSize)) {
                belowUpperStartX += dipToPixels(this.context, 35);
                belowUpperStartY -= dipToPixels(this.context, (float) 11.5);
            } else if (width == 768 && height == (1184 - actionBarSize)) {
                belowUpperStartX += dipToPixels(this.context, 32);
                belowUpperStartY -= dipToPixels(this.context, (float) 9.5);
            }
        }
        Log.e(TAG, "Last X: "+belowUpperStartX);
        // Middle slot
        float middleUpperStartX = 0;
        float middleUpperStartY = 0;
        if (width == 1440 && height == (2392 - actionBarSize)) {
            middleUpperStartX = dipToPixels(this.context, 235);
            middleUpperStartY = dipToPixels(this.context, 120);
        } else if (width == 480 && height == (800 - actionBarSize)) {
            middleUpperStartX = dipToPixels(this.context, 270);
            middleUpperStartY = dipToPixels(this.context, 120);
        } else if (width == 768 && height == (1184 - actionBarSize)) {
            middleUpperStartX = dipToPixels(this.context, 216);
            middleUpperStartY = dipToPixels(this.context, 80);
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
                    if (!vacantSlots[j].equals("")) {
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
            if (width == 1440 && height == (2392 - actionBarSize)) {
                middleUpperStartX += dipToPixels(this.context, 7);
                middleUpperStartY += dipToPixels(this.context, 25);
            } else if (width == 480 && height == (800 - actionBarSize)) {
                middleUpperStartX += dipToPixels(this.context, 10);
                middleUpperStartY += dipToPixels(this.context, 40);
            } else if (width == 768 && height == (1184 - actionBarSize)) {
                middleUpperStartX += dipToPixels(this.context, 8);
                middleUpperStartY += dipToPixels(this.context, 30);
            }
        }

        if (width == 1440 && height == (2392 - actionBarSize)) {
            middleUpperStartY += 50;
            middleUpperStartX -= 24;
        } else if (width == 480 && height == (800 - actionBarSize)) {
            middleUpperStartY += 3;
            middleUpperStartX -= 16;
        } else if (width == 768 && height == (1184 - actionBarSize)) {
            middleUpperStartX -= 16;
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
                    if (!vacantSlots[j].equals("")) {
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
            if (width == 1440 && height == (2392 - actionBarSize)) {
                middleUpperStartX += dipToPixels(this.context, 6);
                middleUpperStartY += dipToPixels(this.context, 23);
            } else if (width == 480 && height == (800 - actionBarSize)) {
                middleUpperStartX += dipToPixels(this.context, (float) 6.5);
                middleUpperStartY += dipToPixels(this.context, 28);
            } else if (width == 768 && height == (1184 - actionBarSize)) {
                middleUpperStartX += dipToPixels(this.context, (float) 5.5);
                middleUpperStartY += dipToPixels(this.context, 20);
            }
        }

        // Left side slot
        float leftUpperStartX = 0;
        float leftUpperStartY = 0;
        if (width == 1440 && height == (2392 - actionBarSize)) {
            leftUpperStartX = dipToPixels(this.context, 116);
            leftUpperStartY = dipToPixels(this.context, 510);
        } else if (width == 480 && height == (800 - actionBarSize)) {
            leftUpperStartX = dipToPixels(this.context, 130);
            leftUpperStartY = dipToPixels(this.context, 605);
        } else if (width == 768 && height == (1184 - actionBarSize)) {
            leftUpperStartX = dipToPixels(this.context, 110);
            leftUpperStartY = dipToPixels(this.context, 438);
        }

        for (int i = 25; i <= 38; i++) {
            float x = leftUpperStartX * ((float) width / (float) scaledBitmap.getWidth());
            float y = leftUpperStartY * ((float) height / (float) scaledBitmap.getHeight());
            boolean flag = false;
            circle.setColor(occupied);

            for (int j = 0; j <= vacantSlots.length; j++) {
                try {
                    /**
                     * if flag is true it means it is available slots
                     */
                    if (!vacantSlots[j].equals("")) {
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
            if (width == 1440 && height == (2392 - actionBarSize)) {
                leftUpperStartX += dipToPixels(this.context, 15);
                leftUpperStartY -= dipToPixels(this.context, 5);
            } else if (width == 480 && height == (800 - actionBarSize)) {
                leftUpperStartX += dipToPixels(this.context, 17);
                leftUpperStartY -= dipToPixels(this.context, (float) 5.5);
            } else if (width == 768 && height == (1184 - actionBarSize)) {
                leftUpperStartX += dipToPixels(this.context, 14);
                leftUpperStartY -= dipToPixels(this.context, (float) 4.5);
            }
        }

        // Left side slot
        float belowLeftUpperStartX = 0;
        float belowLeftUpperStartY = 0;
        if (width == 1440 && height == (2392 - actionBarSize)) {
            belowLeftUpperStartX = dipToPixels(this.context, 140);
            belowLeftUpperStartY = dipToPixels(this.context, 590);
        } else if (width == 480 && height == (800 - actionBarSize)) {
            belowLeftUpperStartX = dipToPixels(this.context, 154);
            belowLeftUpperStartY = dipToPixels(this.context, 708);
        } else if (width == 768 && height == (1184 - actionBarSize)) {
            belowLeftUpperStartX = dipToPixels(this.context, 130);
            belowLeftUpperStartY = dipToPixels(this.context, 506);
        }

        for (int i = 39; i <= 52; i++) {
            float x = belowLeftUpperStartX * ((float) width / (float) scaledBitmap.getWidth());
            float y = belowLeftUpperStartY * ((float) height / (float) scaledBitmap.getHeight());
            boolean flag = false;
            circle.setColor(occupied);

            for (int j = 0; j <= vacantSlots.length; j++) {
                try {
                    /**
                     * if flag is true it means it is available slots
                     */
                    if (!vacantSlots[j].equals("")) {
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
            if (width == 1440 && height == (2392 - actionBarSize)) {
                belowLeftUpperStartX += dipToPixels(this.context, 15);
                belowLeftUpperStartY -= dipToPixels(this.context, 5);
            } else if (width == 480 && height == (800 - actionBarSize)) {
                belowLeftUpperStartX += dipToPixels(this.context, 17);
                belowLeftUpperStartY -= dipToPixels(this.context, (float) 6);
            } else if (width == 768 && height == (1184 - actionBarSize)) {
                belowLeftUpperStartX += dipToPixels(this.context, 14);
                belowLeftUpperStartY -= dipToPixels(this.context, (float) 4.5);
            }
        }

        // Left side slot
        float rightSideUpperStartX = 0;
        float rightSideUpperStartY = 0;
        if (width == 1440 && height == (2392 - actionBarSize)) {
            rightSideUpperStartX = dipToPixels(this.context, 332);
            rightSideUpperStartY = dipToPixels(this.context, 280);
        } else if (width == 480 && height == (800 - actionBarSize)) {
            rightSideUpperStartX = dipToPixels(this.context, 385);
            rightSideUpperStartY = dipToPixels(this.context, 320);
        } else if (width == 768 && height == (1184 - actionBarSize)) {
            rightSideUpperStartX = dipToPixels(this.context, 310);
            rightSideUpperStartY = dipToPixels(this.context, 230);
        }

        for (int i = 53; i <= 61; i++) {
            float x = rightSideUpperStartX * ((float) width / (float) scaledBitmap.getWidth());
            float y = rightSideUpperStartY * ((float) height / (float) scaledBitmap.getHeight());
            boolean flag = false;
            circle.setColor(occupied);

            for (int j = 0; j <= vacantSlots.length; j++) {
                try {
                    /**
                     * if flag is true it means it is available slots
                     */
                    if (!vacantSlots[j].equals("")) {
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
        scaleDetector.onTouchEvent(event);
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
                    if (parkingArea == 3) {
                        Log.e(TAG, "Touch X: "+touchX);
                        for (int i = 1; i < maxAcademicHsParkingSlot; i++) {
                            int nearestMatchXAxis = 0;
                            nearestIndex = 0;
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
                                    if (!vacantSlots[k].equals("")) {
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
                                        if (!vacantSlots[l].equals("")) {
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
                                    // String newSlots = "";
                                    Log.e(TAG, "Slot length: "+vacantSlots.length);
                                    builder.setTitle("Alert")
                                            .setMessage("Are you sure you want to vacate this slot?")
                                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    for (int l = 0; l < vacantSlots.length + 1; l++) {
                                                        if (l  == vacantSlots.length) {
                                                            newSlots += nearestIndex;
                                                        } else {
                                                            newSlots += vacantSlots[l]+ " ";
                                                        }
                                                    }
                                                    Log.e(TAG, "Vacant Slots Else: "+newSlots);
                                                    /*Log.e(TAG, "Found: "+found+" Occupied index: "+
                                                            nearestIndex+" New Slot: "+newSlots);*/

                                                    String []x = newSlots.split(" ");
                                                    vacantSlots = StringHelper.implode(", ", x).split(", ");
                                                    // Update now the table
                                                    parking.updateParkingAreaSlot(StringHelper.implode(", ", x),
                                                            parkingArea, String.valueOf(nearestIndex), "vacant");
                                                    // Tell the View to redraw the Canvas
                                                    invalidate();
                                                }
                                            }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // pressed = false
                                        }
                                    }).show();
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

    private class ScaleListener extends
            ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 10.0f));
            invalidate();
            return true;
        }
    }
}
