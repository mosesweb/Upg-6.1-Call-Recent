package com.example.studerande.upg61;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    final static int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    final static int MY_PERMISSIONS_REQUEST_READ_LOG = 1;

    public int allowedtoreadcontacts = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView contact_listView = (ListView) findViewById(R.id.contact_listView);
        showContacts();
    }
    private void showContacts() {
        /*  edited version of this..
        *   http://stackoverflow.com/questions/30293479/permission-denial-when-trying-to-access-contacts-in-android
        *
        * */

        // Check the SDK version and whether the permission is already granted or not.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_CALL_LOG}, MY_PERMISSIONS_REQUEST_READ_LOG);
                //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
            } else {
                // Android version is lesser than 6.0 or the permission is already granted.
                List<String> contacts = getCallLogItems();
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, contacts);

                ListView contact_listView = (ListView) findViewById(R.id.contact_listView);
                contact_listView.setAdapter(adapter);
            }
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, String[] permissions,
        int[] grantResults) {
            if (requestCode == MY_PERMISSIONS_REQUEST_READ_LOG) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted
                    showContacts();
                } else {
                    Toast.makeText(this, "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
                }
            }
        }

        /**
         * Read the name of all the call log .
         *
         * @return a list of names.
         */
        private List<String> getCallLogItems() {
            List<String> calllog_item = new ArrayList<>();
            // Get the ContentResolver
            ContentResolver cr = getContentResolver();
            // Get the Cursor of all the contacts
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

                if (checkSelfPermission(Manifest.permission.READ_CALL_LOG)
                        == PackageManager.PERMISSION_DENIED) {

                    Log.d("permission", "permission denied to READ CALL LOG - requesting it");
                    String[] permissions = {Manifest.permission.READ_CALL_LOG};

                    requestPermissions(permissions, MY_PERMISSIONS_REQUEST_READ_LOG);

                } else {
                    Cursor cursor = cr.query(CallLog.Calls.CONTENT_URI, null, null, null, null); //cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);


                    // Move the cursor to first. Also check whether the cursor is empty or not.
                    if (cursor.moveToFirst()) {
                        // Iterate through the cursor
                        do {
                            // Get the call log number
                            // @TODO fix bug were an empty row exist
                            String item = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
                            item += " -- " + cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME ));

                            calllog_item.add(item);

                        } while (cursor.moveToNext());
                    }
                    // Close the curosor
                    cursor.close();

                    return calllog_item;
                }
            }
            return calllog_item;
        }
    }
