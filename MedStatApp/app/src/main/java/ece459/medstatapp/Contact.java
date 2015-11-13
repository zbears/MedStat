package ece459.medstatapp;

import java.io.Serializable;

/**
 * Created by Negatu on 11/3/15.
 */
public class Contact implements Serializable {
    private String myName;
    private String myNumber;

    public Contact(String name, String number){
        myName = name;
        myNumber = number;
    }

    public String getName() {
        return myName;
    }
    public String getNumber() {
        return myNumber;
    }

    @Override
    public String toString(){
        return myName;
    }


}
