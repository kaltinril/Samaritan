package com.kaltinril.android.samaritan;

import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class CreateProfile extends AppCompatActivity {

    public static final String PREFS_NAME = "MyPrefsFile";
    private String username;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        loadBasicPreferences();
    }

    private void loadBasicPreferences(){
        // Restore preferences
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        boolean profileCreated = settings.getBoolean("profileCreated", false);
        if (profileCreated){
            username = settings.getString("name", "");
            email = settings.getString("email", "");

            loadProfile();
        }else {
            username = "Enter a Name";
            email = "";
        }
    }

    private void loadProfile(){
        TextView cpName = (TextView)findViewById(R.id.cp_edit_name);
        TextView cpEmail = (TextView)findViewById(R.id.cp_edit_email);
        ImageView cpProfile = (ImageView)findViewById(R.id.cp_profile_picture);

        cpName.setText(username);
        cpEmail.setText(email);
        //TODO: Set the image

    }

    // Save the values
    public void doneClick(View view){

        // get the name, email and picture
        EditText etName = (EditText)findViewById(R.id.cp_edit_name);
        EditText etEmail = (EditText)findViewById(R.id.cp_edit_email);

        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("profileCreated", true);
        editor.putString("name", etName.getText().toString());
        editor.putString("email", etEmail.getText().toString());

        // Commit the edits!
        editor.commit();
    }
}
