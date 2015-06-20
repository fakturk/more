package com.unist.netlab.fakturk.more;

import android.app.Activity;
import android.graphics.Canvas;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import static android.util.FloatMath.cos;
import static android.util.FloatMath.sin;
import static android.util.FloatMath.sqrt;


public class MainActivity extends ActionBarActivity {

    SensorManager SM;
    TextView tv, tv2;
    TextView tvMain;
    Button buttonUp, buttonDown;

    RelativeLayout root;





    SensorEventListener sL = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent se) {
            move(se);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    void move(SensorEvent se)
    {
        String SD = "";
        int originalHeight = tvMain.getHeight();
        int originalWidth = tvMain.getWidth();

        int top = root.getTop();
        int bottom = root.getBottom();
        int left = root.getLeft();
        int right = root.getRight();
        int height = tvMain.getHeight();
        int width = tvMain.getWidth();


        if (se.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            for (int i = 0; i < se.values.length; i++)
            {
                SD += "values[" + i + "] : " + se.values[i] + "`\n";
            }
            tv.setText(SD);
            //RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(150, 50);
            //layoutParams = (RelativeLayout.LayoutParams) tvMain.getLayoutParams();
            //layoutParams.leftMargin= (int) (layoutParams.leftMargin+se.values[1]-9.8-se.values[2]);
            //layoutParams.rightMargin = (int) (layoutParams.rightMargin+se.values[0]);
            //layoutParams.topMargin = (int) (layoutParams.topMargin+se.values[1]-9.8-se.values[2]);
            //layoutParams.bottomMargin = (int) (layoutParams.bottomMargin+se.values[1]-9.8-se.values[2]);

            final float alpha = (float) 0.8;

            float[] gravity = new float[3];
            gravity[0] = alpha * gravity[0] + (1 - alpha) * se.values[0];
            gravity[1] = alpha * gravity[1] + (1 - alpha) * se.values[1];
            gravity[2] = alpha * gravity[2] + (1 - alpha) * se.values[2];

            float[] linear_acceleration = new float[3];
            linear_acceleration[0] = se.values[0] - gravity[0];
            linear_acceleration[1] = se.values[1] - gravity[1];
            linear_acceleration[2] = se.values[2] - gravity[2];


           // tvMain.setLayoutParams(layoutParams);
            //tvMain.invalidate();



          //  tvMain.setX((left+right)/2);
          //  tvMain.setY((top+bottom)/2);

            /*
            if (tvMain.getX() + linear_acceleration[0] > left) {
                tvMain.setX(tvMain.getX() + linear_acceleration[0]);
            } else if (tvMain.getX() + linear_acceleration[0] <= left) {
                tvMain.setX(left);
            }
            if (tvMain.getX() + width + linear_acceleration[0] < right) {
                tvMain.setX(tvMain.getX() + linear_acceleration[0]);
            } else if (tvMain.getX() + width + linear_acceleration[0] >= right) {
                tvMain.setX(right - width);
            }
            if ((tvMain.getY() - (linear_acceleration[1] - se.values[2])) > top) {
                tvMain.setY((float) (tvMain.getY() - (linear_acceleration[1] - se.values[2])));
            } else if ((tvMain.getY() - (linear_acceleration[1] - se.values[2])) <= top) {
                tvMain.setY(top);
            }
            if ((tvMain.getY() + height - (linear_acceleration[1] - se.values[2])) < bottom) {
                tvMain.setY((float) (tvMain.getY() - (linear_acceleration[1] - se.values[2])));
            } else if ((tvMain.getY() + height - (linear_acceleration[1] - se.values[2])) >= bottom) {
                tvMain.setY(bottom - height);
            }
            */


        }
        if (se.sensor.getType() == Sensor.TYPE_GYROSCOPE)
        {
            for (int i = 0; i < se.values.length; i++)
            {
                SD += "values[" + (i+3) + "] : " + se.values[i] + "`\n";
            }

            tv2.setText(SD);
             final float NS2S = 1.0f / 1000000000.0f;
             final float[] deltaRotationVector = new float[4];
            float timestamp = 0;
            final double EPSILON = 0.000001;
            if (timestamp != 0) {
                final float dT = (se.timestamp - timestamp) * NS2S;
                // Axis of the rotation sample, not normalized yet.
                float axisX = se.values[0];
                float axisY = se.values[1];
                float axisZ = se.values[2];

                // Calculate the angular speed of the sample
                float omegaMagnitude = sqrt(axisX*axisX + axisY*axisY + axisZ*axisZ);

                // Normalize the rotation vector if it's big enough to get the axis
                if (omegaMagnitude > EPSILON) {
                    axisX /= omegaMagnitude;
                    axisY /= omegaMagnitude;
                    axisZ /= omegaMagnitude;
                }

                // Integrate around this axis with the angular speed by the timestep
                // in order to get a delta rotation from this sample over the timestep
                // We will convert this axis-angle representation of the delta rotation
                // into a quaternion before turning it into the rotation matrix.
                float thetaOverTwo = omegaMagnitude * dT / 2.0f;
                float sinThetaOverTwo = sin(thetaOverTwo);
                float cosThetaOverTwo = cos(thetaOverTwo);
                deltaRotationVector[0] = sinThetaOverTwo * axisX;
                deltaRotationVector[1] = sinThetaOverTwo * axisY;
                deltaRotationVector[2] = sinThetaOverTwo * axisZ;
                deltaRotationVector[3] = cosThetaOverTwo;
            }
            timestamp = se.timestamp;
            float[] deltaRotationMatrix = new float[9];
            SensorManager.getRotationMatrixFromVector(deltaRotationMatrix, deltaRotationVector);
            /*
            for (int i = 0; i < deltaRotationMatrix.length; i++)
            {
                SD += "delta [" + (i+3) + "] : " + deltaRotationMatrix[i] + "`\n";
            }
            */

            tv2.setText(SD);
            //tvMain.setX(originalWidth*2);
            //tvMain.setY(originalHeight*2);

            if (tvMain.getX() - se.values[1] +se.values[2]> left) {
                smoothMove('x', tvMain.getX(), (-se.values[1] + se.values[2]));
            } else if (tvMain.getX() - se.values[1] +se.values[2] <= left) {
                tvMain.setX(left);
            }
            if (tvMain.getX() + width - se.values[1] +se.values[2] < right) {
                smoothMove('x', tvMain.getX(), (-se.values[1] + se.values[2]));
            } else if (tvMain.getX() + width - se.values[1] +se.values[2] >= right) {
                tvMain.setX(right - width);
            }
            if ((tvMain.getY() - (se.values[0]  +se.values[2])) > top) {
                smoothMove('y', tvMain.getY(), -(se.values[0] + se.values[2]));
            } else if ((tvMain.getY() - (se.values[0]  +se.values[2])) <= top) {
                tvMain.setY(top);
            }
            if ((tvMain.getY() + height - (se.values[0]  +se.values[2])) < bottom) {
                smoothMove('y', tvMain.getY(), -(se.values[0] + se.values[2]));
            } else if ((tvMain.getY() + height - (se.values[0] +se.values[2] )) >= bottom) {
                tvMain.setY(bottom - height);
            }

        }
    }

    void smoothMove(char type,float position, double subtractValue)
    {
        float smooth ;
        int smoothLevel = 3;
        if (type=='x')
        {
            if (smoothLevel!=0) {
                for (int i = 0; i < smoothLevel; i++) {
                    smooth = (float) (position + subtractValue / (2 ^ (i + 1)));
                    tvMain.setX(smooth);
                }
            }
            smooth = (float) (position + subtractValue/(2^(smoothLevel)));
            tvMain.setX(smooth);

        }
        else if (type=='y')
        {
            if (smoothLevel!=0) {
                for (int i = 0; i < smoothLevel; i++) {
                    smooth = (float) (position + subtractValue / (2 ^ (i + 1)));
                    tvMain.setY(smooth);
                }
            }
                smooth = (float) (position + subtractValue/(2^(smoothLevel)));
                tvMain.setY(smooth);


        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SM = (SensorManager) getSystemService(SENSOR_SERVICE);
        tv = (TextView)findViewById(R.id.textView);
        tv2 = (TextView)findViewById(R.id.textView2);
        tvMain = (TextView)findViewById(R.id.textViewMain);
        buttonUp =(Button)findViewById(R.id.buttonUp);
        buttonDown = (Button)findViewById(R.id.buttonDown);
        SM.registerListener(sL, SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST);
        SM.registerListener(sL, SM.getDefaultSensor(Sensor.TYPE_GYROSCOPE),SensorManager.SENSOR_DELAY_GAME);


        root = (RelativeLayout) findViewById(R.id.root);

        buttonUp.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                DisplayMetrics metrics;
                metrics = getApplicationContext().getResources().getDisplayMetrics();
                float Textsize =tv.getTextSize()/metrics.density;
                tvMain.setTextSize(Textsize*2);
            }
        });

        buttonDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DisplayMetrics metrics;
                metrics = getApplicationContext().getResources().getDisplayMetrics();
                float Textsize =tv.getTextSize()/metrics.density;
                tvMain.setTextSize(Textsize/2);
            }
        });

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

