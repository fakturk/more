package com.unist.netlab.fakturk.more;

import android.widget.TextView;

/**
 * Created by fakturk on 1/6/16.
 */
public class DisplayChange

{
    TextView tv;
    TextView tvAngle;


    public DisplayChange(TextView tv, TextView tvAngle)
    {

        this.tv = tv;
        this.tvAngle = tvAngle;
    }

    void setDisplay(String ACC, String GYR, String GRA)
    {

        tv.setText("ACC : \n"+ACC+
                "\n"+
                "GYR : \n"+GYR+
                "\n"+
                "GRA : \n"+GRA);
    }

    void setTvAngle(float[] ACC, float[] GYR, float[] GRA)
    {

        tvAngle.setText(String.valueOf(ACC[0])+", "+
                String.valueOf(ACC[1])+", "+
                String.valueOf(ACC[2])+", \n"
                );
    }
}
