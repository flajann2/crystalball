package com.lrc.neural;

import com.lrc.neural.*;


/**
 * this net has no hidden layers. Just the input and output layers.
 */
public class SimpleNet extends com.lrc.neural.NeuralNet {
    public SimpleNet(NeuronFactory input, NeuronFactory hidden, NeuronFactory output) {
        super(input, hidden, output);
    }
}
