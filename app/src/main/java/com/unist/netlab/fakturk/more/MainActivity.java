package com.unist.netlab.fakturk.more;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class MainActivity extends Activity implements SensorEventListener
{

    SensorManager SM;
    TextView tv, tv2;
    TextView tvMain;
    Button buttonUp, buttonDown, buttonStopService, buttonStartService, buttonFunc;
    Move move;


    RelativeLayout root;

    MyReceiver myReceiver=null;
    Intent i;
    static final String LOG_TAG = "ServiceActivity";


     @Override
    public void onSensorChanged(SensorEvent se)
    {

        //move = new Move(se,  tv,  tv2,   tvMain,  root);
        //move.moveIt();
        callFunc();

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {

    }



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d( LOG_TAG, "onCreate" );

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        i= new Intent(this, SensorService.class);
        Log.d(LOG_TAG, "onCreate/startService");

        //SM = (SensorManager) getSystemService(SENSOR_SERVICE);
        tv = (TextView)  findViewById(R.id.textView);
        tv2 = (TextView) findViewById(R.id.textView2);
        tvMain = (TextView) findViewById(R.id.textViewMain);
        buttonUp = (Button) findViewById(R.id.buttonSizeUp);
        buttonDown = (Button) findViewById(R.id.buttonSizeDown);
        buttonStartService = (Button) findViewById(R.id.buttonStartService);
        buttonStopService = (Button) findViewById(R.id.buttonStopService);
        buttonFunc = (Button) findViewById(R.id.buttonFunc);
        buttonStopService.setEnabled(false);
        buttonFunc.setEnabled(false);

        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                       // double latitude = intent.getDoubleExtra(LocationBroadcastService.EXTRA_LATITUDE, 0);
                       // double longitude = intent.getDoubleExtra(LocationBroadcastService.EXTRA_LONGITUDE, 0);
                        tv.setText("ACC : \n"+intent.getStringExtra("ACC")+
                                        "\n"+
                                "GYR : \n"+intent.getStringExtra("GYR")+
                                         "\n"+
                                "GRA : \n"+intent.getStringExtra("GRA"));
                        //callFunc();
                    }
                }, new IntentFilter(SensorService.ACTION_SENSOR_BROADCAST)
        );

        //SM.registerListener(sL, SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST);
        //SM.registerListener(sL, SM.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_GAME);







        root = (RelativeLayout) findViewById(R.id.root);

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

        buttonStartService.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                serviceStart();


            }
        });

        buttonStopService.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                serviceStop();
            }
        });

        buttonFunc.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                callFunc();
            }
        });





    }

    @Override
    protected void onPause() {
        super.onPause();
        stopService(new Intent(this, SensorService.class));
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    AIDLService mService;
    boolean mBound;
    private ServiceConnection mConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder)
        {
            mService = AIDLService.Stub.asInterface(iBinder);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName)
        {
            mService=null;
        }
    };

    public void serviceStart()
    {

        Intent intent = new Intent(this,SensorService.class);
        getApplicationContext().bindService(intent,mConnection, Context.BIND_AUTO_CREATE);
        buttonStartService.setEnabled(false);
        buttonStopService.setEnabled(true);
        buttonFunc.setEnabled(true);

    }

    public void serviceStop()
    {
        getApplicationContext().unbindService(mConnection);
        stopService(new Intent(this,SensorService.class));
        buttonStartService.setEnabled(true);
        buttonStopService.setEnabled(false);
        buttonFunc.setEnabled(false);
    }

    private void callFunc(){
        try {
            tv.setText(mService.func());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }



}
 class MyReceiver extends BroadcastReceiver{
    static final String Log_Tag = "MyReceiver";
    @Override
    public void onReceive(Context arg0, Intent arg1){
       // Log.d(LOG_TAG, "onReceive");
        String measurement = arg1.getStringExtra("measurement");
        System.out.println("I am here");
    }

}





