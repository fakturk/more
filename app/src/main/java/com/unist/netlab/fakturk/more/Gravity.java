package com.unist.netlab.fakturk.more;

import java.util.Vector;

/**
 * Created by fakturk on 16. 5. 2.
 */
public class Gravity {

    public Gravity() {
    }

    float[] calibrateGravity(float[] totalGravity, int sampleNumber)
    {
        //float[] totalGravity = new float[3];
//        totalGravity[0] = 0.0f;
//        totalGravity[1] = 0.0f;
//        totalGravity[2] = 0.0f;
//        float[] temp = new float[3];
//        temp[0] = 0.0f;
//        temp[1] = 0.0f;
//        temp[2] = 0.0f;
        //int sampleNumber = 1000;
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
            totalGravity[j] = totalGravity[j] / sampleNumber;

        }
        return totalGravity;
    }


    float[] gravity(Vector<float[]> accData, float[] estimatedGravity)
    {
        StatisticCalculations stats = new StatisticCalculations();
        float epsilon = 0.01f;
        float THvar[] = {epsilon, epsilon, epsilon};
        float[] varIncrease = {0,0,0};

        float[] wMean = stats.mean(accData);
        float[] wVar = stats.variance(accData);

        float norm = 0;

        for (int i = 0; i < 3; i++)
        {
            norm += Math.pow(wMean[i] - estimatedGravity[i], 2);
        }
        norm = (float) Math.pow(norm, 0.5);
        if (norm >= 2)
        {
            THvar = new float[]{epsilon, epsilon, epsilon};
        }

        for (int i = 0; i < 3; i++)
        {
            if (wVar[i] < 1.5f)
            {
                estimatedGravity[i] = wMean[i];
                THvar[i] = (wVar[i] + THvar[i]) / 2;

                varIncrease[i] = THvar[i] * epsilon;
            }
            else
            {
                THvar[i] += varIncrease[i];
            }
        }
        return estimatedGravity;

    }

    float[] gravityAfterRotaton(float[] gravity, float[][] rotationMatrix)
    {
        float[] newGravity = new float[3];
        for (int i = 0; i < 3; i++) {
            newGravity[i] = gravity[0]* rotationMatrix[i][0]+ gravity[1]* rotationMatrix[i][1]+gravity[2]* rotationMatrix[i][2];
        }
        return newGravity;
    }

}
