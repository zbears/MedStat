package ece459.medstatapp;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Negatu on 11/5/15.
 */
public class ContactEditorEntry extends LinearLayout {
    private int myRank;
    private Contact myContact;

    public ContactEditorEntry(Context context, int rank, Contact contact){
        super(context);
        myRank = rank;
        myContact = contact;
        TextView contactView = new TextView(context);
        contactView.setText(myRank + " " + myContact.getName());
        this.addView(contactView);
    }

}
