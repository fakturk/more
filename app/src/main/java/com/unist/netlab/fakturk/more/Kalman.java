package com.unist.netlab.fakturk.more;

/**
 * Created by fakturk on 2/17/16.
 */
public class Kalman
{
    float dt;
    float[] ACC_DATA, VEL_DATA, POS_DATA;
    float[][] F;
    float[] H;
    float[] X; // storing  VEL_DATA, POS_DATA, ACC_DATA,

    public Kalman(float dt, float[] ACC_DATA)
    {

        this.dt = dt;
        this.ACC_DATA = new float[3];
        this.VEL_DATA = new float[3];
        this.POS_DATA = new float[3];
        this.F = new float[3][3];
        this.H = new float[]{0, 0, 1};

        // events
        for (int i=0;i<3;i++)
        {
            this.ACC_DATA[i]=ACC_DATA[i];
            this.VEL_DATA[i]=0;
            this.POS_DATA[i]=0;

//
        }
    }




    public void filter()
    {

        for (int i = 0; i < 3; i++)
        {
            X[i]=0;
            X[i+3]=0;
            X[i+6] = ACC_DATA[i];
        }

    }

    public void updateKalmanGain()
    {}
}
