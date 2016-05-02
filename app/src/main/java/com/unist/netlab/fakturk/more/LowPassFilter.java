package com.unist.netlab.fakturk.more;

/**
 * Created by fakturk on 16. 5. 2.
 */
public class LowPassFilter {
    public LowPassFilter() {
    }

    protected float[] lowPass(float factor, float[] input, float[] output)
    {

        if (output == null) return input;
        for (int i = 0; i < input.length; i++)
        {
            output[i] = output[i] + factor * (input[i] - output[i]);
        }
        return output;
    }
}
