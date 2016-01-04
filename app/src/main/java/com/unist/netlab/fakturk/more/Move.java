package com.unist.netlab.fakturk.more;

import android.hardware.SensorManager;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;

import static android.util.FloatMath.cos;
import static android.util.FloatMath.sin;
import static android.util.FloatMath.sqrt;

/**
 * Created by fakturk on 12/29/15.
 */
public class Move
{
    //SensorEvent se;
    TextView tv;
    TextView tvMain;
    RelativeLayout root;
    float[] events =new float[9];;
    float timestamp;

    public Move(float[] e, float timestamp, TextView tv, TextView tvMain, RelativeLayout root)
    {
        //this.se = se;
        this.tv = tv;

        this.tvMain = tvMain;
        this.root = root;
        this.timestamp = timestamp;
       // events
        for (int i=0;i<8;i++)
        {
            Log.d("eventAtama", Float.toString(e[i]));
            this.events[i]=e[i];
        }
    }

    public void moveIt()
    {

        String SD = "";
        int originalHeight = tvMain.getHeight();
        int originalWidth = tvMain.getWidth();

        int top = root.getTop();
        int bottom = root.getBottom();
        int left = root.getLeft();
        int right = root.getRight();
        int height = tvMain.getHeight();
        int width = tvMain.getWidth();


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
            gravity[0] = alpha * gravity[0] + (1 - alpha) * events[0];
            gravity[1] = alpha * gravity[1] + (1 - alpha) * events[1];
            gravity[2] = alpha * gravity[2] + (1 - alpha) * events[2];

            float[] linear_acceleration = new float[3];
            linear_acceleration[0] = events[0] - gravity[0];
            linear_acceleration[1] = events[1] - gravity[1];
            linear_acceleration[2] = events[2] - gravity[2];


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
                float axisX = events[3];
                float axisY = events[4];
                float axisZ = events[5];

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

            if (tvMain.getX() - events[4] +events[5]> left) {
                smoothMove('x', tvMain.getX(), (-events[4] + events[5]));
            } else if (tvMain.getX() - events[4] +events[5] <= left) {
                tvMain.setX(left);
            }
            if (tvMain.getX() + width - events[4] +events[5] < right) {
                smoothMove('x', tvMain.getX(), (-events[4] + events[5]));
            } else if (tvMain.getX() + width - events[4] +events[5] >= right) {
                tvMain.setX(right - width);
            }
            if ((tvMain.getY() - (events[3]  +events[5])) > top) {
                smoothMove('y', tvMain.getY(), -(events[3] + events[5]));
            } else if ((tvMain.getY() - (events[3]  +events[5])) <= top) {
                tvMain.setY(top);
            }
            if ((tvMain.getY() + height - (events[3]  +events[5])) < bottom) {
                smoothMove('y', tvMain.getY(), -(events[3] + events[5]));
            } else if ((tvMain.getY() + height - (events[3] +events[5] )) >= bottom) {
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
