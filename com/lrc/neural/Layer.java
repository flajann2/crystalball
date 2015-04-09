package com.lrc.neural;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class Layer implements java.io.Serializable {
    public Neuron[] neurons;

    public void activate() {
        for (int i = 0; i < neurons.length; ++i)
            if (neurons[i] instanceof HONeuron)
                ((HONeuron) neurons[i]).activate();
    }
}
