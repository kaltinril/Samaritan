package com.kaltinril.android.samaritan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class CreateProfile extends AppCompatActivity {

    public static final String PREFS_NAME = "MyPrefsFile";
    private String username;
    private String email;
    private String profilePicPath;

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
            profilePicPath = settings.getString("profilePic", "");

            loadProfile();
        }else {
            username = "Enter a Name";
            email = "";
            profilePicPath = "";
        }
    }

    private void loadProfile(){
        TextView cpName = (TextView)findViewById(R.id.cp_edit_name);
        TextView cpEmail = (TextView)findViewById(R.id.cp_edit_email);
        ImageView cpProfile = (ImageView)findViewById(R.id.cp_profile_picture);

        cpName.setText(username);
        cpEmail.setText(email);

        if (!profilePicPath.equals("")) {
            Bitmap bmp = loadImageFromStorage(profilePicPath);
            cpProfile.setImageBitmap(bmp);
        }
    }

    private Bitmap loadImageFromStorage(String fileAndPath)
    {
        Bitmap b = null;
        try {
            File f=new File(fileAndPath);
            b = BitmapFactory.decodeStream(new FileInputStream(f));
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        return b;
    }

    public void cancelClick(View view){
        Toast.makeText(getApplicationContext(),
                "Canceled Profile Edit.",
                Toast.LENGTH_SHORT).show();
        // Close this view
        this.finish();
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
        editor.putString("profilePic", profilePicPath);

        // Commit the edits!
        editor.commit();

        Toast.makeText(getApplicationContext(),
                "Profile Saved!",
                Toast.LENGTH_SHORT).show();

        // Close this view
        this.finish();
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    public void takePicture(View view){
        dispatchTakePictureIntent();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            ImageView iv = (ImageView)findViewById(R.id.cp_profile_picture);
            // Get the image on the page and update it
            iv.setImageBitmap(imageBitmap);

            saveProfileThumbnail(imageBitmap);
        }
    }

    // TODO: Add check for permissions and prompt like in MainActivity.java

    // TODO: Change the code here, and above, to save a full size image, not just a thumbnail
    private void saveProfileThumbnail(Bitmap bmp){
        FileOutputStream out = null;
        try {
            // Create a temporary file
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String path = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
            File outFile = new File(path, "ProfilePic_"+timeStamp+".png");

            // Save the path to the image
            profilePicPath = outFile.getAbsolutePath();

            // Compress/Save it
            out = new FileOutputStream(outFile);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
