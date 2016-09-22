package com.unist.netlab.fakturk.more;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Vector;


public class MainActivity extends Activity
{

    DecimalFormat df = new DecimalFormat("#.##");
    //SensorManager SM;
    TextView tv, tv2;
    TextView tvMain;
    TextView tvAngle;
    Button buttonUp, buttonDown, buttonReset, buttonCalibrate, buttonStart;
    Move move;
    DisplayChange displayChange;
    Display mdisp;
    Double alpha;
    float[] oldAcc, oldVelocity, oldDistance, oldGyr, gravity, lowPassAcc, filteredGyr, processedAcc, dynamicAcc;
    int noiseVarianceTimer, gravityTimer;
    double noiseAverage;
    double[][] noiseVariance;
    boolean isNoiseVarianceCalculated = false;
    Vector<float[]> noisyAcc, noisyGyr;
    //    Vector<float[]> tempAcc;
    float factor = 0.02f;
    int sampleNumber = 100;
    int sampleSize=1000;
    String processedAccData, processedAccDataforFile;

    FileOutputStream fOut = null;
    File sd = Environment.getExternalStorageDirectory();
    Calendar c = Calendar.getInstance();
    String path = sd + "/" + "ProcessedAccData" +c.getTime()+ ".xml";
    String mDestXmlFilename=path;
    File myFile = new File(mDestXmlFilename);
    BufferedOutputStream bos;


    StatisticCalculations stats;
    Filter lpf;
    Gravity g;
    AccProcess process;
    DynamicAcceleration dynamic;



    RelativeLayout root;

    //MyReceiver myReceiver=null;
    Intent i;
//    static final String LOG_TAG = "ServiceActivity";
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        //Log.d( LOG_TAG, "onCreate" );

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        root = (RelativeLayout) findViewById(R.id.root);


        i = new Intent(this, SensorService.class);
        //  Log.d(LOG_TAG, "onCreate/startService");

        //SM = (SensorManager) getSystemService(SENSOR_SERVICE);
        tv = (TextView) findViewById(R.id.textView);
        tv2 = (TextView) findViewById(R.id.textView2);

        tvMain = (TextView) findViewById(R.id.textViewMain);
        tvAngle = (TextView) findViewById(R.id.textViewAngle);
        buttonUp = (Button) findViewById(R.id.buttonSizeUp);
        buttonDown = (Button) findViewById(R.id.buttonSizeDown);
        buttonReset = (Button) findViewById(R.id.buttonReset);
        buttonCalibrate = (Button) findViewById(R.id.buttonCalibrate);
        buttonStart = (Button) findViewById(R.id.button_start);
        displayChange = new DisplayChange(tv, tvAngle);
        mdisp = getWindowManager().getDefaultDisplay();
        alpha = 0.0;
        oldAcc = null;
        oldGyr = null;
        lowPassAcc = new float[3];
        oldVelocity = new float[3];
        oldDistance = new float[3];
        gravity = new float[3];
        processedAcc = null;
        dynamicAcc = null;
        //Arrays.fill(processedAcc,0);


        filteredGyr = new float[3];
        noiseVarianceTimer = sampleNumber;
        gravityTimer = sampleNumber;
        //noiseVariance=0;
        noiseAverage = 0;

        noisyAcc = new Vector<>();
        noisyGyr = new Vector<>();

        final float[] totalGravity = new float[3];

