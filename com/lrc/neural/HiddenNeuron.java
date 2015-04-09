package com.lrc.neural;

import com.lrc.neural.*;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class HiddenNeuron extends com.lrc.neural.HONeuron {
    static double tanh(double x) {
        double ex = Math.exp(x);
        double emx = Math.exp(-x);

        return (ex-emx)/(ex+emx);
    }

    /** */
    double f(double dp) {
        return tanh(dp);
    }

    double f_prime(double dp) {
        double th = tanh(dp);

        return 1d-(th*th);
    }
}
