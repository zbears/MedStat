package ece459.medstatapp;

import java.util.UUID;

/**
 * Created by Negatu on 11/13/15.
 */
public class scPair {

    private UUID myService;
    private UUID myCharx;

    public scPair(UUID service, UUID charx){
        myService = service;
        myCharx = charx;
    }

    public UUID getService(){
        return myService;
    }

    public UUID getCharx() {
        return myCharx;
    }
}
