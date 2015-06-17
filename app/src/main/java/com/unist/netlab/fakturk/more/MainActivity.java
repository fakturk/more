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


public class MainActivity extends ActionBarActivity {

    SensorManager SM;
    TextView tv;
    TextView tvMain;
    Button buttonUp, buttonDown;

    RelativeLayout root;



    SensorEventListener sL = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent se) {
            String SD="";
            for (int i=0; i<se.values.length;i++)
            {
                SD+="values["+i+"] : "+se.values[i]+"`\n";
            }
            tv.setText(SD);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(150, 50);
            layoutParams = (RelativeLayout.LayoutParams)tvMain.getLayoutParams();
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


            tvMain.setLayoutParams(layoutParams);
            tvMain.invalidate();

            int top = root.getTop();
            int bottom = root.getBottom();
            int left = root.getLeft();
            int right = root.getRight();
            int height = tvMain.getHeight();
            int width = tvMain.getWidth();

            if(tvMain.getX() + linear_acceleration[0]>left)
            {
                tvMain.setX(tvMain.getX() + linear_acceleration[0]);
            }
            else if (tvMain.getX() + linear_acceleration[0]<=left)
            {
                tvMain.setX(left);
            }
            if(tvMain.getX() + linear_acceleration[0]<right)
            {
                tvMain.setX(tvMain.getX() + linear_acceleration[0]);
            }
            else if (tvMain.getX() + linear_acceleration[0]>=right)
            {
                tvMain.setX(right);
            }
            if ((tvMain.getY()-(linear_acceleration[1]-se.values[2]))>top)
            {
                tvMain.setY((float)(tvMain.getY()-(linear_acceleration[1]-se.values[2])));
            }
            else if((tvMain.getY()-(linear_acceleration[1]-se.values[2]))<=top)
            {
                tvMain.setY(top);
            }
            if ((tvMain.getY()-(linear_acceleration[1]-se.values[2]))<bottom)
            {
                tvMain.setY((float)(tvMain.getY()-(linear_acceleration[1]-se.values[2])));
            }
            else if((tvMain.getY()-(linear_acceleration[1]-se.values[2]))>=bottom)
            {
                tvMain.setY(bottom);
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

        SM = (SensorManager) getSystemService(SENSOR_SERVICE);
        tv = (TextView)findViewById(R.id.textView);
        tvMain = (TextView)findViewById(R.id.textViewMain);
        buttonUp =(Button)findViewById(R.id.buttonUp);
        buttonDown = (Button)findViewById(R.id.buttonDown);
        SM.registerListener(sL, SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST);


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

