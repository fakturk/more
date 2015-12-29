package com.unist.netlab.fakturk.more;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SensorService extends Service
{
    CSVWriter writer = null;

    private String text, text_acc="not catched\n", text_gyr="not catched\n", text_gra="not catched\n";
    SensorManager SM;

    public static final String ACTION_SENSOR_BROADCAST = SensorService.class.getName() + "SensorBroadcast";

    static final String LOG_TAG = "SimpleService";

    Intent intent = new Intent(SensorService.this, MainActivity.class);


    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();


       return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "Service Created", Toast.LENGTH_LONG).show();
        Log.d(LOG_TAG, "onStartCommand");
        SM = (SensorManager)getSystemService(SENSOR_SERVICE);
        SensorEventListener sL = new SensorEventListener() {
            @Override
            public void onAccuracyChanged(Sensor arg0, int arg1) {
            }
            @Override
            public void onSensorChanged(SensorEvent se) {
                Log.d(LOG_TAG, "onSensorChanged");

                sendBroadcastMessage(se);



                //Intent bIntent = new Intent(SensorService.this, MainActivity.class);
                //new SensorEventLoggerTask().execute(se);



               // intent.putExtra("measurement",text);
                //sendBroadcast(intent);
            }
        };
        SM.registerListener(sL, SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        SM.registerListener(sL, SM.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_NORMAL);
        SM.registerListener(sL, SM.getDefaultSensor(Sensor.TYPE_GRAVITY), SensorManager.SENSOR_DELAY_NORMAL);


    }


    NotificationCompat.Builder mCompatBuilder;
    NotificationManager nm;
    Intent notiIntent;


    @Override
    public IBinder onBind(Intent intent)
    {
        /*
        Toast.makeText(this, "onBind - Service Started", Toast.LENGTH_LONG).show();

        SM = (SensorManager)getSystemService(SENSOR_SERVICE);
        SM.registerListener(sL, SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        SM.registerListener(sL, SM.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_NORMAL);
        SM.registerListener(sL, SM.getDefaultSensor(Sensor.TYPE_GRAVITY), SensorManager.SENSOR_DELAY_NORMAL);



        //nm.notify(333, mCompatBuilder.build());

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent bIntent = new Intent(SensorService.this, MainActivity.class);
        PendingIntent pbIntent = PendingIntent.getActivity(SensorService.this, 0 , bIntent, 0);
        NotificationCompat.Builder bBuilder =
                new NotificationCompat.Builder(this)
                        //.setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("Title")
                        .setContentText("Subtitle")
                        .setAutoCancel(true)
                        .setOngoing(true)
                        .setContentIntent(pbIntent);
        Notification barNotif = bBuilder.build();
        this.startForeground(1, barNotif);
        return mBinder;

        */
        return null;


    }
    private final AIDLService.Stub mBinder = new AIDLService.Stub() {
        //Write function of AIDLService
        public String func(){
            text = "";
            text += "Accelerometer\n" + text_acc;
            text += "Gyroscope\n" + text_gyr;
            text += "Gravity\n" + text_gra;

            return text;
        }
    };


    private void sendBroadcastMessage(SensorEvent se) {
        if (se != null) {
            Intent intent = new Intent(ACTION_SENSOR_BROADCAST);
            String[] entries = new String[9];
            switch(se.sensor.getType()){
                case Sensor.TYPE_ACCELEROMETER :
                    text_acc = "";
                    text_acc += "X = " + se.values[0] + "\n";
                    text_acc += "Y = " + se.values[1] + "\n";
                    text_acc += "Z = " + se.values[2] + "\n";
                    entries[0] = String.valueOf(se.values[0]);
                    entries[1] = String.valueOf(se.values[1]);
                    entries[2] = String.valueOf(se.values[2]);

                    //Change the text of notification to accelerometer using setContetnText
                    //If you change the notification then you notify the notification once again
                    //When you use notify() function ID is must be same to precious one

                case Sensor.TYPE_GYROSCOPE :
                    text_gyr = "";
                    text_gyr += "X = " + se.values[0] + "\n";
                    text_gyr += "Y = " + se.values[1] + "\n";
                    text_gyr += "Z = " + se.values[2] + "\n";
                    entries[3] = String.valueOf(se.values[0]);
                    entries[4] = String.valueOf(se.values[1]);
                    entries[5] = String.valueOf(se.values[2]);
                case Sensor.TYPE_GRAVITY :
                    text_gra = "";
                    text_gra += "X = " + se.values[0] + "\n";
                    text_gra += "Y = " + se.values[1] + "\n";
                    text_gra += "Z = " + se.values[2] + "\n";
                    entries[6] = String.valueOf(se.values[0]);
                    entries[7] = String.valueOf(se.values[1]);
                    entries[8] = String.valueOf(se.values[2]);
            }
            intent.putExtra("ACC", text_acc);
            intent.putExtra("GYR", text_gyr);
            intent.putExtra("GRA", text_gra);

            try {
                writer = new CSVWriter(new FileWriter(Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator+"SensorData.csv"),',');
                writer.writeNext(entries);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }


            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }


    @Override
    public boolean onUnbind(Intent intent){
        //SM.unregisterListener(sL);
//		nm.cancel(222);
        super.onUnbind(intent);
        return true;
    }
    @Override
    public void onDestroy(){

        Toast.makeText(this, "Service Stopped", Toast.LENGTH_LONG).show();
        Log.d( LOG_TAG, "onDestroy" );
        //SM.unregisterListener(sL);
        //nm.cancel(333);
        super.onDestroy();
    }

}

class SensorEventLoggerTask extends
        AsyncTask<SensorEvent, Void, Void>
{
    @Override
    protected Void doInBackground(SensorEvent... events) {
        SensorEvent event = events[0];
        // log the value
        return null;
    }
}

