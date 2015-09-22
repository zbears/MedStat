package main_package.android_ble;

import android.content.Context;
import android.widget.Button;

/**
 * Created by Negatu on 9/22/15.
 */
public class DeviceEntry extends Button {
    private int myIndex;
    private Context myContext;

    public DeviceEntry(Context context){
        super(context);
        myContext = context;
    }

    public void setIndex(int index){
        myIndex = index;
    }

    public int getIndex(){
        return myIndex;
    }

    public Context getMyContext(){
        return myContext;
    }
}
