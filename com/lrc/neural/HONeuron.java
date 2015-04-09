package com.lrc.neural;

import com.lrc.neural.*;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public abstract class HONeuron implements com.lrc.neural.Neuron {
    private double output;
    protected Weight[] weights;

    public double out() {
        return output;
    }

    /**
     * Neuron also saves this result for subsequent calls to  out().
     */
    public void activate() {
        output = f(dot());
    }

    abstract double f_prime(double dotprod);

    /**
     * Compute the dot product of the Weight vector. Pass this result to activate!
     *
     * @return DOCUMENT ME!
     */
    double dot() {
        double sum = 0d;

        for (int i = 0; i < weights.length; ++i)
            sum += (weights[i].w*weights[i].n.out());

        return sum;
    }

    /**
     * does the 'sigmoid' on the dot() product.
     *
     * @return DOCUMENT ME!
     */
    abstract double f(double dotprod);

    public Weight[] getWeights() {
        return weights;
    }
}
