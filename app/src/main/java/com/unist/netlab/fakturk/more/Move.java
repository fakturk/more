package com.unist.netlab.fakturk.more;

import android.graphics.Point;
import android.hardware.SensorManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.widget.RelativeLayout;
import android.widget.TextView;

import Jama.Matrix;

import static android.util.FloatMath.cos;
import static android.util.FloatMath.sin;
import static android.util.FloatMath.sqrt;

/**
 * Created by fakturk on 12/29/15.
 */
public class Move
{
    //SensorEvent se;
    TextView tv, tv2;
    TextView tvMain;
    TextView tvAngle;
    RelativeLayout root;
    //float[] events =new float[9];;
    float[] ACC_DATA, GYR_DATA, ACC_DATA_LPF;
    double[][] q;
    float accX=0.0f;
    float factor = 0.02f;

    int top ;
    int bottom ;
    int left;
    int right ;
    int height ;
    int width ;


    long timestamp;
    int maxX, maxY;
    float x,y, new_x=276, new_y;
    double r;
    double  beta=0, betaR;//angles, beta is the current angle and alpha is the change
    Display display;

    public Move(double[][] q, float[] ACC_DATA, float[] GYR_DATA, long timestamp, Display mdisp, TextView tv,TextView tv2, TextView tvMain, TextView tvAngle, RelativeLayout root)
    {
        //this.se = se;
        this.tv = tv;
        this.tv2 = tv2;

        this.tvMain = tvMain;
        this.tvAngle = tvAngle;

        this.root = root;
        this.timestamp = timestamp;

        this.q = q;

        this.display = mdisp;
        this.ACC_DATA = new float[3];
        this.GYR_DATA = new float[3];
//        this.GRA_DATA = new float[3];

         top = root.getTop();
         bottom = root.getBottom();
         left = root.getLeft();
         right = root.getRight();
         height = tvMain.getHeight();
         width = tvMain.getWidth();



       // events
        for (int i=0;i<3;i++)
        {
            //Log.d("eventAtama", Float.toString(e[i]));
            this.ACC_DATA[i]=ACC_DATA[i];
            this.GYR_DATA[i]=GYR_DATA[i];
//            this.GRA_DATA[i]=GRA_DATA[i];
        }
    }

