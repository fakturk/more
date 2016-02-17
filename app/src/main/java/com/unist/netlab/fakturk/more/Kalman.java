package com.unist.netlab.fakturk.more;

/**
 * Created by fakturk on 2/17/16.
 */
public class Kalman
{
    float dt;
    float[] ACC_DATA, VEL_DATA, POS_DATA;
    float[][] F;    //state matrix
    float[][] H;    //measurement sensitivity matrix
    float[][] P;    //error covariance
    float[][] K;    //Kalman gain
    float[] Q;      // process noise covariance matrix
    float[] R;      //measurement noise covariance matrix
    float[][] I;    //identity matrix
    float[] X;      // estimation
    float[] Z;      //real measurement

    public Kalman(float dt, float[] ACC_DATA)
    {

        this.dt = dt;
        this.ACC_DATA = new float[3];
        this.VEL_DATA = new float[3];
        this.POS_DATA = new float[3];
        this.F = new float[3][3];
        this.P = new float[9][9];
        this.K = new float[9][3];
        this.H = new float[][]{
                {0, 0, 0, 0, 0, 0, 1, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 1, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 1}
        };

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
