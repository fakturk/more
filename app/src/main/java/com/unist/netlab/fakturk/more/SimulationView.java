package com.unist.netlab.fakturk.more;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;


/**
 * TODO: document your custom view class.
 */
public class SimulationView extends View implements SensorEventListener{

   // private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Display mDisplay;

    SensorManager SM;
    TextView tv;
    TextView tvMain;

    private float mXOrigin;
    private float mYOrigin;
    private float mHorizontalBound;
    private float mVerticalBound;

    private float mSensorX;
    private float mSensorY;
    private float mSensorZ;
    private long mSensorTimeStamp;

    public float oldX, oldY;

    private static final int BALL_SIZE = 32;
    private static final int HOLE_SIZE = 40;

    public SimulationView(Context context) {
        super(context);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.simulation_view, null);
        SM = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        tv = (TextView)v.findViewById(R.id.textView);
        tv.setText("deneme");
       // tv = (TextView)getChildAt(0);
        //tvMain = (TextView)getChildAt(1);
        //tvMain = (TextView)findViewById(R.id.textViewMain);

        SM.registerListener(this, SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL);
        SM.registerListener(this, SM.getDefaultSensor(Sensor.TYPE_ORIENTATION),SensorManager.SENSOR_DELAY_GAME);

        WindowManager mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mDisplay = mWindowManager.getDefaultDisplay();
        //init();

    }

    public SimulationView(Context context, View view)
    {
        super(context, (AttributeSet) view);
        init(view);
    }

    public void init(View view)
    {
        tv = (TextView)view.findViewById(R.id.textView);
        //tv = (TextView)getChildAt(0);
        //tvMain = (TextView)getChildAt(1);
        //tvMain = (TextView)findViewById(R.id.textViewMain);
        tv.setText("deneme");
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mXOrigin = w * 0.5f;
        mYOrigin = h * 0.5f;

        mHorizontalBound = (w - BALL_SIZE) * 0.5f;
        mVerticalBound = (h - BALL_SIZE) * 0.5f;
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


    }

    /**
     * Gets the example string attribute value.
     *
     * @return The example string attribute value.
     */


    @Override
    public void onSensorChanged(SensorEvent se) {
        if (se.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
        {

            Log.v("fakturk", "x: " + se.values[0] + ", y: " + se.values[1] + " , z: " + se.values[2]);
            switch (mDisplay.getRotation()) {
                case Surface.ROTATION_0:
                    mSensorX = se.values[0];
                    mSensorY = se.values[1];
                    break;
                case Surface.ROTATION_90:
                    mSensorX = -se.values[1];
                    mSensorY = se.values[0];
                    break;
                case Surface.ROTATION_180:
                    mSensorX = -se.values[0];
                    mSensorY = -se.values[1];
                    break;
                case Surface.ROTATION_270:
                    mSensorX = se.values[1];
                    mSensorY = -se.values[0];
                    break;
            }
            mSensorZ = se.values[2];
            mSensorTimeStamp = se.timestamp;
//            Particle corner = new Particle();
           // corner.updatePosition(mSensorX, mSensorY, mSensorZ, mSensorTimeStamp);
            //corner.resolveCollisionWithBounds(mHorizontalBound, mVerticalBound);
            String SD = "";
            for (int i = 0; i < se.values.length; i++)
            {
               // SD += "values[" + i + "] : " + se.values[i] +"\n";
            }
            //SD +="mPosX : "+corner.mPosX+"\n";
           // SD +="mPosY : "+corner.mPosY+"\n";



            tv.setText(SD);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(150, 50);
           // layoutParams = (RelativeLayout.LayoutParams) tvMain.getLayoutParams();
            //layoutParams.leftMargin = (int) (layoutParams.leftMargin - (se.values[1]-oldX));
            //layoutParams.rightMargin = -250;
            //layoutParams.topMargin = (int) (layoutParams.topMargin - (se.values[2]-oldY));
            //layoutParams.bottomMargin = -250;

           // tvMain.setLayoutParams(layoutParams);
            oldX = (int) (se.values[1]);
            oldY = (int) (se.values[2]);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