    public float[][] lyingMove(Display mdisp,float[] oldAcc, float[] oldVelocity, float oldDistance[])
    {
        float dt = (System.nanoTime() - timestamp) / 1000000000.0f;
        final float alpha = (float) 0.98;
        //float  velocityX, distanceX, velocityY, distanceY;
        float[] velocity = new float[3];
        float[] distance = new float[3];
        //float[] oldAcc = new float[3];

        float[] gravity = new float[3];
        gravity[0] = alpha * gravity[0] + (1 - alpha) * ACC_DATA[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * ACC_DATA[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * ACC_DATA[2];

        float[] linear_acceleration = new float[3];
        linear_acceleration[0] = ACC_DATA[0] - gravity[0];
        linear_acceleration[1] = ACC_DATA[1] - gravity[1];
        linear_acceleration[2] = ACC_DATA[2] - gravity[2];

        float[] g = new float[3];
        g[0] = ACC_DATA[0];
        g[1] = ACC_DATA[1];
        g[2] = ACC_DATA[2];

        double norm_Of_g = Math.sqrt(g[0] * g[0] + g[1] * g[1] + g[2] * g[2]);

// Normalize the accelerometer vector
        g[0] = (float) (g[0] / norm_Of_g);
        g[1] = (float) (g[1] / norm_Of_g);
        g[2] = (float) (g[2] / norm_Of_g);

        double inclination =  Math.toDegrees(Math.acos(g[2]));
        double rotation =  Math.toDegrees(Math.atan2(g[0], g[1]));

//        double gravity_z = Math.cos(Math.toRadians(inclination))*SensorManager.GRAVITY_EARTH;
//        double gravity_x = Math.sin(Math.toRadians(inclination))*SensorManager.GRAVITY_EARTH;
        double gravity_x = g[0]*SensorManager.GRAVITY_EARTH;
        double gravity_y = g[1]*SensorManager.GRAVITY_EARTH;
        double gravity_z = g[2]*SensorManager.GRAVITY_EARTH;



        float axisX=0,  axisY=0, axisZ=0;

        final float NS2S = 1.0f / 1000000000.0f;
        final float[] deltaRotationVector = new float[4];
        float tStamp = 0;
        final double EPSILON = 0.000001;

            final float dT = (timestamp - tStamp) * NS2S;
            // Axis of the rotation sample, not normalized yet.
             axisX = GYR_DATA[0];
             axisY = GYR_DATA[1];
             axisZ = GYR_DATA[2];

            // Calculate the angular speed of the sample
            float omegaMagnitude = sqrt(axisX*axisX + axisY*axisY + axisZ*axisZ);

            // Normalize the rotation vector if it's big enough to get the axis
            if (omegaMagnitude > EPSILON) {
                axisX /= omegaMagnitude;
                axisY /= omegaMagnitude;
                axisZ /= omegaMagnitude;
            }

            // Integrate around this axis with the angular speed by the timestep
            // in order to get a delta rotation from this sample over the timestep
            // We will convert this axis-angle representation of the delta rotation
            // into a quaternion before turning it into the rotation matrix.
            float thetaOverTwo = omegaMagnitude * dT / 2.0f;
            float sinThetaOverTwo = sin(thetaOverTwo);
            float cosThetaOverTwo = cos(thetaOverTwo);
            deltaRotationVector[0] = sinThetaOverTwo * axisX;
            deltaRotationVector[1] = sinThetaOverTwo * axisY;
            deltaRotationVector[2] = sinThetaOverTwo * axisZ;
            deltaRotationVector[3] = cosThetaOverTwo;

        tStamp = timestamp;
        float[] deltaRotationMatrix = new float[9];
        SensorManager.getRotationMatrixFromVector(deltaRotationMatrix, deltaRotationVector);
//            String SD="";
//            for (int i = 0; i < deltaRotationMatrix.length; i++)
//            {
//                SD += "delta [" + (i) + "] : " + deltaRotationMatrix[i] + "`\n";
//            }


        //tv2.setText(SD);


        double filterCo = 1;
        gravity_x = gravity_x*filterCo + (axisX*SensorManager.GRAVITY_EARTH)*(1-filterCo);
        gravity_y = gravity_y*filterCo + (axisY*SensorManager.GRAVITY_EARTH)*(1-filterCo);
        gravity_z = gravity_z*filterCo + (axisZ*SensorManager.GRAVITY_EARTH)*(1-filterCo);


        linear_acceleration[0] = (float) (ACC_DATA[0] - gravity_x);
        linear_acceleration[1] = (float) (ACC_DATA[1] - gravity_y);
        linear_acceleration[2] = (float) (ACC_DATA[2] - gravity_z);

        tv2.setText(
                "array g: \n"
                        + "X = " + g[0] + "\n"
                        + "Y = " + g[1] + "\n"
                        + "Z = " + g[2] + "\n"
                +"\n"
                +"Lin ACC : \n"
                + "X = " + linear_acceleration[0] + "\n"
                + "Y = " + linear_acceleration[1] + "\n"
                + "Z = " + linear_acceleration[2] + "\n"
                        +"\n"

                +"gyr: \n"
                        + "X = " + axisX + "\n"
                        + "Y = " + axisY + "\n"
                        + "Z = " + axisZ + "\n"
                        +"\n"


        );


        x = 276;
        y = 530;
        Point size = new Point();
        mdisp.getSize(size);
        maxX = size.x;
        maxY = size.y;

       // accX = accX *(1-factor) + factor*ACC_DATA[0];

//        if (ACC_DATA[0]>0.2||ACC_DATA[0]<-0.2)
//        {
//            velocity = oldVelocity + ACC_DATA[0]*dt;
//        }
//        else velocity = oldVelocity;

       // ACC_DATA_LPF = lowPass(ACC_DATA.clone(),ACC_DATA_LPF);

        //velocity = oldVelocity + ACC_DATA_LPF[0]*dt;
        //distanceX = oldDistance+velocity*dt;

        Kalman kalman = new Kalman(q);

        float[] accDiff = new float[3];

        accDiff[0] = linear_acceleration[0];
        accDiff[1] = linear_acceleration[1];
        accDiff[2] = linear_acceleration[2];


        Matrix X = kalman.filter(dt,accDiff);

//        velocity = oldVelocity + (float)X.get(6,0)*dt;
//        distanceX = oldDistance + velocity*dt;

//         velocity = (oldVelocity + accDiff[0]*dt);
//         distanceX = (oldDistance + velocity*dt);

        for (int i = 0; i < 3; i++)
        {
            velocity[i]= oldVelocity[i] - (float) X.get(3+i,0);
            distance[i] =oldDistance[i] - (float) X.get(i,0);
        }


        DisplayMetrics dm = new DisplayMetrics();
        mdisp.getMetrics(dm);
        float[] px = new float[3];

        for (int i = 0; i < 3; i++) {
             px[i] = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, distance[i]*1000,
                    dm);
        }

        new_x -= px[0];
        new_y -= px[1];

        //oldVelocity = velocity;

        if(new_x<50)
        {
            new_x=50;
            velocity[0] = 0;
           // distanceX = 0;
        }
        if (new_x>right-width-50)
        {
            new_x = right-width-50;
            velocity[0] = 0;
            //distanceX = 0;
        }

        tvMain.setX(new_x);
        tvMain.setY(new_y);

        tvAngle.setText("VelocityX : "+velocity[0]
                +",\n distanceX : "+distance[0]
                + "\n new_x :"+new_x
                + "\n k x :"+X.get(6,0)
                +"\n accdiff :"+accDiff[0]
                +"\n inclination : "+inclination
                +"\n rotation : "+rotation
                +"\n gravity x : "+gravity_x
                +"\n gravity z : "+gravity_z

               );

         float[][] oldValues = new float[3][3];
        for (int i = 0; i < 3; i++) {
            oldValues[0][i] = linear_acceleration[i];
            oldValues[1][i] = velocity[i];
            oldValues[2][i] = distance[i];
        }

        return oldValues;


    }

    protected float[] lowPass( float[] input, float[] output ) {
        if ( output == null ) return input;
        for ( int i=0; i<input.length; i++ ) {
            output[i] = output[i] + factor * (input[i] - output[i]);
        }
        return output;
    }

    public double rotateText(Display mdisp, double oldRotation)
    {
        double alpha; //difference between currrent and old rotation angle
        float[] g = new float[3];
        g[0] = ACC_DATA[0];
        g[1] = ACC_DATA[1];
        g[2] = ACC_DATA[2];

        double norm_Of_g = Math.sqrt(g[0] * g[0] + g[1] * g[1] + g[2] * g[2]);

// Normalize the accelerometer vector
        g[0] = (float) (g[0] / norm_Of_g);
        g[1] = (float) (g[1] / norm_Of_g);
        g[2] = (float) (g[2] / norm_Of_g);

        int inclination = (int) Math.round(Math.toDegrees(Math.acos(g[2])));
        int rotation = (int) Math.round(Math.toDegrees(Math.atan2(g[0], g[1])));
        //int angle = (int) (Math.round((180/Math.PI)*Math.acos(g[1])))-90;

        //tvAngle.setText("Inclination : "+inclination+"\n Rotation : "+rotation);
        //tvAngle.setText(g[0]+", "+g[1]+", "+g[2]);





//        x = tvMain.getX();
//        y = tvMain.getY();
        x = 276;
        y = 530;
        Point size = new Point();
        mdisp.getSize(size);
        maxX = size.x;
        maxY = size.y;

        r = Math.sqrt(Math.pow((maxX-x),2)+Math.pow((maxY-y),2));

        betaR = Math.acos((maxX-x)/r);
        beta = Math.toDegrees(Math.acos((maxX-x)/r));
        alpha = rotation-oldRotation;
//        alpha = 20;
//        new_x = (float) (maxX- Math.cos(Math.toRadians(beta+alpha))*r);
//        new_y = (float) (maxY - Math.sin(Math.toRadians(beta+alpha))*r);

        new_x = x;
        new_y = y;
//        if (new_x<0)
//        {
//            new_x = 0;
//        }
//        if (new_y<0)
//        {
//            new_y=0;
//        }
//        if (new_x>maxX);
//        {
//            new_x=maxX;
//        }
//        if (new_y>maxY)
//        {
//            new_y = maxY;
//        }



        tvMain.setRotation(rotation);
        tvMain.setX(new_x);
        tvMain.setY(new_y);

        tvAngle.setText("Inclination : "+inclination
                +",\n Rotation : "+rotation
                + "\n r :"+r
                + "\n alpha :"+alpha
                + "\n beta :"+beta
                + "\n betaR :"+betaR
                + "\n alpha + beta :"+(alpha+beta)
                + "\n x :"+x
                + "\n y :"+y
                + "\n Y - y :"+(maxY - y)
                + "\n cos beta :"+Math.cos(Math.toRadians(beta))
                + "\n cos beta * r :"+(Math.cos(Math.toRadians(beta))*r)
                + "\n new_x :"+new_x
                + "\n new_y :"+new_y
                + "\n maxX :"+maxX
                + "\n maxY :"+maxY);




        return oldRotation;
    }
    public void moveIt()
    {

        String SD = "";
        int originalHeight = tvMain.getHeight();
        int originalWidth = tvMain.getWidth();

       




//        if (se.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
//
//            for (int i = 0; i < se.values.length; i++)
//            {
//                SD += "acceleration[" + i + "] : " + se.values[i] + "`\n";
//            }
           // tv.setText(SD);
            //RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(150, 50);
            //layoutParams = (RelativeLayout.LayoutParams) tvMain.getLayoutParams();
            //layoutParams.leftMargin= (int) (layoutParams.leftMargin+se.values[1]-9.8-se.values[2]);
            //layoutParams.rightMargin = (int) (layoutParams.rightMargin+se.values[0]);
            //layoutParams.topMargin = (int) (layoutParams.topMargin+se.values[1]-9.8-se.values[2]);
            //layoutParams.bottomMargin = (int) (layoutParams.bottomMargin+se.values[1]-9.8-se.values[2]);

            final float alpha = (float) 0.8;

            float[] gravity = new float[3];
            gravity[0] = alpha * gravity[0] + (1 - alpha) * ACC_DATA[0];
            gravity[1] = alpha * gravity[1] + (1 - alpha) * ACC_DATA[1];
            gravity[2] = alpha * gravity[2] + (1 - alpha) * ACC_DATA[2];

            float[] linear_acceleration = new float[3];
            linear_acceleration[0] = ACC_DATA[0] - gravity[0];
            linear_acceleration[1] = ACC_DATA[1] - gravity[1];
            linear_acceleration[2] = ACC_DATA[2] - gravity[2];

        tvAngle.setText(Float.toString(ACC_DATA[0]));


            // tvMain.setLayoutParams(layoutParams);
            //tvMain.invalidate();



            //  tvMain.setX((left+right)/2);
            //  tvMain.setY((top+bottom)/2);

            /*
            if (tvMain.getX() + linear_acceleration[0] > left) {
                tvMain.setX(tvMain.getX() + linear_acceleration[0]);
            } else if (tvMain.getX() + linear_acceleration[0] <= left) {
                tvMain.setX(left);
            }
            if (tvMain.getX() + width + linear_acceleration[0] < right) {
                tvMain.setX(tvMain.getX() + linear_acceleration[0]);
            } else if (tvMain.getX() + width + linear_acceleration[0] >= right) {
                tvMain.setX(right - width);
            }
            if ((tvMain.getY() - (linear_acceleration[1] - se.values[2])) > top) {
                tvMain.setY((float) (tvMain.getY() - (linear_acceleration[1] - se.values[2])));
            } else if ((tvMain.getY() - (linear_acceleration[1] - se.values[2])) <= top) {
                tvMain.setY(top);
            }
            if ((tvMain.getY() + height - (linear_acceleration[1] - se.values[2])) < bottom) {
                tvMain.setY((float) (tvMain.getY() - (linear_acceleration[1] - se.values[2])));
            } else if ((tvMain.getY() + height - (linear_acceleration[1] - se.values[2])) >= bottom) {
                tvMain.setY(bottom - height);
            }
            */


//        }
//        if (se.sensor.getType() == Sensor.TYPE_GYROSCOPE)
//        {
//            for (int i = 0; i < se.values.length; i++)
//            {
//                SD += "gyroscope[" + (i+3) + "] : " + se.values[i] + "`\n";
//            }

          //  tv2.setText(SD);
            final float NS2S = 1.0f / 1000000000.0f;
            final float[] deltaRotationVector = new float[4];
            float tStamp = 0;
            final double EPSILON = 0.000001;
            if (tStamp != 0) {
                final float dT = (timestamp - tStamp) * NS2S;
                // Axis of the rotation sample, not normalized yet.
                float axisX = GYR_DATA[0];
                float axisY = GYR_DATA[1];
                float axisZ = GYR_DATA[2];

                // Calculate the angular speed of the sample
                float omegaMagnitude = sqrt(axisX*axisX + axisY*axisY + axisZ*axisZ);

                // Normalize the rotation vector if it's big enough to get the axis
                if (omegaMagnitude > EPSILON) {
                    axisX /= omegaMagnitude;
                    axisY /= omegaMagnitude;
                    axisZ /= omegaMagnitude;
                }

                // Integrate around this axis with the angular speed by the timestep
                // in order to get a delta rotation from this sample over the timestep
                // We will convert this axis-angle representation of the delta rotation
                // into a quaternion before turning it into the rotation matrix.
                float thetaOverTwo = omegaMagnitude * dT / 2.0f;
                float sinThetaOverTwo = sin(thetaOverTwo);
                float cosThetaOverTwo = cos(thetaOverTwo);
                deltaRotationVector[0] = sinThetaOverTwo * axisX;
                deltaRotationVector[1] = sinThetaOverTwo * axisY;
                deltaRotationVector[2] = sinThetaOverTwo * axisZ;
                deltaRotationVector[3] = cosThetaOverTwo;
            }
            tStamp = timestamp;
            float[] deltaRotationMatrix = new float[9];
            SensorManager.getRotationMatrixFromVector(deltaRotationMatrix, deltaRotationVector);
            /*
            for (int i = 0; i < deltaRotationMatrix.length; i++)
            {
                SD += "delta [" + (i+3) + "] : " + deltaRotationMatrix[i] + "`\n";
            }
            */

            //tv2.setText(SD);
            //tvMain.setX(originalWidth*2);
            //tvMain.setY(originalHeight*2);

            if (tvMain.getX() - GYR_DATA[1] +GYR_DATA[2]> left) {
                smoothMove('x', tvMain.getX(), (-GYR_DATA[1] + GYR_DATA[2]));
            } else if (tvMain.getX() - GYR_DATA[1] +GYR_DATA[2] <= left) {
                tvMain.setX(left);
            }
            if (tvMain.getX() + width - GYR_DATA[1] +GYR_DATA[2] < right) {
                smoothMove('x', tvMain.getX(), (-GYR_DATA[1] + GYR_DATA[2]));
            } else if (tvMain.getX() + width - GYR_DATA[1] +GYR_DATA[2] >= right) {
                tvMain.setX(right - width);
            }
            if ((tvMain.getY() - (GYR_DATA[0]  +GYR_DATA[2])) > top) {
                smoothMove('y', tvMain.getY(), -(GYR_DATA[0] + GYR_DATA[2]));
            } else if ((tvMain.getY() - (GYR_DATA[0]  +GYR_DATA[2])) <= top) {
                tvMain.setY(top);
            }
            if ((tvMain.getY() + height - (GYR_DATA[0]  +GYR_DATA[2])) < bottom) {
                smoothMove('y', tvMain.getY(), -(GYR_DATA[0] + GYR_DATA[2]));
            } else if ((tvMain.getY() + height - (GYR_DATA[0] +GYR_DATA[2] )) >= bottom) {
                tvMain.setY(bottom - height);
            }

//        }
    }

    void smoothMove(char type,float position, double subtractValue)
    {
        float smooth ;
        int smoothLevel = 3;
        if (type=='x')
        {
            if (smoothLevel!=0) {
                for (int i = 0; i < smoothLevel; i++) {
                    smooth = (float) (position + subtractValue / (2 ^ (i + 1)));
                    tvMain.setX(smooth);
                }
            }
            smooth = (float) (position + subtractValue/(2^(smoothLevel)));
            tvMain.setX(smooth);

        }
        else if (type=='y')
        {
            if (smoothLevel!=0) {
                for (int i = 0; i < smoothLevel; i++) {
                    smooth = (float) (position + subtractValue / (2 ^ (i + 1)));
                    tvMain.setY(smooth);
                }
            }
            smooth = (float) (position + subtractValue/(2^(smoothLevel)));
            tvMain.setY(smooth);


        }

    }
}
