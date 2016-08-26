package com.unist.netlab.fakturk.more;

import android.util.Log;

/**
 * Created by fakturk on 8/25/16.
 * this class takes raw accelerometer readings as input and process this acc data and return results
 */

public class AccProcess
{
    float alpha;
    float[] difference,velocity, hpVelocity, lphpVelocity, distance, hpDistance;
    float[] oldAcc          ,
            oldDifference   ,
            oldVelocity     ,
            oldhpVelocity   ,
            oldlphpVelocity ,
            oldDistance     ,
            oldhpDistance   ;

    public AccProcess()
    {
        this.alpha = 0.95f;
        oldAcc          = new float[3];
        oldDifference   = new float[3];
        oldVelocity     = new float[3];
        oldhpVelocity   = new float[3];
        oldlphpVelocity = new float[3];
        oldDistance     = new float[3];
        oldhpDistance   = new float[3];
    }

    public AccProcess(float alpha)
    {
        this.alpha = alpha;
        oldAcc          = new float[3];
        oldDifference   = new float[3];
        oldVelocity     = new float[3];
        oldhpVelocity   = new float[3];
        oldlphpVelocity = new float[3];
        oldDistance     = new float[3];
        oldhpDistance   = new float[3];
    }

    //input: raw acc data at current time, inputOld : raw acc data from previous time,  output : processed acc data, velocity, distance
    float[] processedData(float[] input,  float[] output)
    {
        Filter f = new Filter();


        if (output==null)
        {
            for (int i = 0; i < 3; i++)
            {
                oldAcc[i]          = input[i];
                oldDifference[i]   = 0;
                oldVelocity[i]     = 0;
                oldhpVelocity[i]   = 0;
                oldlphpVelocity[i] = 0;
                oldDistance[i]     = 0;
                oldhpDistance[i]   = 0;
            }
            output = new float[21];
        }
        else
        {

            for (int i = 0; i < 3 ; i++)
            {
                oldAcc[i]          = output[i];
                oldDifference[i]   = output[i+3];
                oldVelocity[i]     = output[i+6];
                oldhpVelocity[i]   = output[i+9];
                oldlphpVelocity[i] = output[i+12];
                oldDistance[i]     = output[i+15];
                oldhpDistance[i]   = output[i+18];

            }
        }



        difference = takeDifference(input,oldAcc,oldDifference);
        velocity = velocityByDelta(difference,oldVelocity);
        hpVelocity = f.recursivehighPass(alpha,velocity,oldVelocity,oldhpVelocity);
        lphpVelocity = f.recursivelowPass(alpha,hpVelocity,oldlphpVelocity);
        distance = distance(lphpVelocity, oldDistance);
        Log.d("hpDistance","alpha : "+alpha+" , distance :"+distance[0]+" , oldDistance : "+oldDistance[0]+" ,oldhpDistance"+oldhpDistance[0]);
        hpDistance = f.recursivehighPass(alpha,distance, oldDistance,oldhpDistance);


        for (int i = 0; i < 3 ; i++)
        {
            output[i] = input[i];
            output[i+3] = difference[i];
            output[i+6] = velocity[i];
            output[i+9] = hpVelocity[i];
            output[i+12] = lphpVelocity[i];
            output[i+15] = distance[i];
            output[i+18] = hpDistance[i];

        }


        return output;


    }


    //takes the difference of each accelerometer readings
    // y[n] = y[n-1] + x[n] - x[n-1]
    float[] takeDifference(float[] input,float[] inputOld,  float[] output)
    {

        for (int i = 0; i < output.length; i++)
        {
            output[i] = output[i] + input[i] - inputOld[i];
        }
        return output;
    }

    // finds velocity by delta function
    // y[n] = y[n-1] + x[n]*0.01
    float[] velocityByDelta(float[] difference, float[] oldVelocity)
    {
        float[] velocity = new float[3];
        for (int i = 0; i < oldVelocity.length; i++)
        {
            velocity[i] = oldVelocity[i] + difference[i]*0.01f;
        }
        return velocity;
    }

    // finds distance by integrating velocity
    //y[n] = y[n-1]+x[n]*0.01
    float[] distance(float[] velocity, float[] oldDistance)
    {
        float[] distance = new float[3];
        for (int i = 0; i < oldVelocity.length; i++)
        {
            distance[i] = oldDistance[i] + velocity[i];

        }
        return distance;
    }


}
