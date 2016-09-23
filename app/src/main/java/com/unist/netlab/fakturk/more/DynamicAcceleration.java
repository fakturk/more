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
        diffGra[0] = (float) Math.cos(gyrDiff[1]*timeInMillis)* GRAVITY_EARTH;
        diffGra[1] = (float) Math.sin(gyrDiff[0]*timeInMillis)* GRAVITY_EARTH;
        diffGra[2] = (float) ((Math.sin(gyrDiff[1]*timeInMillis)*Math.cos(gyrDiff[0]*timeInMillis)-1)* GRAVITY_EARTH);

        return  diffGra;
    }

    float[] velocity(float[] oldVelocity, float[] dynAcc)
    {
        float[] velocity = new float[]{0.0f, 0.0f, 0.0f};
        for (int i = 0; i < 3; i++) {
            velocity[i] = oldVelocity[i]+dynAcc[i];
        }
        return velocity;
    }

    float[] distance(float[] oldDistance, float[] velocity)
    {
        float[] distance = new float[]{0.0f, 0.0f, 0.0f};
        for (int i = 0; i < 3; i++) {
            distance[i] = oldDistance[i]+velocity[i];
        }
        return distance;
    }

    // calculates and return dynamic acceleration, velocity and distance
    float[] calculate(float[] acc, float[] oldAcc, float[] gyr , float[] oldGyr, float[] oldAccVelDis)
    {
        if (oldAccVelDis==null)
        {
            oldAccVelDis= new float[9];
            for (int i = 0; i <9; i++) {
                oldAccVelDis[i]=0.0f;
            }
        }
        float[] oldDynamicAcc = new float[]{0.0f, 0.0f, 0.0f};
        float[] oldVelocity = new float[]{0.0f, 0.0f, 0.0f};
        float[] oldDistance = new float[]{0.0f, 0.0f, 0.0f};
        for (int i = 0; i < 3; i++) {
            oldDynamicAcc[i] = oldAccVelDis[i];
            oldVelocity[i] = oldAccVelDis[i+3];
            oldDistance[i] = oldAccVelDis[i+6];
        }
        float[] accVelDis = new float[]{0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f};
        float[] dynAccDiff = dynamicAccDiff(acc,gyr,oldAcc,oldGyr);
        float[] dynamicAcc = dynamicAcc(oldDynamicAcc, dynAccDiff);
        float[] velocity = velocity(oldVelocity,dynamicAcc);
        float[] distance = distance(oldDistance, velocity);
        for (int i = 0; i < 3; i++) {
            accVelDis[i]=dynamicAcc[i];
            accVelDis[i+3]= velocity[i];
            accVelDis[i+6]=distance[i];
        }
        return accVelDis;
    }

}
