package ece459.medstatapp;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;


public class MainActivity extends Activity {
    TextView statusView;
    TextView counterView;
    TextView heartRateView;
    TextView moistureView;
    TextView alertView;
    TextView writeView;
    LinearLayout myLayout;

    private static Handler mHandler;
    private EmergencyContactList myList;

    BleService myService;
    private static final UUID heartCharx = UUID.fromString("932a5eac-e2e2-4968-8ec0-92cac3c9f72b");
    private static final UUID moistCharx = UUID.fromString("df342b03-5df9-43b4-acb6-62a63ca0615a");
    private static final UUID ledCharx =   UUID.fromString("DF342B03-53f9-43B4-ACB6-62A63CA0615A");

    private static final UUID hhCharx = UUID.fromString("df342b43-53f9-43b4-acb6-62a63ca0615a");
    private static final UUID lhCharx = UUID.fromString("df342b03-53f9-43b4-adb6-62a63ca0615a");
    private static final UUID hmCharx = UUID.fromString("df342b03-53fa-43b4-acb6-62a63ca0615a");
    private static final UUID lmCharx = UUID.fromString("df342b03-5df9-43b4-acb6-62a63ca0615a");

    private static final UUID emCharx = UUID.fromString("df342b03-5df9-43b4-acb6-62a63ca0615a");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


            myLayout = (LinearLayout) findViewById(R.id.myLayout);
            statusView = (TextView) findViewById(R.id.statusView);
            counterView = new TextView(this);
            counterView.setText("Hello Counter!");
            myLayout.addView(counterView);

            myList = new EmergencyContactList(this);

            heartRateView = new TextView(this);
            heartRateView.setText("HeartRate --");
            myLayout.addView(heartRateView);

            moistureView = new TextView(this);
            moistureView.setText("Moisture --");
            myLayout.addView(moistureView);

            alertView = new TextView(this);
            alertView.setText("Alert --");
            myLayout.addView(alertView);

            writeView = new TextView(this);
            writeView.setText("Write --");
            myLayout.addView(writeView);

            mHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    try {
                        switch (msg.arg1) {
                            case 0:
                                throwPopup("No device found");
                                break;
                            case 1:
                                appendStatus("Device found:- " + msg.obj.toString());
                                myService.connectMe();
                                break;
                            case 2:
                                appendStatus("Fetching services");
                                myService.startChar();
                                break;
                            case 3:
                                appendStatus("Fetching characteristics");
                                //myService.printTable();
                                myService.setCharacteristic();
                                break;
                            case 4:
                                appendStatus("disconnecting");
                                myService.disconnectMe();

                            case 19:
                                appendStatus("Handler: " + msg.arg1);
                                break;
                            case 13:
                                appendStatus("Handler " + msg.arg1);
                                BluetoothGattCharacteristic bleChar = (BluetoothGattCharacteristic) msg.obj;
                                byte[] bytes = bleChar.getValue();
                                //int value = ByteBuffer.wrap(bytes).getInt();
                                int value = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getInt();

                                //If else nest here for more UUIDs.
                                if (bleChar.getUuid().equals(ledCharx)) {
                                    writeView.setText("Write LED = " + value);
                                }

                                break;
                            case 14:
                                //updateStatus("Handler " + msg.arg1);
                                bleChar = (BluetoothGattCharacteristic) msg.obj;
                                bytes = bleChar.getValue();
                                //int value = ByteBuffer.wrap(bytes).getInt();
                                value = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getInt();

                                //If else nest here for more UUIDs.
                                if (bleChar.getUuid().equals(heartCharx)) {
                                    heartRateView.setText("HeartRate: " + value);
                                } else if (bleChar.getUuid().equals(moistCharx)) {
                                    moistureView.setText("Moisture: " + value);
                                } else if (bleChar.getUuid().equals(emCharx)) {
                                    if (value == 1) {
                                        try {
                                            int contactIndex = 0;
                                            myList.sendAlert("Alert Alert Alert! ", contactIndex);
                                            alertView.setText("Sent Alerts");
                                        } catch (Exception e) {
                                            appendStatus("Failed to send Text \n" + e.getMessage());
                                        }
                                    }
                                    if (value == 0) {
                                        alertView.setText("No Alerts!");
                                    }
                                }
                                break;
                            default:
                                break;
                        }

                    } catch (Exception e) {
                        appendStatus("Handler Exception " + e.getMessage());
                    }

                }
            };

            try {
                myService = new BleService(this, mHandler);
            } catch (Exception e) {
                throwPopup("ServiceStartupError: " + e.getMessage());
            }

            /*
            Button ledOn = new Button(this);
            ledOn.setText("LED ON");
            ledOn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    myService.writeMChar(ledCharx, 1);
                    myService.readCharVal(ledCharx);
                }
            });
            myLayout.addView(ledOn);

            Button ledOff = new Button(this);
            ledOff.setText("LED OFF");
            ledOff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    myService.writeMChar(ledCharx, 0);
                    myService.readCharVal(ledCharx);
                }
            });
            myLayout.addView(ledOff);
            */

            final EditText writeVal = new EditText(this);
            myLayout.addView(writeVal);

            Button setHeartLow = new Button(this);
            setHeartLow.setText("HL");
            setHeartLow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        int value = Integer.parseInt(writeVal.getText().toString());
                        writeVal.setText("");
                        myService.writeMChar(lhCharx, value);
                        myService.readCharVal(lhCharx);
                    } catch (Exception e){
                        throwPopup("Write Error: "+e.getMessage());
                    }
                }
            });

            Button setHeartHigh = new Button(this);
            setHeartHigh.setText("HH");
            setHeartHigh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        int value = Integer.parseInt(writeVal.getText().toString());
                        writeVal.setText("");
                        myService.writeMChar(hhCharx, value);
                        myService.readCharVal(hhCharx);
                    } catch (Exception e){
                        throwPopup("Write Error: "+e.getMessage());
                    }
                }
            });

            Button setMoistLow = new Button(this);
            setMoistLow.setText("ML");
            setMoistLow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        int value = Integer.parseInt(writeVal.getText().toString());
                        writeVal.setText("");
                        myService.writeMChar(lmCharx, value);
                        myService.readCharVal(lmCharx);
                    } catch (Exception e){
                        throwPopup("Write Error: "+e.getMessage());
                    }
                }
            });

            Button setMoistHigh = new Button(this);
            setMoistHigh.setText("MH");
            setMoistHigh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        int value = Integer.parseInt(writeVal.getText().toString());
                        writeVal.setText("");
                        myService.writeMChar(hmCharx, value);
                        myService.readCharVal(hmCharx);
                    } catch (Exception e){
                        throwPopup("Write Error: "+e.getMessage());
                    }
                }
            });

            LinearLayout writersLayout = new LinearLayout(this);
            writersLayout.setOrientation(LinearLayout.HORIZONTAL);
            writersLayout.addView(setHeartLow);
            writersLayout.addView(setHeartHigh);
            writersLayout.addView(setMoistLow);
            writersLayout.addView(setMoistHigh);
            myLayout.addView(writersLayout);

            Button editContacts = new Button(this);
            editContacts.setText("Edit Emergency List");
            myLayout.addView(editContacts);
            editContacts.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //launchContactEditor();
                    myService.printTable();
                }
            });

            //Launcher Buttons
            /*
        //GraphActivity Launcher
        Button launchGraph = new Button(this);
        launchGraph.setText("Launch Graph");
        myLayout.addView(launchGraph);
        launchGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchGraphingActivity();
            }
        });

        /*

        //SimulatorActivity Launcher
        Button launchSimulate = new Button(this);
        launchSimulate.setText("Launch Log Simulator");
        myLayout.addView(launchSimulate);
        launchSimulate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchLogSimulator();
            }
        });

        //ContactEditor Launcher
        Button editContacts = new Button(this);
        editContacts.setText("Edit Emergency List");
        myLayout.addView(editContacts);
        editContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchContactEditor();
            }
        });
        */

        } catch (Exception e) {
            throwPopup("MainError: \n" +  e.getMessage());
        }

    }

    private void appendStatus(String update){
        counterView.setText(update + "\n" + counterView.getText().toString());
    }
    private void updateStatus(String update){
        counterView.setText(update);
    }


    private void throwPopup(String msg){
        ErrorPopup popup = new ErrorPopup(this, msg);
    }


    private void launchGraphingActivity(){
        myService.disconnectMe();
        Intent intent = new Intent(this, GraphActivity.class);
        startActivity(intent);
    }

    private void launchLogSimulator() {
        Intent intent = new Intent(this, SimulateLog.class);
        startActivity(intent);
    }

    private void launchContactEditor() {
        Intent intent = new Intent(this, ContactEditor.class);
        startActivity(intent);
    }
}
