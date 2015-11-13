package ece459.medstatapp;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Negatu on 11/2/15.
 */
public class HealthHistory {

    private Context myContext;
    private final String heartRateLog = "heartRateLog";
    private final String moistureLog = "moistureLog";
    private ArrayList<LogElement> heartRateList;
    private ArrayList<LogElement> moistureList;
    private Thresholds myThresholds;


    public HealthHistory(Context context){
        myContext = context;
        myThresholds = new Thresholds(context);
        try {
            FileInputStream fis = myContext.openFileInput(heartRateLog);
            ObjectInputStream is = new ObjectInputStream(fis);
            heartRateList = (ArrayList<LogElement>) is.readObject();
        } catch (Exception e) {
            throwError("Creating history files!");
            initFiles();
        }
        try {
            //read in heartrate
            FileInputStream fis = myContext.openFileInput(heartRateLog);
            ObjectInputStream is = new ObjectInputStream(fis);
            heartRateList = (ArrayList<LogElement>) is.readObject();
            is.close();
            fis.close();

            //read in moisture
            fis = myContext.openFileInput(moistureLog);
            is = new ObjectInputStream(fis);
            moistureList = (ArrayList<LogElement>) is.readObject();
            is.close();
            fis.close();


        } catch (Exception e){
            throwError("Failed to open HealthHistory \n" + e.getMessage());
        }

    }

    private void initFiles() {
        ArrayList<LogElement> emptyList = new ArrayList<>();
        try {
            //create heartrate log
            FileOutputStream fos = myContext.openFileOutput(heartRateLog, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(emptyList);
            os.close();
            fos.close();

            //create moisture log
            fos = myContext.openFileOutput(moistureLog, Context.MODE_PRIVATE);
            os = new ObjectOutputStream(fos);
            os.writeObject(emptyList);
            os.close();
            fos.close();
        } catch (Exception e) {
            throwError("HealthHistory failed to initiate log files \n" + e.getMessage());
        }
    }

    public Double gethThresh(){
        return myThresholds.getHeartRateThreshold();
    }

    public Double getmThresh(){
        return myThresholds.getMoistureThreshold();
    }

    public void updateHeartRateThreshold(Double threshold) {
        myThresholds.updateHeartRateThreshold(threshold);
    }

    public void updateMoistureThreshold(Double threshold) {
        myThresholds.updateMoistureThreshold(threshold);
    }

    public void logHeartRate(double rate) {
        if(rate > myThresholds.getHeartRateThreshold()){
            EmergencyContactList emList = new EmergencyContactList(myContext);
            emList.sendAlert("Patient Heartrate threshold overflow!! "+rate, 0);
        }
        logReading(rate, heartRateList, heartRateLog);
    }

    public void logMoisture(double reading) {
        if(reading > myThresholds.getMoistureThreshold()){
            EmergencyContactList emList = new EmergencyContactList(myContext);
            emList.sendAlert("Patient Moisture threshold overflow!! "+reading, 0);
        }
        logReading(reading, moistureList, moistureLog);
    }

    public void logReading(double value, ArrayList valueList, String fileName ) {
        LogElement newLog = new LogElement(value);
        valueList.add(newLog);
        try {
            FileOutputStream fos = myContext.openFileOutput(fileName, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(valueList);
            os.close();
            fos.close();
        } catch (Exception e) {
            throwError("HealthHistory failed to log reading \n" + e.getMessage());
        }
    }

    public ArrayList<LogElement> getHeartRates(){
        return heartRateList;
    }

    public ArrayList<LogElement> getMoistures(){
        return moistureList;
    }

    private void throwError(String errorMessage) {
        ErrorPopup errorPopup = new ErrorPopup(myContext, errorMessage);
    }



}
