package com.unist.netlab.fakturk.more;

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
    void seperate(float[] acc, float[] gyr)
    {

        for (int i = 0; i < 3; i++)
        {
            dynAcc[i] = (acc[i]-oldAcc[i])-(gyr[i]- oldGyr[i]/timeInMillis);
        }

    }

    void speed()
    {

    }

    void distance()
    {

    }

}
