package com.unist.netlab.fakturk.more;

import android.graphics.Point;
import android.hardware.SensorManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.*;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DecimalFormat;

import Jama.Matrix;

import static android.util.FloatMath.cos;
import static android.util.FloatMath.sin;
import static android.util.FloatMath.sqrt;

/**
 * Created by fakturk on 12/29/15.
 */
public class Move
{

    private static final float GRAVITY_EARTH = 9.80665f;
    DecimalFormat df = new DecimalFormat("#.####");

    //SensorEvent se;
    TextView tv, tv2;
    TextView tvMain;
    TextView tvAngle;
    RelativeLayout root;
    //float[] events =new float[9];;
    float[] ACC_DATA, GYR_DATA,MAG_DATA, ACC_DATA_LPF, gravity;
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
    float x,y, new_x=276, new_y=530;
    double r;
    double  beta=0, betaR;//angles, beta is the current angle and alpha is the change
    Display display;

    private boolean initState = true;


    // angular speeds from gyro
    private float[] gyro = new float[3];

    // rotation matrix from gyro data
    private float[] gyroMatrix = new float[9];

    // orientation angles from gyro matrix
    private float[] gyroOrientation = new float[3];

    // magnetic field vector
    private float[] magnet = new float[3];

    // accelerometer vector
    private float[] accel = new float[3];

    // orientation angles from accel and magnet
    private float[] accMagOrientation = new float[3];

    // final orientation angles from sensor fusion
    private float[] fusedOrientation = new float[3];

    // accelerometer and magnetometer based rotation matrix
    private float[] rotationMatrix = new float[9];

