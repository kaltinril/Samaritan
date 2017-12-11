package com.kaltinril.android.samaritan;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Dialog imageDialog;

    public static final String PREFS_NAME = "MyPrefsFile";
    private boolean profileCreated;
    private String username;
    private String email;
    private String profilePicPath;
    private Set<String> friendsSet;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_FILE = 100;
    String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        friendsSet = new HashSet<String>();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Opening Camera...", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
                requestWritePermissions();
                //dispatchTakePictureIntent();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //drawer.setDrawerListener(toggle); // This was deprecated
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Load any of the basic preferrences
        loadBasicPreferences();

        // if the user has not yet created a profile, create one
        if (!profileCreated) {
            Intent intent = new Intent(this, CreateProfile.class);
            startActivity(intent);
        }

    }

    private void loadNavHeader(){
        TextView tvName = (TextView)findViewById(R.id.nav_name);
        TextView tvEmail = (TextView)findViewById(R.id.nav_email);
        ImageView ivProfile = (ImageView)findViewById(R.id.nav_image);

        // It seems like the onCreateOptionsMenu doesn't get called until
        // it is opened for the first time, prevent errors if it is not present
        if (tvName != null) {
            tvName.setText(username);
            tvEmail.setText(email);
            // Set the image based on the image the user took previously
            if (!profilePicPath.equals("")) {
                Bitmap bmp = loadImageFromStorage(profilePicPath);
                ivProfile.setImageBitmap(bmp);
            }
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

    @Override
    public void onResume(){
        super.onResume();
        loadBasicPreferences();
        loadNavHeader();
    }

    public void navEditProfile(View view){
        // Close the drawer when we click on the image to open the profile editor
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        Intent intent = new Intent(this, CreateProfile.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // This method is called when the app opens
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        // Put the users name there
        if (profileCreated)
            loadNavHeader();

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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_camera) {
            requestWritePermissions();
        } else if (id == R.id.nav_add_friend) {
            showAddFriendDialog();
        } else if (id == R.id.nav_search) {

        } else if (id == R.id.nav_help) {
            Intent intent = new Intent(this, Help.class);
            startActivity(intent);
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_profile) {
            Intent intent = new Intent(this, CreateProfile.class);
            startActivity(intent);
        } else if (id == R.id.nav_view_friends){

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /* /////////////////////////////////////
        User Button code clicks
    */ //////////////////////////////////////

    public void cancelClick(View view){
        Toast.makeText(getApplicationContext(),
                "Canceled Proclamation.",
                Toast.LENGTH_SHORT).show();

        imageDialog.cancel();
        if (imageDialog != null)
            imageDialog = null;
    }

    public void proclaimClick(View view){
        // Testing to see what happens if we open another Activity ontop of this one.
        Intent intent = new Intent(this, CreateProfile.class);
        startActivity(intent);
    }

    public void updatePictureClick(View view){
        requestWritePermissions();
    }

    public void cancelAddFriendClick(View view){
        Toast.makeText(getApplicationContext(),
                "Canceled add.",
                Toast.LENGTH_SHORT).show();

        imageDialog.cancel();
        if (imageDialog != null)
            imageDialog = null;
    }

    public void addFriendClick(View view){
        if (friendsSet == null){
            friendsSet = new HashSet<String>();
        }

        // Grab the email address from the dialog
        TextView tv = (TextView)imageDialog.findViewById(R.id.af_email);
        String newEmail = tv.getText().toString();

        // Check if there was an email, if not, don't add it
        // TODO: basic email validation?
        if (!newEmail.isEmpty()) {
            friendsSet.add(newEmail.toLowerCase());

            // Save the new friend into the preferences so we don't lose it.
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            profileCreated = settings.getBoolean("profileCreated", false);

            if (profileCreated) {
                SharedPreferences.Editor editor = settings.edit();

                if (friendsSet != null) {
                    editor.putStringSet("friends", friendsSet);
                    editor.apply();
                    Toast.makeText(getApplicationContext(),
                            "Friend added!",
                            Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(getApplicationContext(),
                        "Failed to add friend, create your profile first.",
                        Toast.LENGTH_LONG).show();
            }
        }

        imageDialog.cancel();
        if (imageDialog != null)
            imageDialog = null;
    }

    private void loadBasicPreferences(){
        // Restore preferences
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        profileCreated = settings.getBoolean("profileCreated", false);

        if (profileCreated){
            username = settings.getString("name", "");
            email = settings.getString("email", "");
            profilePicPath = settings.getString("profilePic", "");
            friendsSet = settings.getStringSet("friends", null);
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // TODO: Perhaps we want to allow people to add images from the gallery?
        // Make sure we have a camera on this device
        if (getPackageManager().hasSystemFeature(getPackageManager().FEATURE_CAMERA)) {

            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File
                    Toast.makeText(getApplicationContext(),
                            "Error occurred while creating the file, is drive full?",
                            Toast.LENGTH_LONG).show();
                    System.out.println("ERROR: " + ex.getMessage());
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this,
                            "com.kaltinril.android.fileprovider",
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        } else {
            //display in long period of time
            Toast.makeText(getApplicationContext(), "No camera found, unable to take photos.",
                    Toast.LENGTH_LONG).show();
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir =   getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            galleryAddPic();

            if (imageDialog == null) {
                showDialog();
            }else{
                updateDialog();
            }
        }
    }

    // GEO info:
    // http://android-coding.blogspot.com/2011/10/read-exif-of-jpg-file-using.html
    private String getGeoCords(String file){
        String exif="";
        double lat=0;
        double lng=0;
        try {
            ExifInterface exifInterface = new ExifInterface(file);

            String attrLATITUDE = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
            String attrLATITUDE_REF = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
            String attrLONGITUDE = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
            String attrLONGITUDE_REF = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);

            if((attrLATITUDE !=null)
                    && (attrLATITUDE_REF !=null)
                    && (attrLONGITUDE != null)
                    && (attrLONGITUDE_REF !=null)) {

                if (attrLATITUDE_REF.equals("N")) {
                    lat = convertToDegree(attrLATITUDE);
                } else {
                    lat = 0 - convertToDegree(attrLATITUDE);
                }

                if (attrLONGITUDE_REF.equals("E")) {
                    lng = convertToDegree(attrLONGITUDE);
                } else {
                    lng = 0 - convertToDegree(attrLONGITUDE);
                }

                exif = String.valueOf(lat) + ", " + String.valueOf(lng);
            }

            Toast.makeText(getApplicationContext(),"finished", Toast.LENGTH_LONG).show();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),
                    e.toString(),
                    Toast.LENGTH_LONG).show();
        }

        return exif;
    }


    // Used coded from below to save myself typing
    // here http://android-er.blogspot.com/2010/01/convert-exif-gps-info-to-degree-format.html
    private Float convertToDegree(String stringDMS){
        Float result = null;
        String[] DMS = stringDMS.split(",", 3);

        String[] stringD = DMS[0].split("/", 2);
        Double D0 = new Double(stringD[0]);
        Double D1 = new Double(stringD[1]);
        Double FloatD = D0/D1;

        String[] stringM = DMS[1].split("/", 2);
        Double M0 = new Double(stringM[0]);
        Double M1 = new Double(stringM[1]);
        Double FloatM = M0/M1;

        String[] stringS = DMS[2].split("/", 2);
        Double S0 = new Double(stringS[0]);
        Double S1 = new Double(stringS[1]);
        Double FloatS = S0/S1;

        result = new Float(FloatD + (FloatM/60) + (FloatS/3600));

        return result;


    };

    private void requestWritePermissions(){
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else { // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_FILE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the result of the request.
            }
        } else
        {
            // Permissions are granted, display it
            dispatchTakePictureIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_FILE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    System.out.println("ERROR: Can not use camera because you did not grant it permission");
                    // TODO: Disable the capture button
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    private void setPic(ImageView mImageView) {
        // Get the dimensions of the View
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        if (targetW ==0 || targetH == 0){
            targetH = this.getWindow().getDecorView().getHeight() / 2;
            targetW = this.getWindow().getDecorView().getWidth();
        }

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        // Create the bitmap
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

        // Check if we need to rotate the bitmap
        try {
            // GEt the orientation of the photo
            ExifInterface exif = new ExifInterface(mCurrentPhotoPath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);

            Matrix matrix = new Matrix();
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90){
                bitmap = RotateBitmap(bitmap, 90);
            }
            else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                bitmap = RotateBitmap(bitmap, 180);
            }
            else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                bitmap = RotateBitmap(bitmap, 270);
            }
        }
        catch (Exception e) {
            System.err.println(e.toString());
        }

        // Set the bitmap to the imageview
        mImageView.setImageBitmap(bitmap);
    }

    public static Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    // Takes a bitmap and stores it in the image on a pop-up dialog
    private void showDialog(){
        imageDialog = new Dialog(this);
        imageDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        imageDialog.setContentView(getLayoutInflater().inflate(R.layout.dialog_post_layout, null));

        // Update all the items on the dialog
        updateDialog();

        // Show the dialog
        imageDialog.show();
    }

    private void updateDialog(){
        // Change the image
        ImageView iv = (ImageView)imageDialog.findViewById(R.id.dpl_iv_post_image);
        setPic(iv);

        // Update the geo coordinates
        TextView tv = (TextView)imageDialog.findViewById(R.id.dpl_tx_geo);
        String geoCoords = getGeoCords(mCurrentPhotoPath);
        if (geoCoords.equalsIgnoreCase("")) {
            tv.setText("Image has no GeoLocation data");
        }else {
            tv.setText(geoCoords);
        }
    }

    // Takes a bitmap and stores it in the image on a pop-up dialog
    private void showAddFriendDialog(){
        imageDialog = new Dialog(this);
        imageDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        imageDialog.setContentView(getLayoutInflater().inflate(R.layout.dialog_add_friend_layout, null));

        // Show the dialog
        imageDialog.show();
    }

}
