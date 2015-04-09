package com.lrc.neural;

import com.lrc.neural.*;


/**
 * We have thought about possible non-Backprop learning strategies. We have come up with
 * one.
 * 
 * <p>
 * Basically, there are two classes of layer groups -- the classifier layer  group, and
 * the identifier layer group. The classifier layer group is trained given sample imputs
 * along with a bias identifier to denote classification of input types.
 * </p>
 * 
 * <p>
 * The identifier layer is also similarly trained. The outputs of the identifier layer
 * correspond to the disired classes. Each class output  is trained to fire when its
 * class is present, and NOT to fire when its class is absent.
 * </p>
 * 
 * <p></p>
 */
public abstract class NeuralNet implements java.io.Serializable {
    protected Layer[] layers = null;

    /**
     * this constructor is just a place holder. The derivation must do the actual
     * construction.
     *
     * @param input DOCUMENT ME!
     * @param hidden DOCUMENT ME!
     * @param output DOCUMENT ME!
     */
    public NeuralNet(NeuronFactory input, NeuronFactory hidden, NeuronFactory output) {}
}
