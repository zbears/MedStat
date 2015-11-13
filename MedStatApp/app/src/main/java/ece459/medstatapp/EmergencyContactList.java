package ece459.medstatapp;

import android.content.Context;
import android.telephony.SmsManager;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Created by Negatu on 11/3/15.
 */
public class EmergencyContactList {

    private ArrayList<Contact> myList;
    private final String fileName = "EmergencyContactList";
    private Context myContext;

    public EmergencyContactList(Context context) {
        myContext = context;
        try {
            FileInputStream fis = myContext.openFileInput(fileName);
            ObjectInputStream is = new ObjectInputStream(fis);
            myList = (ArrayList<Contact>) is.readObject();
        } catch (Exception e) {
            throwError("Please Add Emergency Contacts, you currently have none");
            initContacts();
        }
    }

    public void addContact(String name, String number){
        Contact newContact = new Contact(name, number);
        addContact(newContact);
    }

    public void addContact(Contact contact) {
        myList.add(contact);
        writeListToFile();
    }

    public void deleteContact(int index){
        try {
            myList.remove(index);
            writeListToFile();
        } catch (Exception e){
            throwError("Failed to delete contact \n" +  e.getMessage());
        }
        writeListToFile();
    }

    public void rankUp(int index) {
        if( (index > 0) && (index < myList.size()) ){
            Contact temp = myList.get(index-1);
            myList.set(index-1, myList.get(index));
            myList.set(index, temp);
        }
        writeListToFile();
    }

    public void rankDown(int index) {
        if ( (index >= 0) && (index+1 < myList.size()) ){
            Contact temp = myList.get(index+1);
            myList.set(index+1, myList.get(index));
            myList.set(index, temp);
        }
        writeListToFile();
    }

    public ArrayList<Contact> getEmergencyList(){
        return myList;
    }

    public int getSize(){
        return myList.size();
    }

    private void writeListToFile(){
        try {
            FileOutputStream fos = myContext.openFileOutput(fileName, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(myList);
            os.close();
            fos.close();
        } catch (Exception e) {
            throwError("Failed to write contact to file \n" + e.getMessage());
        }
    }

    private void initContacts(){
        myList = new ArrayList<>();
        try {
            //create emergency list file
            FileOutputStream fos = myContext.openFileOutput(fileName, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(myList);
            os.close();
            fos.close();
        } catch (Exception e) {
            throwError("Failed to create contact file \n" + e.getMessage());
        }
    }

    public void sendAlert(String msg, int index) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            String number = myList.get(index).getNumber();
            smsManager.sendTextMessage(number, null, msg, null, null);
        } catch (Exception e){
            throwError("Faild to send SMS alert \n" +  e.getMessage());
        }
    }

    private void throwError(String errorMessage) {
        ErrorPopup errorPopup = new ErrorPopup(myContext, errorMessage);
    }
}
