package ece459.medstatapp;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


public class SimulateLog extends Activity {
    private LinearLayout myLayout;
    private HealthHistory myHistory;
    private TextView statusView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulate_log);

        statusView = (TextView) findViewById(R.id.statusView);
        myLayout = (LinearLayout) findViewById(R.id.myLayout);
        myHistory = new HealthHistory(this);

        final EditText inputValue = new EditText(this);

        Button logHR = new Button(this);
        logHR.setText("Log HeartRate");
        logHR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    myHistory.logHeartRate(Double.parseDouble(inputValue.getText().toString()));
                    inputValue.setText("");
                } catch (Exception e) {
                    throwError("Invalid Heartrate value: Please log again \n" + e.getMessage());
                }
            }
        });

        Button logMoisture = new Button(this);
        logMoisture.setText("Log Moisture");
        logMoisture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    myHistory.logMoisture(Double.parseDouble(inputValue.getText().toString()));
                    inputValue.setText("");
                } catch (Exception e) {
                    throwError("Invalid moisture value: Please log again \n" + e.getMessage());
                }
            }
        });


        Button updateHeartThresh = new Button(this);
        updateHeartThresh.setText("Update HeartRate threshold");
        updateHeartThresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    myHistory.updateHeartRateThreshold(Double.parseDouble(inputValue.getText().toString()));
                    inputValue.setText("");
                    statusView.setText(myHistory.gethThresh().toString());
                } catch (Exception e) {
                    throwError("Invalid threshold value \n" + e.getMessage());
                }
            }
        });

        Button updateMoistureThresh = new Button(this);
        updateMoistureThresh.setText("Update Moisture threshold");
        updateMoistureThresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    myHistory.updateMoistureThreshold(Double.parseDouble(inputValue.getText().toString()));
                    inputValue.setText("");
                    statusView.setText(myHistory.getmThresh().toString());
                } catch (Exception e){
                    throwError("Invalid threshold value \n");
                }
            }
        });


        myLayout.addView(inputValue);
        myLayout.addView(logHR);
        myLayout.addView(logMoisture);
        myLayout.addView(updateHeartThresh);
        myLayout.addView(updateMoistureThresh);


    }

    private void throwError(String errorMessage) {
        ErrorPopup errorPopup = new ErrorPopup(this, errorMessage);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_simulate_log, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
