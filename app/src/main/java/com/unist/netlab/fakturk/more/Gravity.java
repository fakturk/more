package com.unist.netlab.fakturk.more;

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

}
