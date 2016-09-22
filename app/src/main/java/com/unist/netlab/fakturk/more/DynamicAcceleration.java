package com.unist.netlab.fakturk.more;

import static android.hardware.SensorManager.GRAVITY_EARTH;

/**
 * Created by fakturk on 9/8/16.
 */

public class DynamicAcceleration
{
    float[] oldAcc,oldGyr, dynAcc;
    long timeInMillis;

    public DynamicAcceleration()
    {
        timeInMillis = 10;
        oldAcc = new float[]{0.0f, 0.0f, 0.0f};
        oldGyr = new float[]{0.0f, 0.0f, 0.0f};
        dynAcc = new float[]{0.0f, 0.0f, 0.0f};

    }
    public DynamicAcceleration(long time)
    {
        timeInMillis = time;
        oldAcc = new float[]{0.0f, 0.0f, 0.0f};
        oldGyr = new float[]{0.0f, 0.0f, 0.0f};
        dynAcc = new float[]{0.0f, 0.0f, 0.0f};

    }

    //takes acc and gyr values and seperate dynamic acceleration from gravity
    float[] dynamicAccDiff(float[] acc, float[] gyr, float[] oldAcc, float[] oldGyr)
    {
        float[] dynAccDiff = new float[]{0.0f, 0.0f, 0.0f};
        float[] accDiff = accDiff(oldAcc, acc);
        float[] gyrDiff = gyrDiff(oldGyr, gyr);
        float[] graDiff = graDiff(gyrDiff);

        for (int i = 0; i < 3; i++)
        {
            dynAccDiff[i] = accDiff[i]-graDiff[i];
        }

        return dynAccDiff;

    }

    float[] dynamicAcc(float[] oldDynamicAcc, float[] dynAccDiff)
    {
        float[] dynAcc= new float[]{0.0f, 0.0f, 0.0f};
        for (int i = 0; i < 3; i++)
        {
            dynAcc[i] = oldDynamicAcc[i] + dynAccDiff[i];
        }

        return dynAcc;
    }

    //find acceleration difference d_t
    float[] accDiff(float[] oldAcc, float[] newAcc)
    {
        float[] diffAcc = new float[]{0.0f, 0.0f, 0.0f};

        for (int i = 0; i < 3; i++)
        {
            diffAcc[i] = (newAcc[i]-oldAcc[i]);
        }
        return diffAcc;
    }

    //find acceleration difference d_t
    float[] gyrDiff(float[] oldGyr, float[] newGyr)
    {
        float[] diffGyr = new float[]{0.0f, 0.0f, 0.0f};

        for (int i = 0; i < 3; i++)
        {
            diffGyr[i] = (newGyr[i]-oldGyr[i]);
        }
        return diffGyr;
    }

    float[] graDiff(float[] gyrDiff)
    {
        float[] diffGra = new float[]{0.0f, 0.0f, 0.0f};
        diffGra[0] = (float) Math.cos(gyrDiff[1]*timeInMillis*1000)* GRAVITY_EARTH;
        diffGra[1] = (float) Math.sin(gyrDiff[0]*timeInMillis*1000)* GRAVITY_EARTH;
        diffGra[2] = (float) ((Math.sin(gyrDiff[1]*timeInMillis*1000)*Math.cos(gyrDiff[0]*timeInMillis*1000)-1)* GRAVITY_EARTH);

        return  diffGra;
    }

    void speed()
    {

    }

    void distance()
    {

    }

}
