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
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.util.Calendar;


public class SensorService extends Service implements SensorEventListener
{


    private String text, text_acc="not catched\n", text_gyr="not catched\n", text_gra="not catched\n";
    SensorManager SM;

    public static final String ACTION_SENSOR_BROADCAST = SensorService.class.getName() + "SensorBroadcast";

    static final String LOG_TAG = "SimpleService";

//    Intent intent = new Intent(SensorService.this, MainActivity.class);
//    NotificationCompat.Builder mCompatBuilder;
//    NotificationManager nm;
//    Intent notiIntent;
//
//
//    int id_To_Update = 0;


    File sd = Environment.getExternalStorageDirectory();
    Calendar c = Calendar.getInstance();
    String path = sd + "/" + "SensorData" +c.getTime()+ ".xml";


    String mDestXmlFilename=path;


    File myFile = new File(mDestXmlFilename);
    BufferedOutputStream bos;





    @Override
    public void onSensorChanged(SensorEvent se)
    {
        Log.d(LOG_TAG, "onSensorChanged");

        sendBroadcastMessage(se);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i)
    {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        SM = (SensorManager)getSystemService(SENSOR_SERVICE);


//        FileOutputStream fOut = null;
//        try
//        {
//            myFile.createNewFile();
//            fOut = new FileOutputStream(myFile);
//            bos = new BufferedOutputStream(fOut);
//        }catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }




        SM.registerListener(this, SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        SM.registerListener(this, SM.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_NORMAL);
        SM.registerListener(this, SM.getDefaultSensor(Sensor.TYPE_GRAVITY), SensorManager.SENSOR_DELAY_NORMAL);

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

        return SensorService.START_STICKY;
    }

    @Override
    public void onCreate() {
        //super.onCreate();
        Toast.makeText(this, "Service Created", Toast.LENGTH_LONG).show();
        //Log.d(LOG_TAG, "onStartCommand");



    }

    @Override
    public void onDestroy(){

        Toast.makeText(this, "Service Stopped", Toast.LENGTH_LONG).show();
        Log.d( LOG_TAG, "onDestroy" );
        SM.unregisterListener(this);
//        try
//        {
//            bos.close();
//        } catch (IOException e)
//        {
//            e.printStackTrace();
//        }

        //super.onDestroy();
    }




    @Override
    public IBinder onBind(Intent intent)
    {
        return null;


    }



    private void sendBroadcastMessage(SensorEvent se) {
        if (se != null) {
            Intent intent = new Intent(ACTION_SENSOR_BROADCAST);
            float[] entries = new float[9];
            float timestep = 0;

            switch(se.sensor.getType()){
                case Sensor.TYPE_ACCELEROMETER :
                    text_acc = "";
                    text_acc += "X = " + se.values[0] + "\n";
                    text_acc += "Y = " + se.values[1] + "\n";
                    text_acc += "Z = " + se.values[2] + "\n";
                    entries[0]=se.values[0];
                    entries[1]=se.values[1];
                    entries[2]=se.values[2];


                    //Change the text of notification to accelerometer using setContetnText
                    //If you change the notification then you notify the notification once again
                    //When you use notify() function ID is must be same to precious one

                case Sensor.TYPE_GYROSCOPE :
                    text_gyr = "";
                    text_gyr += "X = " + se.values[0] + "\n";
                    text_gyr += "Y = " + se.values[1] + "\n";
                    text_gyr += "Z = " + se.values[2] + "\n";
                    entries[3]=se.values[0];
                    entries[4]=se.values[1];
                    entries[5]=se.values[2];
                    timestep = se.timestamp;


                case Sensor.TYPE_GRAVITY :
                    text_gra = "";
                    text_gra += "X = " + se.values[0] + "\n";
                    text_gra += "Y = " + se.values[1] + "\n";
                    text_gra += "Z = " + se.values[2] + "\n";
                    entries[6]=se.values[0];
                    entries[7]=se.values[1];
                    entries[8]=se.values[2];


            }
            intent.putExtra("ACC", text_acc);
            intent.putExtra("GYR", text_gyr);
            intent.putExtra("GRA", text_gra);
            intent.putExtra("SEN",entries);
            intent.putExtra("TIME",timestep);


//
//            try
//            {
//                Toast.makeText(this, "Teheey", Toast.LENGTH_LONG).show();
//                bos.write("ACC\n".getBytes());
//                bos.write(text_acc.getBytes());
//                bos.write("GYR\n".getBytes());
//                bos.write(text_gyr.getBytes());
//                bos.write("GRA\n".getBytes());
//                bos.write(text_gra.getBytes());
//            } catch (IOException e)
//            {
//                e.printStackTrace();
//            }
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }





}
