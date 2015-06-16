package com.unist.netlab.fakturk.more;

import android.content.Context;
import android.graphics.Canvas;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

    SensorManager SM;
    TextView tv;
    TextView tvMain;

    public float oldX, oldY;


    private float mSensorX;
    private float mSensorY;
    private float mSensorZ;
    private long mSensorTimeStamp;
    private Display mDisplay;

    SensorEventListener sL = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent se) {
            if (se.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            {

                Log.v("fakturk", "x: "+se.values[0]+", y: "+se.values[1]+" , z: "+se.values[2]);
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
                Particle corner = new Particle();
                corner.updatePosition(mSensorX, mSensorY, mSensorZ, mSensorTimeStamp);
                //corner.resolveCollisionWithBounds(mHorizontalBound, mVerticalBound);
                String SD = "";
                for (int i = 0; i < se.values.length; i++)
                {
                    SD += "values[" + i + "] : " + se.values[i] +"\n";
                }
                SD +="mPosX : "+corner.mPosX+"\n";
                SD +="mPosY : "+corner.mPosY+"\n";



                tv.setText(SD);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(150, 50);
                layoutParams = (RelativeLayout.LayoutParams) tvMain.getLayoutParams();
                //layoutParams.leftMargin = (int) (layoutParams.leftMargin - (se.values[1]-oldX));
                //layoutParams.rightMargin = -250;
                //layoutParams.topMargin = (int) (layoutParams.topMargin - (se.values[2]-oldY));
                //layoutParams.bottomMargin = -250;

                tvMain.setLayoutParams(layoutParams);
                oldX = (int) (se.values[1]);
                oldY = (int) (se.values[2]);
            }

        }


        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SM = (SensorManager)getSystemService(SENSOR_SERVICE);
        tv = (TextView)findViewById(R.id.textView);
        tvMain = (TextView)findViewById(R.id.textViewMain);
        SM.registerListener(sL, SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL);
        SM.registerListener(sL, SM.getDefaultSensor(Sensor.TYPE_ORIENTATION),SensorManager.SENSOR_DELAY_GAME);

        WindowManager mWindowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        mDisplay = mWindowManager.getDefaultDisplay();


    }

    public class Particle {
        /* coefficient of restitution */
        private static final float COR = 0.7f;

        public float mPosX;
        public float mPosY;
        private float mVelX;
        private float mVelY;

        public void updatePosition(float sx, float sy, float sz, long timestamp) {
            float dt = (System.nanoTime() - timestamp) / 1000000000.0f;
            mVelX += -sx * dt;
            mVelY += -sy * dt;

            mPosX += mVelX * dt;
            mPosY += mVelY * dt;
        }

        public void resolveCollisionWithBounds(float mHorizontalBound, float mVerticalBound) {
            if (mPosX > mHorizontalBound) {
                mPosX = mHorizontalBound;
                mVelX = -mVelX * COR;
            } else if (mPosX < -mHorizontalBound) {
                mPosX = -mHorizontalBound;
                mVelX = -mVelX * COR;
            }
            if (mPosY > mVerticalBound) {
                mPosY = mVerticalBound;
                mVelY = -mVelY * COR;
            } else if (mPosY < -mVerticalBound) {
                mPosY = -mVerticalBound;
                mVelY = -mVelY * COR;
            }
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
