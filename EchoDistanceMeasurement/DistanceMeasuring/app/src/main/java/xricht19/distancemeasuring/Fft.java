package xricht19.distancemeasuring;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Jirka on 9. 5. 2015.
 */
public class Fft {
    /*
         * Computes the discrete Fourier transform (DFT) of the given complex vector, storing the result back into the vector.
         * The vector can have any length. This is a wrapper function.
         */
    public static void transform(double[] real, double[] imag) {
        if (real.length != imag.length)
            throw new IllegalArgumentException("Mismatched lengths");

        int n = real.length;
        if (n == 0)
            return;
        else if ((n & (n - 1)) == 0)  // Is power of 2
            transformRadix2(real, imag);
    }


    /*
     * Computes the inverse discrete Fourier transform (IDFT) of the given complex vector, storing the result back into the vector.
     * The vector can have any length. This is a wrapper function. This transform does not perform scaling, so the inverse is not a true inverse.
     */
    public static void inverseTransform(double[] real, double[] imag) {
        transform(imag, real);
    }


    /*
     * Computes the discrete Fourier transform (DFT) of the given complex vector, storing the result back into the vector.
     * The vector's length must be a power of 2. Uses the Cooley-Tukey decimation-in-time radix-2 algorithm.
     */
    public static void transformRadix2(double[] real, double[] imag) {
        // Initialization
        if (real.length != imag.length)
            throw new IllegalArgumentException("Mismatched lengths");
        int n = real.length;
        int levels = 31 - Integer.numberOfLeadingZeros(n);  // Equal to floor(log2(n))
        if (1 << levels != n)
            throw new IllegalArgumentException("Length is not a power of 2");
        double[] cosTable = new double[n / 2];
        double[] sinTable = new double[n / 2];
        for (int i = 0; i < n / 2; i++) {
            cosTable[i] = Math.cos(2 * Math.PI * i / n);
            sinTable[i] = Math.sin(2 * Math.PI * i / n);
        }

        // Bit-reversed addressing permutation
        for (int i = 0; i < n; i++) {
            int j = Integer.reverse(i) >>> (32 - levels);
            if (j > i) {
                double temp = real[i];
                real[i] = real[j];
                real[j] = temp;
                temp = imag[i];
                imag[i] = imag[j];
                imag[j] = temp;
            }
        }

        // Cooley-Tukey decimation-in-time radix-2 FFT
        for (int size = 2; size <= n; size *= 2) {
            int halfsize = size / 2;
            int tablestep = n / size;
            for (int i = 0; i < n; i += size) {
                for (int j = i, k = 0; j < i + halfsize; j++, k += tablestep) {
                    double tpre =  real[j+halfsize] * cosTable[k] + imag[j+halfsize] * sinTable[k];
                    double tpim = -real[j+halfsize] * sinTable[k] + imag[j+halfsize] * cosTable[k];
                    real[j + halfsize] = real[j] - tpre;
                    imag[j + halfsize] = imag[j] - tpim;
                    real[j] += tpre;
                    imag[j] += tpim;
                }
            }
            if (size == n)  // Prevent overflow in 'size *= 2'
                break;
        }
    }

    public static ArrayList<Double> correlation(ArrayList<Short> x, ArrayList<Short> y){
        // find size of signal
        int signalSizeCorr;
        if (x.size() > y.size())
            signalSizeCorr = x.size();
        else
            signalSizeCorr = y.size();
        //      - change the size to be at least two times bigger than signalSizeCorr and also
        //          to be power of 2
        Collections.reverse(y);
        ArrayList<Short> newOrigSig = zeroPad(x,signalSizeCorr);
        ArrayList<Short> newRecSig = zeroPad(y,signalSizeCorr);
        //      - correlation of origSig and reverse recSig
        double[] corrD = new double[newOrigSig.size()];

        Fft.correlation(toPrimitive(newOrigSig), toPrimitive(newRecSig), corrD);

        return toArrayList(corrD);
    }

    /*
    * Computes the circular convolution of the given real vectors. Each vector's length must be the same.
    */
    public static void correlation(double[] x, double[] y, double[] out) {
        if (x.length != y.length || x.length != out.length)
            throw new IllegalArgumentException("Mismatched lengths");
        int n = x.length;
        correlation(x, new double[n], y, new double[n], out, new double[n]);
    }


    /*
     * Computes the circular convolution of the given complex vectors. Each vector's length must be the same.
     */
    public static void correlation(double[] xreal, double[] ximag, double[] yreal, double[] yimag, double[] outreal, double[] outimag) {
        if (xreal.length != ximag.length || xreal.length != yreal.length || yreal.length != yimag.length || xreal.length != outreal.length || outreal.length != outimag.length)
            throw new IllegalArgumentException("Mismatched lengths");

        int n = xreal.length;
        xreal = xreal.clone();
        ximag = ximag.clone();
        yreal = yreal.clone();
        yimag = yimag.clone();

        transform(xreal, ximag);
        transform(yreal, yimag);
        for (int i = 0; i < n; i++) {
            double temp = xreal[i] * yreal[i] - ximag[i] * yimag[i];
            ximag[i] = ximag[i] * yreal[i] + xreal[i] * yimag[i];
            xreal[i] = temp;
        }
        inverseTransform(xreal, ximag);
        for (int i = 0; i < n; i++) {  // Scaling (because this FFT implementation omits it)
            outreal[i] = xreal[i] / n;
            outimag[i] = ximag[i] / n;
        }
    }

    // ------------------------------------------------------------------------------------------ //
    // edit signals to be ready for FFT - zeroPad to right size ------------------------------------
    public static ArrayList<Short> zeroPad(ArrayList<Short> signal, int size){
        ArrayList<Short> optimalSig = new ArrayList<Short>();

        // find first power of the for 2 times size
        int newSize = 2 * size;
        newSize = log2nlz(newSize);
        newSize = (int)Math.pow(2, newSize);

        // zero-pad the array from input
        ArrayList<Short> zeros = new ArrayList<Short>(Collections.nCopies((newSize - signal.size()), (short) 0));
        //optimalSig.addAll(zeros);
        optimalSig.addAll(signal);
        optimalSig.addAll(zeros);
        return optimalSig;
    }
    // calculate base 2 logarithm
    public static int log2nlz( int bits ){
        if( bits == 0 )
            return 0; // or throw exception
        return 31 - Integer.numberOfLeadingZeros( bits );
    }
    // Array list of shorts to double
    public static double[] toPrimitive(ArrayList<Short> array) {
        if (array == null) {
            return null;
        } else if (array.size() == 0) {
            return null;
        }
        final double[] result = new double[array.size()];
        for (int i = 0; i < array.size(); i++)
            result[i] = array.get(i);
        return result;
    }
    // Double array to double ArrayList
    public static ArrayList<Double> toArrayList(double[] array){
        ArrayList<Double> temp = new ArrayList<Double>();
        for (double item : array){
            temp.add(item);
        }
        return temp;
    }

}
