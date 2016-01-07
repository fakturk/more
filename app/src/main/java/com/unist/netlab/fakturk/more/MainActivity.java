package com.unist.netlab.fakturk.more;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class MainActivity extends Activity
{

    //SensorManager SM;
    TextView tv;
    TextView tvMain;
    TextView tvAngle;
    Button buttonUp, buttonDown;
    Move move;
    DisplayChange displayChange;
    Display mdisp;
    Double alpha;






    RelativeLayout root;

    //MyReceiver myReceiver=null;
    Intent i;
    static final String LOG_TAG = "ServiceActivity";









    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d( LOG_TAG, "onCreate" );

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        root = (RelativeLayout) findViewById(R.id.root);



        i= new Intent(this, SensorService.class);
        Log.d(LOG_TAG, "onCreate/startService");

        //SM = (SensorManager) getSystemService(SENSOR_SERVICE);
        tv = (TextView)  findViewById(R.id.textView);

        tvMain = (TextView) findViewById(R.id.textViewMain);
        tvAngle = (TextView) findViewById(R.id.textViewAngle);
        buttonUp = (Button) findViewById(R.id.buttonSizeUp);
        buttonDown = (Button) findViewById(R.id.buttonSizeDown);
        displayChange = new DisplayChange(tv, tvAngle);
        mdisp = getWindowManager().getDefaultDisplay();
        alpha = 0.0;




        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {


                        displayChange.setDisplay(intent.getStringExtra("ACC"), intent.getStringExtra("GYR"), intent.getStringExtra("GRA"));
                        //displayChange.setTvAngle(intent.getFloatArrayExtra("ACC_DATA"), intent.getFloatArrayExtra("GYR_DATA"), intent.getFloatArrayExtra("GRA_DATA"));

                        move = new Move(intent.getFloatArrayExtra("ACC_DATA"),intent.getFloatArrayExtra("GYR_DATA"),intent.getFloatArrayExtra("GRA_DATA"), intent.getFloatExtra("TIME",0),  tv,   tvMain, tvAngle, root);
                        //move.moveIt();

                        alpha = move.rotateText(mdisp, alpha);

                    }
                }, new IntentFilter(SensorService.ACTION_SENSOR_BROADCAST)
        );

        //SM.registerListener(sL, SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST);
        //SM.registerListener(sL, SM.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_GAME);









        buttonUp.setOnClickListener(new View.OnClickListener()
        {


            @Override
            public void onClick(View v)
            {

                DisplayMetrics metrics;
                metrics = getApplicationContext().getResources().getDisplayMetrics();
                float Textsize = tv.getTextSize() / metrics.density;
                tvMain.setTextSize(Textsize * 2);
            }
        });

        buttonDown.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                DisplayMetrics metrics;
                metrics = getApplicationContext().getResources().getDisplayMetrics();
                float Textsize = tv.getTextSize() / metrics.density;
                tvMain.setTextSize(Textsize / 2);
            }
        });

//
    }

    @Override
    protected void onPause() {
        super.onPause();
        //File sd = Environment.getExternalStorageDirectory();
        //String path = sd + "/" + "SensorData.db" + ".xml";


    }


    @Override
    protected void onResume() {
        super.onResume();
        startService(new Intent(this, SensorService.class));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy()
    {


        super.onDestroy();
    }



}






