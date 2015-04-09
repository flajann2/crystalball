package com.lrc.neural;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public interface NeuronFactory extends java.io.Serializable {
    Neuron[] getNeuronLayer(int layerID, Neuron[] previousLayer);
}
