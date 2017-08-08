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
import android.os.Build;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.citparkingsystem.R;
import com.citparkingsystem.requests.Parking;

/**
 * Created by Dave Tolentin on 8/2/2017.
 */

public class ParkingAreaHelper extends View {

    private static String TAG = ParkingAreaHelper.class.getSimpleName();

    private SharedPreferences sharedPreferences;
    private String parkingArea;
    private Paint circle = new Paint();
    private Parking parking;
    private Context context;
    private AlertDialog.Builder builder;

    private int maxAcademicAreaParkingSlot = 40;
    private float []xAxis = {};
    private float []yAxis = {};
    private String []vacantSlots = {};
    private boolean changeColor = false;
    private int slotIndex = 0;
    private boolean pressed = true;

    public ParkingAreaHelper(Context context, String parkingArea) {
        super(context);
        this.context = context;
        parking = new Parking(context);
        sharedPreferences = context.getSharedPreferences("CIT_PARKING_SYSTEM", Context.MODE_PRIVATE);
        this.parkingArea = parkingArea;
        parking.getParkingSlots(parkingArea);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(context);
        }
        vacantSlots = this.sharedPreferences.getString("keySlots", "").split(",");
        if (parkingArea == "academic") {
            xAxis = new float[maxAcademicAreaParkingSlot];
            yAxis = new float[maxAcademicAreaParkingSlot];
        }
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        // Hardcode all x, y and radius in all screen devices
        float deviceDensity = context.getResources().getDisplayMetrics().density;
        Log.e(TAG, "Device density: "+deviceDensity);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.mapping2);
        int width = DeviceHelper.getScreenWidth();
        int height = DeviceHelper.getScreenHeight();

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
        canvas.drawBitmap(scaledBitmap, 0, 0, null);

        // Upper slot
        int upperStartX = 20;
        int upperStartY = 50;
        int radius = 10;
        int vacant = Color.GREEN;
        int occupied = Color.RED;

        for (int i = 1; i <= 8; i++) {
            float x = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, upperStartX,
                    context.getResources().getDisplayMetrics());
            float y = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, upperStartY,
                    context.getResources().getDisplayMetrics());
            boolean flag = false;
            circle.setColor(occupied);

            for (int j = 0; j <= vacantSlots.length; j++) {
                try {
                    /**
                     * if flag is true it means it is available slots
                     */
                    if (i == Integer.parseInt(vacantSlots[j].trim())) {
                        flag = true;
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

            // Change the color if toggle/un toggle
            if (changeColor && i == slotIndex) {
                if (flag) {
                    circle.setColor(occupied); // vacant to occupied
                } else {
                    circle.setColor(vacant); // occupied to vacant
                }
            }
            canvas.drawCircle(x, y, radius, circle);
            upperStartX += 20;
            upperStartY += 10;
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
                    changeColor = true;
                    slotIndex = 1;

                    if (parkingArea == "academic") {
                        Log.e(TAG, "Touch X: "+touchX);
                        for (int i = 1; i < maxAcademicAreaParkingSlot; i++) {
                            int nearestMatchXAxis = 0;
                            int nearestIndex = 0;
                            int distance = Math.abs((int)xAxis[0] - Math.round(touchX));
                            for (int j = 0; j < xAxis.length; j++) {
                                int cDistance = Math.abs((int)xAxis[j] - Math.round(touchX));
                                if (cDistance < distance){
                                    nearestMatchXAxis = (int)xAxis[j];
                                    nearestIndex = j;
                                    distance = cDistance;
                                }
                            }
                            if (nearestIndex > 0) {
                                Log.e(TAG, "X slot: "+xAxis[nearestIndex]);
                                Log.e(TAG, "Nearest X Axis: "+nearestMatchXAxis+" Nearest Index: "+nearestIndex);
                                break;
                            }
                        }
                    }

                    // Add here the remaining parking area

                    // Remove the current slot index if set to occupied
                    // else add slot index if set to vacant
                    // Update now the table
                    parking.updateParkingAreaSlot("1", parkingArea);

                    // Query again for new slots
                    parking.getParkingSlots(parkingArea);
                }
                break;
        }
        // Tell the View to redraw the Canvas
        invalidate();

        // Tell the View that we handled the event
        return true;
    }
}
