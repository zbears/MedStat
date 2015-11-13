package ece459.medstatapp;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

import java.util.ArrayList;
import java.util.Random;


public class GraphActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        /*
        GraphView.GraphViewData[] dataSeries = new GraphView.GraphViewData[50];
        Random random = new Random();
        for (int i=0; i<50; i++){
            int val = random.nextInt(70);
            dataSeries[i] = new GraphView.GraphViewData((System.currentTimeMillis() + i)%1000, val);
        }
        */


        HealthHistory history = new HealthHistory(this);
        ArrayList<LogElement> heartRateList = history.getHeartRates();
        GraphView.GraphViewData[] hDataSeries = new GraphView.GraphViewData[heartRateList.size()];
        for (int i=0; i<heartRateList.size(); i++){
            LogElement iLog = heartRateList.get(i);
            hDataSeries[i] = new GraphView.GraphViewData(iLog.getTimeStamp(), iLog.getLogValue());
        }
        GraphViewSeries heartRateSeries = new GraphViewSeries(hDataSeries);
        GraphView heartRateView = new LineGraphView(this, "Heart Rate");

        heartRateView.addSeries(heartRateSeries); // data
        LinearLayout graph1Layout = (LinearLayout) findViewById(R.id.graph1); // graph
        graph1Layout.addView(heartRateView);

        ArrayList<LogElement> moistureList = history.getMoistures();
        GraphView.GraphViewData[] mDataSeries = new GraphView.GraphViewData[moistureList.size()];
        for (int i=0; i<moistureList.size(); i++) {
            LogElement iLog = moistureList.get(i);
            mDataSeries[i] = new GraphView.GraphViewData(iLog.getTimeStamp(), iLog.getLogValue());
        }
        GraphViewSeries moistureSeries = new GraphViewSeries(mDataSeries);
        GraphView moistureView = new LineGraphView(this, "Moisture");

        moistureView.addSeries(moistureSeries);
        LinearLayout graph2Layout = (LinearLayout) findViewById(R.id.graph2);
        graph2Layout.addView(moistureView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_graph, menu);
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

    private void throwError(String errorMessage) {
        ErrorPopup errorPopup = new ErrorPopup(this, errorMessage);
    }
}
