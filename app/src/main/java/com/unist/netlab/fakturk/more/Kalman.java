package com.unist.netlab.fakturk.more;

import Jama.Matrix;

/**
 * Created by fakturk on 2/17/16.
 */
public class Kalman
{


    float dt;
    float[] ACC_DATA, VEL_DATA, POS_DATA;
    Matrix F;       // state matrix 9 X 9
    Matrix H;       // measurement sensitivity matrix 3 X 9
    Matrix P_post;  // a posteriori error covariance 9 X 9
    Matrix P_pre;   // a priori error covariance 9 X 9
    Matrix K;       // Kalman gain 9 X 3
    Matrix Q;       // process noise covariance matrix 9 X 9
    Matrix R;       // measurement noise covariance matrix 3 X 3
    Matrix I;       // identity matrix 9 X 9
    Matrix X;       // calculated position, velocity and accelerometer value
    Matrix X_pre;   // pre state estimation 9 X 1
    Matrix X_post;  // post state estimation 9 X 1
    Matrix Z;       // real measurement 3 X 1
    Matrix G;       // error matrix 9 X 1
    double[] q;       // variance of error

    public Kalman()
    {

        this.dt = 0;
        this.ACC_DATA = new float[3];
        this.VEL_DATA = new float[3];
        this.POS_DATA = new float[3];
        this.F = new Matrix(9,9);
        this.P_post = new Matrix(9,9);
        this.P_pre = new Matrix(9,9);
        this.K = new Matrix(9,3);
        this.Q = new Matrix(9,9);
        this.R = new Matrix(3,3);
        this.I =  Matrix.identity(9,9);
        this.X_pre = new Matrix(9,1);
        this.X_post = new Matrix(9,1);
        this.X = new Matrix(9,1);
        this.Z = new Matrix(3,1);
        this.G = new Matrix(9,1);

        double[][] sensitivity = new double[][]{
                {0, 0, 0, 0, 0, 0, 1, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 1, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 1}
        };
        this.H = new Matrix(sensitivity);



    }




    public void filter(float dt, float[] ACC_DATA, double[] q) // q is variance of error
    {
        // events
        this.dt = dt;
        for (int i=0;i<3;i++)
        {
            Z.set(i,0,ACC_DATA[i]);

        }
        double[] error = new double[]{0.5*dt*dt, 0.5*dt*dt, 0.5*dt*dt, dt, dt, dt, 0, 0, 0};
        Matrix Error = new Matrix(error,9);
        G.setMatrix(0,9,0,0,Error);

        double[][] state = new double[9][9];
        for (int i = 0; i < 9; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                if (i==j)
                {
                    state[i][j]=1;
                }
                if(i==j-3)
                {
                    state[i][j]=dt;
                }
                if (i==j-6)
                {
                    state[i][j]=0.5*dt*dt;
                }
            }
        }

        Matrix State = new Matrix(state);
        F.setMatrix(0,9,0,9,State);


        this.q = q;
        Q = G.times(G);
        Q.timesEquals(q[0]);
        Matrix e_pre, e_post;
        e_pre  = X.minus(X_pre);
        e_post = X.minus(X_post);




    }






    public void updateKalmanGain()
    {}
}