    public Move(double[][] q, float[] ACC_DATA, float[] GYR_DATA, float[] MAG_DATA, float[] gravity, long timestamp, Display mdisp, TextView tv, TextView tv2, TextView tvMain, TextView tvAngle, RelativeLayout root)
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
        this.MAG_DATA = new float[3];
        this.gravity = new float[3];
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
            this.MAG_DATA[i]=MAG_DATA[i];
            this.gravity[i] = gravity[i];
//            this.GRA_DATA[i]=GRA_DATA[i];
        }



        gyroOrientation[0] = 0.0f;
        gyroOrientation[1] = 0.0f;
        gyroOrientation[2] = 0.0f;

        // initialise gyroMatrix with identity matrix
        gyroMatrix[0] = 1.0f; gyroMatrix[1] = 0.0f; gyroMatrix[2] = 0.0f;
        gyroMatrix[3] = 0.0f; gyroMatrix[4] = 1.0f; gyroMatrix[5] = 0.0f;
        gyroMatrix[6] = 0.0f; gyroMatrix[7] = 0.0f; gyroMatrix[8] = 1.0f;
    }

    public float[][] lyingMove(Display mdisp,float[] oldAcc, float[] oldVelocity, float[] oldDistance, float[] gravity)
    {
        float dt = (System.nanoTime() - timestamp) / 1000000000.0f;
        final float alpha = (float) 0.98;
        //float  velocityX, distanceX, velocityY, distanceY;
        float[] velocity = new float[3];
        float[] distance = new float[3];
        float[] projection = new float[3];
        float[] horizontal = new float[3];
        //float[] oldAcc = new float[3];

//        float[] gravity = new float[3];
//        gravity[0] = alpha * gravity[0] + (1 - alpha) * ACC_DATA[0];
//        gravity[1] = alpha * gravity[1] + (1 - alpha) * ACC_DATA[1];
//        gravity[2] = alpha * gravity[2] + (1 - alpha) * ACC_DATA[2];

        float[] linear_acceleration = new float[3];
        linear_acceleration[0] = ACC_DATA[0] - gravity[0];
        linear_acceleration[1] = ACC_DATA[1] - gravity[1];
        linear_acceleration[2] = ACC_DATA[2] - gravity[2];

        float projectionCoefficient = 0;
        for (int i = 0; i < 3; i++)
        {
            projectionCoefficient+= gravity[i]*linear_acceleration[i];
        }
        for (int i = 0; i < 3; i++)
        {
            projection[i]=projectionCoefficient*gravity[i];
            horizontal[i]=linear_acceleration[i]-projection[i];
        }

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
                        + "X =  " + g[0] + "\n"
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

        accDiff[0] = oldAcc[0]- linear_acceleration[0];
        accDiff[1] = oldAcc[1]-linear_acceleration[1];
        accDiff[2] = oldAcc[2]-linear_acceleration[2];


        Matrix X = kalman.filter(dt,linear_acceleration);

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
        if(new_y<50)
        {
            new_y=50;
            velocity[0] = 0;
            // distanceX = 0;
        }
        if (new_y>bottom-height-50)
        {
            new_y = bottom-height-50;
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
    public void moveIt(float[] ACC_DATA, float[] GYR_DATA, float[] MAG_DATA)
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
                getRotationVectorFromGyro(GYR_DATA, deltaRotationVector, dT / 2.0f);
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

        // initialisation of the gyroscope based rotation matrix
        if(initState) {
            float[] initMatrix = new float[9];
            calculateAccMagOrientation(rotationMatrix,ACC_DATA,MAG_DATA,accMagOrientation);
            initMatrix = getRotationMatrixFromOrientation(accMagOrientation);
            float[] test = new float[3];
            SensorManager.getOrientation(initMatrix, test);
            gyroMatrix = matrixMultiplication(gyroMatrix, initMatrix);
            initState = false;
        }
        // copy the new gyro values into the gyro array
        // convert the raw gyro data into a rotation vector

        calculateAccMagOrientation(rotationMatrix,ACC_DATA,MAG_DATA,accMagOrientation);

        float FILTER_COEFFICIENT = 0.98f;
        float oneMinusCoeff = 1.0f - FILTER_COEFFICIENT;
        fusedOrientation[0] =
                FILTER_COEFFICIENT * gyroOrientation[0]
                        + oneMinusCoeff * accMagOrientation[0];

        fusedOrientation[1] =
                FILTER_COEFFICIENT * gyroOrientation[1]
                        + oneMinusCoeff * accMagOrientation[1];

        fusedOrientation[2] =
                FILTER_COEFFICIENT * gyroOrientation[2]
                        + oneMinusCoeff * accMagOrientation[2];

        // overwrite gyro matrix and orientation with fused orientation
        // to comensate gyro drift
        gyroMatrix = getRotationMatrixFromOrientation(fusedOrientation);
        System.arraycopy(fusedOrientation, 0, gyroOrientation, 0, 3);



        // convert rotation vector into rotation matrix
        float[] deltaMatrix = new float[9];
        SensorManager.getRotationMatrixFromVector(deltaMatrix, deltaRotationVector);

        // apply the new rotation interval on the gyroscope based rotation matrix
        gyroMatrix = matrixMultiplication(gyroMatrix, deltaMatrix);

        // get the gyroscope based orientation from the rotation matrix
        SensorManager.getOrientation(gyroMatrix, gyroOrientation);

        gravity = getGravity(gyroMatrix, ACC_DATA);



        for (int i = 0; i < 3; i++)
        {
            for (int j=0; j<3; j++)
            {
                SD += "d[" + (i*3+j) + "] : " + df.format(gyroMatrix[i*3+j]) + ", ";
            }
            SD+="\n";
        }




        tvAngle.setText("Inclination : "+inclination
                +",\n Rotation : "+rotation
                +",\n acc - g : "+(norm_Of_g - GRAVITY_EARTH)
                +",\n gravity : "+(df.format(gravity[0])+", "+df.format(gravity[1])+", "+df.format(gravity[2]))
                );
        tv.setText(",\n"+SD);




//            tv2.setText(SD);
            //tvMain.setX(originalWidth*2);
            //tvMain.setY(originalHeight*2);
        float threshold = 0.1f;


            if (tvMain.getX() - GYR_DATA[1] + GYR_DATA[2] > left)
            {
                smoothMove('x', tvMain.getX(), (-GYR_DATA[1] + GYR_DATA[2]));
            } else if (tvMain.getX() - GYR_DATA[1] + GYR_DATA[2] <= left)
            {
                tvMain.setX(left);
            }
            if (tvMain.getX() + width - GYR_DATA[1] + GYR_DATA[2] < right)
            {
                smoothMove('x', tvMain.getX(), (-GYR_DATA[1] + GYR_DATA[2]));
            } else if (tvMain.getX() + width - GYR_DATA[1] + GYR_DATA[2] >= right)
            {
                tvMain.setX(right - width);
            }
            if ((tvMain.getY() - (GYR_DATA[0] + GYR_DATA[2])) > top)
            {
                smoothMove('y', tvMain.getY(), -(GYR_DATA[0] + GYR_DATA[2]));
            } else if ((tvMain.getY() - (GYR_DATA[0] + GYR_DATA[2])) <= top)
            {
                tvMain.setY(top);
            }
            if ((tvMain.getY() + height - (GYR_DATA[0] + GYR_DATA[2])) < bottom)
            {
                smoothMove('y', tvMain.getY(), -(GYR_DATA[0] + GYR_DATA[2]));
            } else if ((tvMain.getY() + height - (GYR_DATA[0] + GYR_DATA[2])) >= bottom)
            {
                tvMain.setY(bottom - height);
            }

//        }
    }

    void smoothMove(char type,float position, double subtractValue)
    {
        float smooth ;
        int smoothLevel = 0;
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

    private float[] matrixMultiplication(float[] A, float[] B)
    {
        float[] result = new float[9];

        result[0] = A[0] * B[0] + A[1] * B[3] + A[2] * B[6];
        result[1] = A[0] * B[1] + A[1] * B[4] + A[2] * B[7];
        result[2] = A[0] * B[2] + A[1] * B[5] + A[2] * B[8];

        result[3] = A[3] * B[0] + A[4] * B[3] + A[5] * B[6];
        result[4] = A[3] * B[1] + A[4] * B[4] + A[5] * B[7];
        result[5] = A[3] * B[2] + A[4] * B[5] + A[5] * B[8];

        result[6] = A[6] * B[0] + A[7] * B[3] + A[8] * B[6];
        result[7] = A[6] * B[1] + A[7] * B[4] + A[8] * B[7];
        result[8] = A[6] * B[2] + A[7] * B[5] + A[8] * B[8];

        return result;
    }

    private float[] getGravity(float[] rotationMatrix, float[] acc)
    {
        float[] result = new float[3];
        float[] noise = new float[3];
        double[][] R = {{rotationMatrix[0],rotationMatrix[1],rotationMatrix[2]},
                {rotationMatrix[3],rotationMatrix[4],rotationMatrix[5]},
                {rotationMatrix[6],rotationMatrix[7],rotationMatrix[8]}};
        Matrix Rotation = new Matrix(R);
        Matrix inverseRotation=Rotation.inverse();

        noise[0] =  acc[0]-(float)(inverseRotation.get(0,2)*GRAVITY_EARTH);
        noise[1] =  acc[1]-(float)(inverseRotation.get(1,2)*GRAVITY_EARTH);
        noise[2] =  acc[2]-(float)(inverseRotation.get(2,2)*GRAVITY_EARTH);


        result[0] = (float) inverseRotation.get(0,2)*GRAVITY_EARTH;
        result[1] = (float) inverseRotation.get(1,2)*GRAVITY_EARTH;
        result[2] = (float) inverseRotation.get(2,2)*GRAVITY_EARTH;



        return result;
    }

    private void getRotationVectorFromGyro(float[] gyroValues,
                                           float[] deltaRotationVector,
                                           float timeFactor)
    {
        float[] normValues = new float[3];

        // Calculate the angular speed of the sample
        float omegaMagnitude =
                (float)Math.sqrt(gyroValues[0] * gyroValues[0] +
                        gyroValues[1] * gyroValues[1] +
                        gyroValues[2] * gyroValues[2]);

        // Normalize the rotation vector if it's big enough to get the axis
     float EPSILON = 0.000000001f;
        if(omegaMagnitude > EPSILON) {
            normValues[0] = gyroValues[0] / omegaMagnitude;
            normValues[1] = gyroValues[1] / omegaMagnitude;
            normValues[2] = gyroValues[2] / omegaMagnitude;
        }

        // Integrate around this axis with the angular speed by the timestep
        // in order to get a delta rotation from this sample over the timestep
        // We will convert this axis-angle representation of the delta rotation
        // into a quaternion before turning it into the rotation matrix.
        float thetaOverTwo = omegaMagnitude * timeFactor;
        float sinThetaOverTwo = (float)Math.sin(thetaOverTwo);
        float cosThetaOverTwo = (float)Math.cos(thetaOverTwo);
        deltaRotationVector[0] = sinThetaOverTwo * normValues[0];
        deltaRotationVector[1] = sinThetaOverTwo * normValues[1];
        deltaRotationVector[2] = sinThetaOverTwo * normValues[2];
        deltaRotationVector[3] = cosThetaOverTwo;
    }

    private float[] getRotationMatrixFromOrientation(float[] o) {
        float[] xM = new float[9];
        float[] yM = new float[9];
        float[] zM = new float[9];

        float sinX = (float)Math.sin(o[1]);
        float cosX = (float)Math.cos(o[1]);
        float sinY = (float)Math.sin(o[2]);
        float cosY = (float)Math.cos(o[2]);
        float sinZ = (float)Math.sin(o[0]);
        float cosZ = (float)Math.cos(o[0]);

        // rotation about x-axis (pitch)
        xM[0] = 1.0f; xM[1] = 0.0f; xM[2] = 0.0f;
        xM[3] = 0.0f; xM[4] = cosX; xM[5] = sinX;
        xM[6] = 0.0f; xM[7] = -sinX; xM[8] = cosX;

        // rotation about y-axis (roll)
        yM[0] = cosY; yM[1] = 0.0f; yM[2] = sinY;
        yM[3] = 0.0f; yM[4] = 1.0f; yM[5] = 0.0f;
        yM[6] = -sinY; yM[7] = 0.0f; yM[8] = cosY;

        // rotation about z-axis (azimuth)
        zM[0] = cosZ; zM[1] = sinZ; zM[2] = 0.0f;
        zM[3] = -sinZ; zM[4] = cosZ; zM[5] = 0.0f;
        zM[6] = 0.0f; zM[7] = 0.0f; zM[8] = 1.0f;

        // rotation order is y, x, z (roll, pitch, azimuth)
        float[] resultMatrix = matrixMultiplication(xM, yM);
        resultMatrix = matrixMultiplication(zM, resultMatrix);
        return resultMatrix;
    }
    public void calculateAccMagOrientation(float[] rotationMatrix, float[] accel, float[] magnet, float[] accMagOrientation) {
        if(SensorManager.getRotationMatrix(rotationMatrix, null, accel, magnet)) {
            SensorManager.getOrientation(rotationMatrix, accMagOrientation);
        }
    }

}


