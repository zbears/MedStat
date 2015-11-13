package ece459.medstatapp;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Created by Negatu on 11/5/15.
 */
public class Thresholds {

    private Double heartRateThresh;
    private Double moistureThresh;
    private final String heartThreshFile = "heartThreshFile";
    private final String moistureThreshFile = "moistureThreshFile";
    private Double defaultHeartThresh = 70.0;
    private Double defaultMoistureThresh = 3.0;
    private Context myContext;

    public Thresholds(Context context){
        myContext = context;
        try {
            //load heart rate threshold
            FileInputStream fis = myContext.openFileInput(heartThreshFile);
            ObjectInputStream is = new ObjectInputStream(fis);
            heartRateThresh = (Double) is.readObject();
            //load moisture threshold
            fis = myContext.openFileInput(moistureThreshFile);
            is = new ObjectInputStream(fis);
            moistureThresh = (Double) is.readObject();
        } catch (Exception e) {
            throwError("Initiating Thresholds");
            initThresholds();
        }
    }

    private void initThresholds(){
        try {
            //create heartrate threshold file
            FileOutputStream fos = myContext.openFileOutput(heartThreshFile, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(defaultHeartThresh);
            os.close();
            fos.close();

            //create moisture threshold file
            fos = myContext.openFileOutput(moistureThreshFile, Context.MODE_PRIVATE);
            os = new ObjectOutputStream(fos);
            os.writeObject(defaultMoistureThresh);
            os.close();
            fos.close();
        } catch (Exception e) {
            throwError("Failed to create threshold files \n" + e.getMessage());
        }
    }

    public Double getHeartRateThreshold(){
        return heartRateThresh;
    }

    public Double getMoistureThreshold(){
        return moistureThresh;
    }

    public void updateHeartRateThreshold(Double threshold){
        heartRateThresh = threshold;
        writeThreshold(threshold, heartThreshFile);
    }

    public void updateMoistureThreshold(Double threshold) {
        moistureThresh = threshold;
        writeThreshold(threshold, moistureThreshFile);
    }

    private void writeThreshold(Double thresh, String file){
        try {
            FileOutputStream fos = myContext.openFileOutput(file, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(thresh);
            os.close();
            fos.close();
        } catch (Exception e) {
            throwError("Failed to write threshold to file \n" + e.getMessage());
        }
    }

    private void throwError(String errorMessage) {
        ErrorPopup errorPopup = new ErrorPopup(myContext, errorMessage);
    }

}
