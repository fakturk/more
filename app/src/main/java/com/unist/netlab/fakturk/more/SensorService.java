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
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;


public class SensorService extends Service implements SensorEventListener
{


//    private String text, text_acc="not catched\n", text_gyr="not catched\n", text_gra="not catched\n",text_lacc="not catched\n";
    private String text, text_acc="", text_gyr="";

    SensorManager SM;
    float[] ACC_DATA=new float[3];
    float[] GYR_DATA=new float[3];
//    float[] GRA_DATA=new float[3];
    float[] LACC_DATA=new float[3];

    public static final String ACTION_SENSOR_BROADCAST = SensorService.class.getName() + "SensorBroadcast";

    static final String LOG_TAG = "SimpleService";

    long mSensorTimeStamp;

//    Intent intent = new Intent(SensorService.this, MainActivity.class);
//    NotificationCompat.Builder mCompatBuilder;
//    NotificationManager nm;
//    Intent notiIntent;
//
//
//    int id_To_Update = 0;


    File sd = Environment.getExternalStorageDirectory();
    Calendar c = Calendar.getInstance();
    //String path = sd + "/" + "SensorData" +c.getTime()+ ".xml";
    String path_acc = sd + "/" + "SensorDataAcc" +c.getTime()+ ".xml";
    String path_gyr = sd + "/" + "SensorDataGyr" +c.getTime()+ ".xml";


    //String mDestXmlFilename=path;
    String mDestXmlFilenameAcc=path_acc;
    String mDestXmlFilenameGyr=path_gyr;


    //File myFile = new File(mDestXmlFilename);
    File fileAcc = new File(mDestXmlFilenameAcc);
    File fileGyr = new File(mDestXmlFilenameGyr);
    //BufferedOutputStream bos;
    BufferedOutputStream bos_acc;
    BufferedOutputStream bos_gyr;





    @Override
    public void onSensorChanged(SensorEvent se)
    {
//        Log.d(LOG_TAG, "onSensorChanged");
//
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


        FileOutputStream fOut = null;
        FileOutputStream fOutAcc = null;
        FileOutputStream fOutGyr = null;
        try
        {
           // myFile.createNewFile();
            fileAcc.createNewFile();
            fileGyr.createNewFile();
            //fOut = new FileOutputStream(myFile);
            fOutAcc = new FileOutputStream(fileAcc);
            fOutGyr = new FileOutputStream(fileGyr);
            //bos = new BufferedOutputStream(fOut);
            bos_acc = new BufferedOutputStream(fOutAcc);
            bos_gyr = new BufferedOutputStream(fOutGyr);
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }




        SM.registerListener(this, SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST);
        SM.registerListener(this, SM.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_FASTEST);
       // SM.registerListener(this, SM.getDefaultSensor(Sensor.TYPE_GRAVITY), SensorManager.SENSOR_DELAY_FASTEST);

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
//        Log.d( LOG_TAG, "onDestroy" );
        SM.unregisterListener(this);
        try
        {
            //bos.close();
            bos_acc.close();
            bos_gyr.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

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
            //float[] entries = new float[9];



            switch(se.sensor.getType()){
                case Sensor.TYPE_ACCELEROMETER :
                    text_acc = "";
                    text_acc += "X = " + se.values[0] + "\n";
                    text_acc += "Y = " + se.values[1] + "\n";
                    text_acc += "Z = " + se.values[2] + "\n";
                    ACC_DATA[0] = se.values[0];
                    ACC_DATA[1] = se.values[1];
                    ACC_DATA[2] = se.values[2];
                    break;

//                    Log.d("ACC", String.valueOf(se.values[0])+", "+String.valueOf(se.values[1])+", "+String.valueOf(se.values[2]));


                    //Change the text of notification to accelerometer using setContetnText
                    //If you change the notification then you notify the notification once again
                    //When you use notify() function ID is must be same to precious one

                case Sensor.TYPE_GYROSCOPE :
                    text_gyr = "";
                    text_gyr += "X = " + se.values[0] + "\n";
                    text_gyr += "Y = " + se.values[1] + "\n";
                    text_gyr += "Z = " + se.values[2] + "\n";
                    GYR_DATA[0] = se.values[0];
                    GYR_DATA[1] = se.values[1];
                    GYR_DATA[2] = se.values[2];
                    break;



//                case Sensor.TYPE_GRAVITY :
//                    text_gra = "";
//                    text_gra += "X = " + se.values[0] + "\n";
//                    text_gra += "Y = " + se.values[1] + "\n";
//                    text_gra += "Z = " + se.values[2] + "\n";
//                    GRA_DATA[0] = se.values[0];
//                    GRA_DATA[1] = se.values[1];
//                    GRA_DATA[2] = se.values[2];


//                case Sensor.TYPE_LINEAR_ACCELERATION :
//                    text_lacc = "";
//                    text_lacc += "X = " + se.values[0] + "\n";
//                    text_lacc += "Y = " + se.values[1] + "\n";
//                    text_lacc += "Z = " + se.values[2] + "\n";
//                    LACC_DATA[0] = se.values[0];
//                    LACC_DATA[1] = se.values[1];
//                    LACC_DATA[2] = se.values[2];

            }


//            Log.d("SEN", String.valueOf(ACC_DATA[0])+", "
//                    +String.valueOf(ACC_DATA[1])+", "
//                    +String.valueOf(ACC_DATA[2])+", "
//                    +String.valueOf(GYR_DATA[0])+", "
//                    +String.valueOf(GYR_DATA[1])+", "
//                    +String.valueOf(GYR_DATA[2])+", "
////                    +String.valueOf(GRA_DATA[0])+", "
////                    +String.valueOf(GRA_DATA[1])+", "
////                    +String.valueOf(GRA_DATA[2])
//                    );
//            Log.d("ACC_DATA", String.valueOf(ACC_DATA[0])+", "+String.valueOf(ACC_DATA[1])+", "+String.valueOf(ACC_DATA[2])+", ");


            mSensorTimeStamp = se.timestamp;
            long timeInMillis = (new Date()).getTime()
                    + (mSensorTimeStamp - System.nanoTime()) / 1000000L;

            intent.putExtra("ACC", text_acc);
//            intent.putExtra("LACC", text_lacc);
            intent.putExtra("GYR", text_gyr);
//            intent.putExtra("GRA", text_gra);

            intent.putExtra("ACC_DATA", ACC_DATA);
            intent.putExtra("GYR_DATA", GYR_DATA);
//            intent.putExtra("GRA_DATA", GRA_DATA);
            intent.putExtra("TIME",mSensorTimeStamp);
//            intent.putExtra("LACC_DATA", LACC_DATA);


//
            try
            {
                Toast.makeText(this, "Teheey", Toast.LENGTH_LONG).show();
              //  bos_acc.write("ACC\n".getBytes());
                bos_acc.write((Long.toString(timeInMillis)+" ").getBytes());
                bos_acc.write((ACC_DATA[0]+" "+ACC_DATA[1]+" "+ACC_DATA[2]+"\n").getBytes());
                //bos_gyr.write("GYR\n".getBytes());
                bos_gyr.write((Long.toString(timeInMillis)+" ").getBytes());
                bos_gyr.write((GYR_DATA[0]+" "+GYR_DATA[1]+" "+GYR_DATA[2]+"\n").getBytes());
//                bos.write("GRA\n".getBytes());
//                bos.write(text_gra.getBytes());
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }





}
