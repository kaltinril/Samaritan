package com.kaltinril.android.samaritan;

import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ManageFriends extends AppCompatActivity {

    public static final String PREFS_NAME = "MyPrefsFile";
    private Set<String> friendsSet;

    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    ArrayList<String> listItems=new ArrayList<String>();

    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_friends);

        adapter=new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                listItems);

        ListView lv = (ListView)findViewById(R.id.friendsList);
        lv.setAdapter(adapter);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        friendsSet = new HashSet<String>();

        loadFriends();
        populateListview();
    }

    private void loadFriends(){
        // Save the new friend into the preferences so we don't lose it.
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        boolean profileCreated = settings.getBoolean("profileCreated", false);

        if (profileCreated) {
            friendsSet = settings.getStringSet("friends", null);
        }
    }

    public void populateListview() {
        listItems.clear();

        for(String friend : friendsSet){
            listItems.add(friend);
        }
        adapter.notifyDataSetChanged();
    }
}
