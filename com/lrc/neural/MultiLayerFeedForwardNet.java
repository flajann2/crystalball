package com.lrc.neural;

import com.lrc.neural.*;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class MultiLayerFeedForwardNet extends com.lrc.neural.NeuralNet {
    public MultiLayerFeedForwardNet(NeuronFactory input, NeuronFactory hidden,
                                    int hiddenCount, NeuronFactory output) {
        super(input, hidden, output);
    }
}
