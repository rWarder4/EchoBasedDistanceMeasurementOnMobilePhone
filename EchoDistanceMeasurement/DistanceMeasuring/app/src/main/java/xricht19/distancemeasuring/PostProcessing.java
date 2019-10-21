package xricht19.distancemeasuring;

import android.os.Environment;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.logging.Handler;

/**
 * Created by Jirka on 11. 4. 2015.
 */
public class PostProcessing implements Runnable {
    // create variables
    private UserInterface parent;
    private short[] recordedSound;
    private UserInterface.TypesOfSignals type;
    ArrayList<ArrayList<Short>> recordedSignals;
    ArrayList<ArrayList<Double>> correlationOfSignals;
    ArrayList<Double> averageCorr;
    ArrayList<ArrayList<Short>> originalSignals;
    Boolean preprocessFinished = false;
    ArrayList<Double> distance;
    Double distanceValue = 0.0;
    // for separation in type 1
    private static final double maximumThreshold = 0.4*32768;
    private static final double minimumThreshold = 0.2*32768;
    private static final int separationStart = -120;
    private static final int separationStep = 1200;
    // enable/disable writing to files
    private static final boolean enableWriteToFiles = false;
    // for communication with UI thread
    Message message;

    // ------------------------------------------------------------------------------------------ //
    public PostProcessing(UserInterface parent, UserInterface.TypesOfSignals type){
        this.parent = parent;
        this.type = type;
    }
    // ------------------------------------------------------------------------------------------ //
    public void run() {
        distance = new ArrayList<Double>();
        // do preprocessing
        preprocess();
        Log.e("Preprocess Done","!!!!!!!!!!!!!!!!!!!");
        // create message for handler
        message = Message.obtain();
        // choose type of measuring
        if (type == UserInterface.TypesOfSignals.SWITCHINGSILENT){
            postProcessingType1();
        } else if (type == UserInterface.TypesOfSignals.CONTINUOUS){
            postProcessingType2();
        }
    }
    // ------------------------------------------------------------------------------------------ //
    public ArrayList<Short> convertToShort(int start, int stop){
        ArrayList<Short> recSig = new ArrayList<Short>();
        for (int i=start; i<stop; i++) {
            recSig.add(recordedSound[i]);
        }
        return recSig;
    }
    // ------------------------------------------------------------------------------------------ //
    public Boolean isBiggerThanMinimumThreshold(ArrayList<Short> sig){
        // test if some value in array is bigger than minimum threshold
        if (Collections.max(sig)>minimumThreshold)
            return true;
        // else return false
        return false;
    }
    // ------------------------------------------------------------------------------------------ //
    public void saveFoundedSignal(int start, int stop){
        // save first signal if bigger than threshold
        ArrayList<Short> possSignal = convertToShort(start,stop);
        if (isBiggerThanMinimumThreshold(possSignal)){
            recordedSignals.add(possSignal);
        }
    }
    // ------------------------------------------------------------------------------------------ //
    // function return true if recorded signal was successfully separated --------------------------
    public Boolean isSignalSeparated(){
        if (recordedSignals != null ){
            return true;
        }
        return false;
    }
    // ------------------------------------------------------------------------------------------ //
    // function return separate recorded signals ---------------------------------------------------
    public ArrayList<ArrayList<Short>> getSeparateRecordedSignals(){
        return recordedSignals;
    }
    // ------------------------------------------------------------------------------------------ //
    // function return correlation of signals ------------------------------------------------------
    public ArrayList<ArrayList<Double>> getCorrelationOfSignals(){
        return correlationOfSignals;
    }
    //--------------------------------------------------------------------------------------------//
    public short[] getOrdinaryShortArray(ArrayList<Short> shortList){
        short[] arr = new short[shortList.size()];
        for (int i=0; i < shortList.size();i++){
            arr[i] =  shortList.get(i);
        }
        return arr;
    }
    //--------------------------------------------------------------------------------------------//
    public double[] getOrdinaryDoubleArray(ArrayList<Double> shortList){
        double[] arr = new double[shortList.size()];
        for (int i=0; i < shortList.size();i++){
            arr[i] =  shortList.get(i);
        }
        return arr;
    }
    //--------------------------------------------------------------------------------------------//
    //--------------------------------------------------------------------------------------------//
    // Saving recording sound to file.
    public void saveSignal(String fileName, short[] signal){
        // save loaded audio into file
        String mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        //add audio name
        mFileName += "/" + "distanceMeasuring" + "/" + String.valueOf(parent.soundVolume) + "/"
                + parent.distanceToName + "/" + parent.measuringSignal + "/" + fileName + ".txt";
        Log.i("mFileName:", mFileName);

        // create output stream
        BufferedWriter fos = null;
        try {
            fos = new BufferedWriter(new FileWriter(mFileName, false));
        } catch (IOException e) {
            Log.e("Output stream doesn't create.", e.getMessage());
        }
        try {
            // write recorded signal
            for (short item : signal){
                //Log.i("item written to file.", String.valueOf(item));
                fos.write(String.valueOf(item));
                fos.write("\r\n");
            }
            fos.close();
        } catch (NullPointerException e) {
            Log.e("Null Pointer Exception while writing to file.", e.getMessage());
        } catch (IOException e) {
            Log.e("Cannot write or close the file!", e.getMessage());
        } catch (Exception e) {
            Log.e("Unknown error!", e.getMessage());
        }
    }
    //--------------------------------------------------------------------------------------------//
    // Saving recording sound to file.
    public void saveSignal(String fileName, double[] signal){
        // save loaded audio into file
        String mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        //add audio name
        mFileName += "/" + "distanceMeasuring" + "/" + String.valueOf(parent.soundVolume) + "/"
                + parent.distanceToName + "/" + parent.measuringSignal + "/" + fileName + ".txt";
        Log.i("mFileName:", mFileName);

        // create output stream
        BufferedWriter fos = null;
        try {
            fos = new BufferedWriter(new FileWriter(mFileName, false));
        } catch (IOException e) {
            Log.e("Output stream doesn't create.", e.getMessage());
        }
        try {
            // write recorded signal
            for (double item : signal){
                //Log.i("item written to file.", String.valueOf(item));
                fos.write(String.valueOf(item));
                fos.write("\r\n");
            }
            fos.close();
        } catch (NullPointerException e) {
            Log.e("Null Pointer Exception while writing to file.", e.getMessage());
        } catch (IOException e) {
            Log.e("Cannot write or close the file!", e.getMessage());
        } catch (Exception e) {
            Log.e("Unknown error!", e.getMessage());
        }
    }
    //--------------------------------------------------------------------------------------------//
    // Saving recording sound to file.
    public void saveSignal(String fileName, double signal){
        // save loaded audio into file
        String mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        //add audio name
        mFileName += "/" + "distanceMeasuring" + "/" + String.valueOf(parent.soundVolume) + "/"
                + parent.distanceToName + "/" + parent.measuringSignal + "/" + fileName + ".txt";
        Log.i("mFileName:", mFileName);

        // create output stream
        BufferedWriter fos = null;
        try {
            fos = new BufferedWriter(new FileWriter(mFileName, false));
        } catch (IOException e) {
            Log.e("Output stream doesn't create.", e.getMessage());
        }
        try {
            // write value
            fos.write(String.valueOf(signal));
            fos.write("\r\n");
            fos.close();
        } catch (NullPointerException e) {
            Log.e("Null Pointer Exception while writing to file.", e.getMessage());
        } catch (IOException e) {
            Log.e("Cannot write or close the file!", e.getMessage());
        } catch (Exception e) {
            Log.e("Unknown error!", e.getMessage());
        }
    }
    //--------------------------------------------------------------------------------------------//
    // Loading sourceFiles/recordedSound from file.
    public ArrayList<ArrayList<Short>> loadOrigSignals(int fileNameId){
        // Open file.
        InputStream inputStream = parent.getApplicationContext().getResources().openRawResource(fileNameId);

        InputStreamReader inputReader = new InputStreamReader(inputStream);
        BufferedReader buffReader = new BufferedReader(inputReader);
        String line;
        StringBuilder text = new StringBuilder();
        // array for loading values from file
        ArrayList<ArrayList<Short>> myArray = new ArrayList<ArrayList<Short>>();
        ArrayList<Short> stringArray  = new ArrayList<Short>();

        try {
            while (( line = buffReader.readLine()) != null) {
                //Log.i("First line -----!!!!!", line);
                // cut line into array of shorts
                String[] splitArray = line.split(",");
                //Log.e(" Number of elements on line -----!!!!!", String.valueOf(splitArray.length));

                for (String item : splitArray) {
                    try {
                        Short shortItem = Short.parseShort(item);
                        stringArray.add(shortItem);
                    } catch (NumberFormatException e) {
                        Log.e("Cannot convert string to short when reading the file!", e.toString());
                    }
                }
                // add one line into collection
                myArray.add(stringArray);
                stringArray = new ArrayList<Short>();
            }
        } catch (IOException e) {
            Log.e("Error while reading the file", e.toString());
            return null;
        }

        // printing all values
        /*for (int i = 0; i < myArray.size(); i++) {
            System.out.println("values of index " + i + " are :" + myArray.get(i).toString());
        }*/

        return myArray;
    }
    // ------------------------------------------------------------------------------------------ //
    // function return correlation of signals ------------------------------------------------------
    public ArrayList<Double> getAverageCorr(){
        return averageCorr;
    }
    // ------------------------------------------------------------------------------------------ //
    // put buffer into vector of doubles -----------------------------------------------------------
    public void separateRecSignals(){
        // Check message
        Log.i("Recording finished!", "Separate signals!");
        recordedSignals = new ArrayList<ArrayList<Short>>();
        //int numOfFindPeaks = 0;
        int firstBiggerThatThreshold = -1;
        // find first bigger than threshold
        for (int i=0;i<recordedSound.length;i++){
            if (recordedSound[i] > maximumThreshold){
                Log.i("First bigger than threshold - index!!!!!", String.valueOf(i));
                Log.i("First bigger than threshold - value!!!!!", String.valueOf(recordedSound[i]));
                // found first bigger, breaking the cycle
                firstBiggerThatThreshold = i;
                break;
            }
        }
        if (firstBiggerThatThreshold == -1){
            Log.e("First bigger than threshold didn't found.", "Breaking app.");
            return;
        }
        //Log.i("Number of Peaks: ", String.valueOf(numOfFindPeaks));
        // Separate signals into array recordedSignals
        // take 2.5 ms before peak and 22.5 ms after founded peak
        //      -> 2.5 ms before peak = 120 samples
        int start = firstBiggerThatThreshold + separationStart;
        //      -> 22.5 ms after peak = 1080 samples
        int stop = start + separationStep;

        // save first founded signal in collection
        saveFoundedSignal(start,stop);

        // cycle until the coordinates are outside the array.
        while(true){
            // calculate new values of borders of signal
            start = stop;
            stop = stop+separationStep;
            // are we at the end of array
            if (start >= recordedSound.length)
                break;
            // Is stop still inside array?
            if (stop > recordedSound.length)
                stop = recordedSound.length;
            // save next signal in collection
            saveFoundedSignal(start,stop);
        }
    }
    // ------------------------------------------------------------------------------------------ //
    public ArrayList<Double> crossCorr(ArrayList<Short> origSig, ArrayList<Short> recSig){
        // calculate correlation using FFT
        // 1. Zero-pad the input signals to same size,
        //      - find size of bigger signals

        ArrayList<Double> corr = Fft.correlation(origSig, recSig);

        // return correlation of origSig and recSig
        return corr;
    }
    // ------------------------------------------------------------------------------------------ //
    // calculate cross-correlation for all pairs of signal -----------------------------------------
    public void crossCorrOfAllSignals(ArrayList<ArrayList<Short>> originalSignals){
        // variable for save of correlations
        correlationOfSignals = new ArrayList<ArrayList<Double>>();
        // calculate correlation of signal
        //      -> iterate over all signals
        int numbOfSignals = Math.min(originalSignals.size(),recordedSignals.size());
        for (int n=0; n<numbOfSignals; n++){
        //for (int n=0; n<5; n++){
            //  -> calculate correlation of signal
            correlationOfSignals.add(crossCorr(originalSignals.get(n), recordedSignals.get(n)));
            if(enableWriteToFiles) {
                Log.i("Korelaca signalu cislo n vytvotena: ", String.valueOf(n));
                Log.e("Korelaca signalu cislo n je : ", String.valueOf(correlationOfSignals.get(n)));
            }
        }
    }
    // ------------------------------------------------------------------------------------------ //
    // calculate average correlation of signal -----------------------------------------------------
    public void calculateAverageCorrelation(){
        // create value to save average correlation
        //  -> all correlations have same size, set size of average array as first of them
        averageCorr = new ArrayList<Double>();
        // fill average correlation collection with zeros
        for (int i=0;i<correlationOfSignals.get(0).size();i++){
            averageCorr.add((double)0);
        }
        // iterate over all correlations
        for (ArrayList<Double> item : correlationOfSignals){
            // iterate over all
            for (int i=0;i<item.size();i++){
                averageCorr.set(i,averageCorr.get(i)+item.get(i));
            }
        }
        // divide by number of correlated signals
        for (int i=0;i<correlationOfSignals.get(0).size();i++){
            averageCorr.set(i,averageCorr.get(i) / correlationOfSignals.size());
        }
    }
    // ------------------------------------------------------------------------------------------ //
    // calculate distance -----------------------------------------------------
    public ArrayList<Double> calculateDistance(){
        ArrayList<Double> distanceInfo = new ArrayList<Double>();
        //  -> speed of sound=343.59 m/s in 20 degrees
        double speedOfSound = 343.59;
        // skip cca 45 cm -> 135 samples
        int skipOver = 135;
        // reverse average correlation because it's in bad order after calculation through FFT
        Collections.reverse(averageCorr);
        // find maximum of signal
        int maxValueOfCorr = getIndexOfAbsoluteMaximum(averageCorr);
        //  -> create sub-array without recorded sound through device
        ArrayList<Double> partAverageCorr = new ArrayList<Double>(averageCorr.subList(maxValueOfCorr+skipOver,averageCorr.size()));
        // get ordinary maximum in sublist
        //int maxValueInSubList = getIndexOfAbsoluteMaximum(averageCorr,maxValueOfCorr+skipOver,averageCorr.size())-maxValueOfCorr;
        // find max in sub-list through windowing
        int maxValueInSubList = getIndexOfAbsMaxWindowed(partAverageCorr);
        // calculate distance in samples
        double distanceInSamples = maxValueInSubList + skipOver;
        // calculate distance in metres
        //  -> calculate distance
        double distance = distanceInSamples/48000*speedOfSound/2;
        // write down calculated values
        Log.e("first MAX : ", String.valueOf(maxValueOfCorr));
        Log.e("second MAX : ", String.valueOf(maxValueInSubList));
        Log.e("distance in Samples : ", String.valueOf(distanceInSamples));
        Log.e("distance : ", String.valueOf(distance));
        //add to return structure
        distanceInfo.add((double)maxValueOfCorr);
        distanceInfo.add((double)maxValueInSubList);
        distanceInfo.add(distanceInSamples);
        distanceInfo.add(distance);
        // return calculated distance
        return distanceInfo;
    }
    // ------------------------------------------------------------------------------------------ //
    // Function to find maximum absolute value in array, provided by parameter
    public int getIndexOfAbsoluteMaximum(ArrayList<Double> array){
        int index=0;
        Double currentMax = 0.0;

        for (int i=0; i<array.size();i++){
            if(Math.abs(array.get(i)) > currentMax){
                currentMax = Math.abs(array.get(i));
                index = i;
            }
        }
        // return index of first absolute maximum find in array
        return index;
    }
    // ------------------------------------------------------------------------------------------ //
    // Function to find maximum absolute value in part of array, provided by parameter
    public int getIndexOfAbsoluteMaximum(ArrayList<Double> array,int start, int stop){
        int index=0;
        Double currentMax = 0.0;

        for (int i=start; i<stop;i++){
            if(Math.abs(array.get(i)) > currentMax){
                currentMax = Math.abs(array.get(i));
                index = i;
            }
        }
        // return index of first absolute maximum find in array
        return index;
    }
    // ------------------------------------------------------------------------------------------ //
    // Function to calculate absolute sum of part of array, provided by parameter.
    public Double getAbsoluteSum(ArrayList<Double> array,int start, int stop){
        Double sum = 0.0;
        for(int i=start;i<stop;i++){
            sum += Math.abs(array.get(i));
        }
        return sum;
    }
    // ------------------------------------------------------------------------------------------ //
    // Function to calculate absolute maximum of array provided through parameter. Maximum is located
    // through application of window of size 50 and step 10.
    public int getIndexOfAbsMaxWindowed(ArrayList<Double> array){
        // set parameters for windowing
        int windowSize = 50;
        int step = 10;
        // create variable for output of function
        int index = 0;
        int maxStart = 0;
        int maxStop = 0;
        Double currentMax = 0.0;
        // set start and stop index of window
        int start = 0;
        int stop = start + windowSize;

        // go through array
        while (true){
            // calculate absolute maximum of current window
            if (getAbsoluteSum(array,start,stop)>currentMax){
                maxStart = start;
                maxStop = stop;
                currentMax = getAbsoluteSum(array,start,stop);
            }
            // calculate next values
            start +=step;
            stop = start + windowSize;
            if(stop>=array.size()){
                break;
            }
        }
        // Biggest window found. Get index of max value in that window.
        index = getIndexOfAbsoluteMaximum(array,maxStart,maxStop);

        return index;
    }
    // ------------------------------------------------------------------------------------------ //
    // Preprocess before sound is recorded ---------------------------------------------------------
    public void preprocess(){
        // load original signals
        originalSignals = loadOrigSignals(parent.originalFileName);
        if (enableWriteToFiles) {
            for (int i = 0; i < originalSignals.size(); i++) {
                saveSignal("origSig" + String.valueOf(i), getOrdinaryShortArray(originalSignals.get(i)));
            }
        }
        preprocessFinished = true;
    }
    // ------------------------------------------------------------------------------------------ //
    // process signal -> type 1 --------------------------------------------------------------------
    //  -> switching signal and silent
    public void postProcessingType1(){
        // wait until recording is finished
        synchronized (parent.lock) {
            while (!parent.notifyPostProcess) {
                try {
                    parent.lock.wait();
                } catch (Exception e) {
                    Log.e("Waiting for notifyPlaying!", e.getMessage());
                }
            }
        }
        // get recorded signal from parent
        recordedSound = parent.recordedSoundShorts;
        // separate recorded signals
        separateRecSignals();
        // is loading of originals finished
        while(!preprocessFinished){}
        Log.i("Number of signals located: ", String.valueOf(getSeparateRecordedSignals().size()));
        if (isSignalSeparated() && recordedSignals.size() == originalSignals.size()) {
            // save separated signals
            if (enableWriteToFiles) {
                for (int i = 0; i < getSeparateRecordedSignals().size(); i++) {
                    saveSignal("recSig" + String.valueOf(i), getOrdinaryShortArray(getSeparateRecordedSignals().get(i)));
                }
            }
            // if the number of separated signals from recorded signal is different from number of
            // original signals, break with error
            if (recordedSignals.size() == originalSignals.size()){
                // correlation of recorded and original signals
                crossCorrOfAllSignals(originalSignals);
                // save correlations into file
                if (enableWriteToFiles) {
                    for (int i = 0; i < getCorrelationOfSignals().size(); i++) {
                        saveSignal("xcorr" + String.valueOf(i), getOrdinaryDoubleArray(getCorrelationOfSignals().get(i)));
                    }
                }
                // calculate average correlation from all correlations
                calculateAverageCorrelation();
                if (enableWriteToFiles) {
                    saveSignal("xcorrMain", getOrdinaryDoubleArray(getAverageCorr()));
                }
                // calculate distance of object
                distance = calculateDistance();
                distanceValue = distance.get(3);
            } else {
                Log.e("Different number of recorded and original signals!", String.valueOf(recordedSignals.size()) + " x " + String.valueOf(originalSignals.size()));
            }

            // save result to file
            if (enableWriteToFiles) {
                saveSignal("distance", getOrdinaryDoubleArray(distance));
            }
        }
        // handler
        message.obj = distanceValue;
        parent.handler.sendMessage(message);
    }
    // ------------------------------------------------------------------------------------------ //
    // process signal -> type 2 --------------------------------------------------------------------
    //  -> continuous signal
    public void postProcessingType2(){
        // wait until recording is finished
        synchronized (parent.lock) {
            while (!parent.notifyPostProcess) {
                try {
                    parent.lock.wait();
                } catch (Exception e) {
                    Log.e("Waiting for notifyPlaying!", e.getMessage());
                }
            }
        }
        // get recorded signal from parent
        recordedSound = parent.recordedSoundShorts;
        // copy recorded signal into recordedSignals array
        recordedSignals = new ArrayList<ArrayList<Short>>();
        ArrayList<Short> temp = new ArrayList<Short>();
        for (Short item : recordedSound){
            temp.add(item);
        }
        recordedSignals.add(temp);
        // save separated signals
        if (enableWriteToFiles) {
            for (int i = 0; i < getSeparateRecordedSignals().size(); i++) {
                saveSignal("recSig" + String.valueOf(i), getOrdinaryShortArray(getSeparateRecordedSignals().get(i)));
            }
        }
        // create correlation of all signals -> only one in this case
        crossCorrOfAllSignals(originalSignals);
        // save correlations into file
        if (enableWriteToFiles) {
            for (int i = 0; i < getCorrelationOfSignals().size(); i++) {
                saveSignal("xcorr" + String.valueOf(i), getOrdinaryDoubleArray(getCorrelationOfSignals().get(i)));
            }
        }
        // calculate average of all signals -> only one in this case
        calculateAverageCorrelation();
        if (enableWriteToFiles) {
            saveSignal("xcorrMain", getOrdinaryDoubleArray(getAverageCorr()));
        }
        // calculate distance of object
        distance = calculateDistance();
        // save result to file
        if (enableWriteToFiles) {
            saveSignal("distance", getOrdinaryDoubleArray(distance));
        }
        // handler
        message.obj = distance.get(3);
        parent.handler.sendMessage(message);

    }

}
