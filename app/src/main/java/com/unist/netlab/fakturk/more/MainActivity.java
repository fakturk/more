package com.unist.netlab.fakturk.more;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Vector;


public class MainActivity extends Activity
{

    //SensorManager SM;
    TextView tv, tv2;
    TextView tvMain;
    TextView tvAngle;
    Button buttonUp, buttonDown, buttonReset, buttonCalibrate;
    Move move;
    DisplayChange displayChange;
    Display mdisp;
    Double alpha;
    float[] oldAcc, oldVelocity, oldDistance, gravity;
    int noiseVarianceTimer, gravityTimer;
    double  noiseAverage;
    double[][] noiseVariance;
    boolean isNoiseVarianceCalculated=false;
    Vector<float[]> noisyAcc;







    RelativeLayout root;

    //MyReceiver myReceiver=null;
    Intent i;
    static final String LOG_TAG = "ServiceActivity";









    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //Log.d( LOG_TAG, "onCreate" );

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        root = (RelativeLayout) findViewById(R.id.root);



        i= new Intent(this, SensorService.class);
      //  Log.d(LOG_TAG, "onCreate/startService");

        //SM = (SensorManager) getSystemService(SENSOR_SERVICE);
        tv = (TextView)  findViewById(R.id.textView);
        tv2 = (TextView)  findViewById(R.id.textView2);

        tvMain = (TextView) findViewById(R.id.textViewMain);
        tvAngle = (TextView) findViewById(R.id.textViewAngle);
        buttonUp = (Button) findViewById(R.id.buttonSizeUp);
        buttonDown = (Button) findViewById(R.id.buttonSizeDown);
        buttonReset = (Button) findViewById(R.id.buttonReset);
        buttonCalibrate = (Button) findViewById(R.id.buttonCalibrate);
        displayChange = new DisplayChange(tv, tvAngle);
        mdisp = getWindowManager().getDefaultDisplay();
        alpha = 0.0;
        oldAcc = new float[3];
        oldVelocity = new float[3];
        oldDistance = new float[3];
        gravity = new float[3];
        noiseVarianceTimer = 100;
        gravityTimer = 100;
        //noiseVariance=0;
        noiseAverage=0;

        noisyAcc = new Vector<float[]>();

        final float[] totalGravity = new float[3];








        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {


                        displayChange.setDisplay(intent.getStringExtra("ACC"), intent.getStringExtra("GYR"), intent.getStringExtra("LACC"));
                        //displayChange.setTvAngle(intent.getFloatArrayExtra("ACC_DATA"), intent.getFloatArrayExtra("GYR_DATA"));

                        if (noiseVarianceTimer>0)
                        {
                            noisyAcc.add(intent.getFloatArrayExtra("ACC_DATA"));
                            noiseVarianceTimer--;
                            float[] temp = intent.getFloatArrayExtra("ACC_DATA");
                            for (int j = 0; j < 3; j++)
                            {
                                totalGravity[j] += temp[j];

                            }

                        }
                        else
                        {
                            if (!isNoiseVarianceCalculated)
                            {
                                noiseVariance = calculateNoiseVariance(noisyAcc);
                            }

                            gravity = calibrateGravity(totalGravity);
                            move = new Move(noiseVariance, intent.getFloatArrayExtra("ACC_DATA"), intent.getFloatArrayExtra("GYR_DATA"),gravity, intent.getLongExtra("TIME", 0), mdisp, tv,tv2, tvMain, tvAngle, root);
                            //move.moveIt();

                            // alpha = move.rotateText(mdisp, alpha);
                            float[][] temp;
                            temp = move.lyingMove(mdisp, oldAcc, oldVelocity, oldDistance, gravity);

                            for (int i = 0; i < 3; i++) {
                                oldAcc[i] = temp[0][i];
                                oldVelocity[i] = temp[1][i];
                                oldDistance[i] = temp[2][i];
                            }

                        }

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
                for (int i = 0; i < 3; i++) {
                    oldAcc[i] = 0.0f;
                    oldVelocity[i] = 0.0f;
                    oldDistance[i] = 0.0f;
                }
                tvMain.setX(276);
                tvMain.setY(530);
                noiseVarianceTimer = 100;
                noisyAcc.clear();
            }
        });

        buttonCalibrate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                noiseVarianceTimer=100;
                noisyAcc.clear();
                gravity = calibrateGravity(totalGravity);
            }
        });

//
    }

    private float[] calibrateGravity(float[] totalGravity)
    {
        //float[] totalGravity = new float[3];
//        totalGravity[0] = 0.0f;
//        totalGravity[1] = 0.0f;
//        totalGravity[2] = 0.0f;
//        float[] temp = new float[3];
//        temp[0] = 0.0f;
//        temp[1] = 0.0f;
//        temp[2] = 0.0f;
        int sampleNumber = 100;
//        for (int i = 0; i < sampleNumber; i++)
//        {
//            temp = getIntent().getFloatArrayExtra("ACC_DATA");
//            for (int j = 0; j < 3; j++)
//            {
//                totalGravity[j] += temp[j];
//
//            }
//
//        }
        for (int j = 0; j < 3; j++)
        {
            totalGravity[j] = totalGravity[j]/sampleNumber;

        }
        return totalGravity;
    }

    private double[][] calculateNoiseVariance(Vector<float[]> noisyAcc)
    {
        double[][] variance   = {{0, 0, 0},{0,0,0},{0,0,0}};
        double[] avg        = {0, 0, 0};
        double[] total      = {0, 0, 0};
        int sampleSize = noisyAcc.size();

        for (int i = 0; i < sampleSize; i++)
        {
            total[0]+=noisyAcc.get(i)[0];
            total[1]+=noisyAcc.get(i)[1];
            total[2]+=noisyAcc.get(i)[2];
        }
        for (int i = 0; i < 3; i++)
        {
            avg[i] = total[i]/(sampleSize*1.0);
           // Log.d("avg","avg : "+avg[i]);
        }

        for (int k = 0; k < sampleSize; k++)
        {
            for (int i = 0; i < 3; i++)
            {

                    variance[i][i]+=Math.sqrt((noisyAcc.get(k)[i]-avg[i])*(noisyAcc.get(k)[i]-avg[i]));
                 //   Log.d("variance","variance "+i+" : "+variance[i][i]+", acc : "+noisyAcc.get(k)[i]+" avg : "+avg[i]);

//                variance[i][i]+=Math.pow(Math.pow(noisyAcc.get(k)[i],2),0.5)/sampleSize;


//                for (int j = 0; j < 3; j++)
//                {
//                    variance[i][j]+=Math.sqrt(Math.abs(noisyAcc.get(k)[i]*noisyAcc.get(k)[j]))/sampleSize;
//                }

            }
        }

        for (int i = 0; i < 3; i++)
        {
//            if(variance[i][i]==0)
//            {
//
//            }
//                variance[i][i]/=sampleSize;

            variance[i][i]=0.1;
        }



        return variance;
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






