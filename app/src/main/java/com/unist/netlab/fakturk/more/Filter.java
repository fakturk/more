package com.unist.netlab.fakturk.more;

/**
 * Created by fakturk on 16. 5. 2.
 */
public class Filter {
    public Filter() {
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

    protected float[] highPass(float factor, float[] input, float[] output)
    {

        if (output == null) return input;
        for (int i = 0; i < input.length; i++)
        {
            output[i] = factor*output[i] + (1-factor) * (input[i]);
        }

        return output;
    }

}