        stats = new StatisticCalculations();
        lpf = new Filter();
        g= new Gravity();
        process = new AccProcess();
        dynamic = new DynamicAcceleration();


        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver()
                {
                    @Override
                    public void onReceive(Context context, Intent intent)
                    {
                        float[] acc = intent.getFloatArrayExtra("ACC_DATA");
                        float[] gyr = intent.getFloatArrayExtra("GYR_DATA");
                        float[] mag = intent.getFloatArrayExtra("MAG_DATA");
                        if (acc!=null && gyr!=null)
                        {
                            if (oldAcc==null)
                            {
                                oldAcc=acc;
                            }
                            if (oldGyr==null)
                            {
                                oldGyr=gyr;
                            }
                            dynamicAcc = dynamic.calculate(acc,oldAcc,gyr,oldGyr, dynamicAcc);
                            oldAcc = acc;
                            oldGyr = gyr;
//                            processedAcc = process.processedData(acc,processedAcc);

                            processedAccData = "Acc : "+    df.format(dynamicAcc[0])+", "+df.format(dynamicAcc[1])+", "+df.format(dynamicAcc[2])+"\n"
                                              +"Vel : "+    df.format(dynamicAcc[3])+", "+df.format(dynamicAcc[4])+", "+df.format(dynamicAcc[5])+"\n"
                                              +"Dist : "+   df.format(dynamicAcc[6])+", "+df.format(dynamicAcc[7])+", "+df.format(dynamicAcc[8])+"\n"
                                              +"gyr : "+    df.format(gyr[0])+", "+df.format(gyr[1])+", "+df.format(gyr[2])+"\n";

                            processedAccDataforFile = (dynamicAcc[0])+" "+(dynamicAcc[1])+" "+(dynamicAcc[2])+" "
                                    +   (dynamicAcc[3])+" "+(dynamicAcc[4])+" "+(dynamicAcc[5])+" "
                                    +   (dynamicAcc[6])+" "+(dynamicAcc[7])+" "+(dynamicAcc[8])+" "
                                    +   (processedAcc[9])+" "+(processedAcc[10])+" "+(processedAcc[11])+" "
                                    +   (gyr[0])+" "+(gyr[1])+" "+(gyr[2])+"\n";

                            tvMain.setText(processedAccData) ;
                            try
                            {
                                bos.write(processedAccDataforFile.getBytes());

                            } catch (IOException e)
                            {
                                e.printStackTrace();
                            }
                        }


//                        float accMagnitude = (float) Math.pow((acc[0]*acc[0]+acc[1]*acc[1]+acc[2]*acc[2]),0.5);
//                        if (noisyAcc.size()<sampleSize)
//                        {
//                            noisyAcc.add(acc);
//                        }
//                        else
//                        {
//                            noisyAcc.remove(0);
//                            noisyAcc.add(acc);
//                        }
//                        if (noisyGyr.size()<sampleSize)
//                        {
//                            noisyGyr.add(gyr);
//                        }
//                        else
//                        {
//                            noisyGyr.remove(0);
//                            noisyGyr.add(gyr);
//                        }
//
//                        gravity = g.gravity(noisyAcc, gravity);
//                        filteredGyr = lpf.lowPass(0.05f,gyr,filteredGyr);

//                        move = new Move(noiseVariance, acc, gyr, mag, gravity, intent.getLongExtra("TIME", 0), mdisp, tv, tv2, tvMain, tvAngle, root);
//                        move.moveIt(acc, gyr, mag);



//                        float[] temp = intent.getFloatArrayExtra("ACC_DATA");
//                        long timeInMillis = (new Date()).getTime()
//                                + (intent.getLongExtra("TIME", 0) - System.nanoTime()) / 1000000L;
//                        //tempAcc.add(temp);
//                        displayChange.setDisplay(intent.getStringExtra("ACC"), intent.getStringExtra("GYR"), intent.getStringExtra("LACC"));
//                        //displayChange.setTvAngle(intent.getFloatArrayExtra("ACC_DATA"), intent.getFloatArrayExtra("GYR_DATA"));
//
//                        lowPassAcc = lpf.lowPass(factor ,temp, lowPassAcc);
//                        noisyAcc.add(lowPassAcc);
//
//                        for (int j = 0; j < 3; j++)
//                        {
//                            totalGravity[j] += lowPassAcc[j];
//
//                        }
//                        if (noiseVarianceTimer > 0)
//                        {
//
//                            noiseVarianceTimer--;
//
//
//                        } else
//                        {
//                            if (!isNoiseVarianceCalculated)
//                            {
//                                noiseVariance = stats.calculateNoiseVariance(noisyAcc);
//                            }
//
////                            gravity = g.calibrateGravity(totalGravity, sampleNumber);
////                            gravity = g.gravity(noisyAcc, gravity);
//
////                            try
////                            {
////
////                                finalBos_gra.write(((timeInMillis)+" ").getBytes());
////                                finalBos_gra.write((gravity[0]+" "+gravity[1]+" "+gravity[2]+"\n").getBytes());
////
////                            } catch (IOException e)
////                            {
////                                e.printStackTrace();
////                            }
//
//                            //gravity = gravity(tempAcc, gravity);
////                            move = new Move(noiseVariance, intent.getFloatArrayExtra("ACC_DATA"), intent.getFloatArrayExtra("GYR_DATA"), gravity, intent.getLongExtra("TIME", 0), mdisp, tv, tv2, tvMain, tvAngle, root);
//
//                            // alpha = move.rotateText(mdisp, alpha);
////                            float[][] temp2;
////                            temp2 = move.lyingMove(mdisp, oldAcc, oldVelocity, oldDistance, gravity);
////
////                            for (int i = 0; i < 3; i++)
////                            {
////                                oldAcc[i] = temp2[0][i];
////                                oldVelocity[i] = temp2[1][i];
////                                oldDistance[i] = temp2[2][i];
////                            }
//
//                        }

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

        buttonReset.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                for (int i = 0; i < 3; i++)
                {
                    oldAcc[i] = 0.0f;
                    oldVelocity[i] = 0.0f;
                    oldDistance[i] = 0.0f;
                }
                tvMain.setX(276);
                tvMain.setY(530);
                noiseVarianceTimer = sampleNumber;
                noisyAcc.clear();
                processedAcc = null;
            }
        });

        buttonCalibrate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                noiseVarianceTimer = sampleNumber;
                noisyAcc.clear();
                gravity = g.calibrateGravity(totalGravity, sampleNumber);
                processedAcc = null;
            }
        });

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (buttonStart.getText().equals("Start")) {
                    buttonStart.setText("Stop");
                    startService(new Intent(MainActivity.this,SensorService.class));
                    FileOutputStream fOut = null;

                    try
                    {
                        myFile.createNewFile();

                        fOut = new FileOutputStream(myFile);

                        bos = new BufferedOutputStream(fOut);

                    }catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    buttonStart.setText("Start");
                    stopService(new Intent(MainActivity.this,SensorService.class));
                    try
                      {
                        bos.close();

                        } catch (IOException e)
                        {
                            e.printStackTrace();
                        }

                }
            }
        });



//
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }









    @Override
    protected void onPause()
    {

        super.onPause();
        //File sd = Environment.getExternalStorageDirectory();
        //String path = sd + "/" + "SensorData.db" + ".xml";


    }


    @Override
    protected void onResume()
    {

        super.onResume();
//        startService(new Intent(this, SensorService.class));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
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


        return id == R.id.action_settings || super.onOptionsItemSelected(item);

    }

    @Override
    protected void onDestroy()
    {


        super.onDestroy();
    }


    @Override
    public void onStart()
    {

        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.unist.netlab.fakturk.more/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop()
    {

        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.unist.netlab.fakturk.more/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
