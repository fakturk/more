package com.unist.netlab.fakturk.more;

import java.util.Vector;

/**
 * Created by fakturk on 16. 5. 2.
 */
public class StatisticCalculations {
    public StatisticCalculations() {
    }

    public float[] mean(Vector<float[]> data)
    {

        int dataArrayLength = data.elementAt(0).length;
        float[] total = new float[dataArrayLength];

        for (int i = 0; i < data.size(); i++)
        {
            for (int j = 0; j < dataArrayLength; j++)
            {
                total[j] += data.elementAt(i)[j];
            }
        }
        for (int j = 0; j < dataArrayLength; j++)
        {
            total[j] = total[j] / data.size();
        }

        return total;
    }

    public float[] variance(Vector<float[]> data)
    {

        float[] mean = mean(data);
        int dataArrayLength = data.elementAt(0).length;
        float[] temp = new float[dataArrayLength];

        for (int i = 0; i < data.size(); i++)
        {
            for (int j = 0; j < dataArrayLength; j++)
            {
                temp[j] += Math.pow(data.elementAt(i)[j] - mean[j], 2);
            }
        }
        for (int j = 0; j < dataArrayLength; j++)
        {
            temp[j] = temp[j] / data.size();
        }

        return temp;
    }

     double[][] calculateNoiseVariance(Vector<float[]> noisyAcc)
    {

        double[][] variance = {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}};
        double[] avg = {0, 0, 0};
        double[] total = {0, 0, 0};
        int sampleSize = noisyAcc.size();

        for (int i = 0; i < sampleSize; i++)
        {
            total[0] += noisyAcc.get(i)[0];
            total[1] += noisyAcc.get(i)[1];
            total[2] += noisyAcc.get(i)[2];
        }
        for (int i = 0; i < 3; i++)
        {
            avg[i] = total[i] / (sampleSize * 1.0);
            // Log.d("avg","avg : "+avg[i]);
        }

        for (int k = 0; k < sampleSize; k++)
        {
            for (int i = 0; i < 3; i++)
            {

                variance[i][i] += Math.sqrt((noisyAcc.get(k)[i] - avg[i]) * (noisyAcc.get(k)[i] - avg[i]));
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

            variance[i][i] = 0.1;
        }


        return variance;
    }
}
