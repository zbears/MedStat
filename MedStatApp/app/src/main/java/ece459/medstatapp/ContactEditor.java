package ece459.medstatapp;

import android.app.Activity;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;


public class ContactEditor extends Activity {
    private TextView statusView;
    private ListView myListView;
    private LinearLayout lowerLayout;
    private LinearLayout upperLayout;
    private EmergencyContactList myEMBook;

    private Contact selectedContact;
    private Integer selectedRank;
    private ArrayList<Contact> allContacts;
    private Spinner allContactSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_editor);

        statusView = (TextView) findViewById(R.id.statusView);
        myListView = (ListView) findViewById(R.id.myList);
        lowerLayout = (LinearLayout) findViewById(R.id.lowerLayout);
        upperLayout = (LinearLayout) findViewById(R.id.upperLayout);


        myEMBook = new EmergencyContactList(this);
        populateUpperLayout();
        loadListView();


        //Fetch All contacts
        allContacts = new ArrayList<>();
        allContactSpinner = new Spinner(this);
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,null, null);
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            Contact contact = new Contact(name, phoneNumber);
            allContacts.add(contact);
        }
        LinearLayout importContactBox = new LinearLayout(this);
        importContactBox.setOrientation(LinearLayout.HORIZONTAL);
        allContactSpinner = new Spinner(this);
        ArrayAdapter<Contact> spinnerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, allContacts.toArray(new Contact[allContacts.size()]));
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        allContactSpinner.setAdapter(spinnerArrayAdapter);
        Button addButton = new Button(this);
        addButton.setText("ADD NEW");
        importContactBox.addView(addButton);
        importContactBox.addView(allContactSpinner);
        lowerLayout.addView(importContactBox);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Contact newContact = (Contact) allContactSpinner.getSelectedItem();
                    addContactToList(newContact);
                } catch (Exception e) {
                    throwError(e.getMessage());
                }
            }
        });
    }



    private void loadListView() {
        Contact[] myEMContacts = myEMBook.getEmergencyList().toArray(new Contact[myEMBook.getEmergencyList().size()]);
        ArrayAdapter<Contact> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, myEMContacts);
        myListView.setAdapter(adapter);
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    selectedContact = (Contact) myListView.getItemAtPosition(i);
                    selectedRank = i;
                    statusView.setText(selectedRank + ". " + selectedContact.getName());
                } catch (Exception e) {
                    throwError(">> " + e.getMessage());
                }

            }
        });
        if (myEMBook.getSize()>0){
            selectedContact = myEMContacts[0];
            selectedRank = 0;
            statusView.setText(selectedRank + ". " + selectedContact.getName());
        } else {
            statusView.setText("Please add contacts");
        }
    }

    private void populateUpperLayout(){
        Button moveDown = new Button(this);
        moveDown.setText("MoveDown");
        moveDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rankDownContact();
            }
        });
        Button moveUp = new Button(this);
        moveUp.setText("MoveUp");
        moveUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rankUpContact();
            }
        });
        Button delete = new Button(this);
        delete.setText("Delete");
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteContact();
            }
        });
        upperLayout.addView(moveDown);
        upperLayout.addView(moveUp);
        upperLayout.addView(delete);
    }

    private void rankDownContact(){
        myEMBook.rankDown(selectedRank);
        loadListView();
    }

    private void rankUpContact() {
        myEMBook.rankUp(selectedRank);
        loadListView();
    }

    private void deleteContact() {
        if (selectedRank!=null && selectedRank>=0 && selectedRank<myEMBook.getSize()){
            myEMBook.deleteContact(selectedRank);
        }
        loadListView();
    }

    private void addContactToList(Contact contact) {
        myEMBook.addContact(contact);
        loadListView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_contact_editor, menu);
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
