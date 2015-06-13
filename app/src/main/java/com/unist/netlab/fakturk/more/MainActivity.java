package com.unist.netlab.fakturk.more;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

    SensorManager SM;
    TextView tv;
    TextView tvMain;

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
            layoutParams.leftMargin= (int) (layoutParams.leftMargin+se.values[0]);
            layoutParams.rightMargin = (int) (layoutParams.rightMargin+se.values[0]);
            layoutParams.topMargin = (int) (layoutParams.topMargin+se.values[2]);
            layoutParams.bottomMargin = (int) (layoutParams.bottomMargin+se.values[2]);

            tvMain.setLayoutParams(layoutParams);
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
