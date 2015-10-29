package ece459.medstatapp;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


public class MainActivity extends Activity {
    TextView statusView;
    LinearLayout myLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myLayout = (LinearLayout) findViewById(R.id.myLayout);
        statusView = (TextView) findViewById(R.id.statusView);

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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    private void launchGraphingActivity(){
        Intent intent = new Intent(this, GraphActivity.class);
        startActivity(intent);
    }
}
