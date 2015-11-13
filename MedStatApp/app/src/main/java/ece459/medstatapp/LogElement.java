package ece459.medstatapp;

import java.io.Serializable;

/**
 * Created by Negatu on 11/2/15.
 */
public class LogElement implements Serializable {
    private Long timeStamp;
    private Double logValue;

    public LogElement(Double val){
        timeStamp = System.currentTimeMillis();
        logValue = val;
    }

    public Long getTimeStamp(){
        return timeStamp;
    }

    public Double getLogValue(){
        return logValue;
    }
}
