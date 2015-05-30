package com.nerdcastle.nazmulhasan.samplefilesystem;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class MainActivity extends Activity implements View.OnClickListener {

    private String fileName = "MyCachedImage.jpg";
    private String stringToSave = "The rain in Spain falls mainly on the plain";

    private final int PREFERENCE_MODE_PRIVATE = 0;
    private String MY_BUTTON_PREFERENCES_FILE = "button_prefs_file";
    private SharedPreferences preferenceSettings;
    private SharedPreferences.Editor preferencesEditor;
    private boolean isCache;
    private boolean isPublic;
    private boolean isInternal;

    private Button buttonShowCache;
    private Button buttonShowPublic;
    private Button buttonShowInternal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get the shared preferences
        preferenceSettings = getSharedPreferences(MY_BUTTON_PREFERENCES_FILE, PREFERENCE_MODE_PRIVATE);
        preferencesEditor = preferenceSettings.edit();

        isCache = preferenceSettings.getBoolean("isCache", false);
        isPublic = preferenceSettings.getBoolean("isPublic", false);
        isInternal = preferenceSettings.getBoolean("isInternal", false);

        //get the save buttons
        Button buttonSaveCache = (Button) findViewById(R.id.button4);
        Button buttonSavePublic = (Button) findViewById(R.id.button5);
        Button buttonSaveInternal = (Button) findViewById(R.id.button6);

        //do on clicks
        buttonSaveCache.setOnClickListener(this);
        buttonSavePublic.setOnClickListener(this);
        buttonSaveInternal.setOnClickListener(this);

        //get the show buttons
        buttonShowCache = (Button) findViewById(R.id.button);
        buttonShowPublic = (Button) findViewById(R.id.button2);
        buttonShowInternal = (Button) findViewById(R.id.button3);

        //check values of preferences
        if (isCache) {
            //show cache button
            buttonShowCache.setVisibility(View.VISIBLE);
            buttonShowCache.setOnClickListener(this);
        }

        if (isPublic) {
            //show public button
            buttonShowPublic.setVisibility(View.VISIBLE);
            buttonShowPublic.setOnClickListener(this);
        }

        if (isInternal) {
            //show internal button
            buttonShowInternal.setVisibility(View.VISIBLE);
            buttonShowInternal.setOnClickListener(this);
        }
    }

    /*on return from backpress in display activity - check the boolean
    values stored in SharedPreferences to see
    if any of the show buttons should be displayed*/
    @Override
    protected void onResume() {
        super.onResume();

        //get the shared preferences
        preferenceSettings = getSharedPreferences(MY_BUTTON_PREFERENCES_FILE, PREFERENCE_MODE_PRIVATE);

        isCache = preferenceSettings.getBoolean("isCache", false);
        isPublic = preferenceSettings.getBoolean("isPublic", false);
        isInternal = preferenceSettings.getBoolean("isInternal", false);

        //check values of preferences
        if (!isCache) {
            //show cache button
            buttonShowCache.setVisibility(View.INVISIBLE);
        }

        if (!isPublic) {
            //show public button
            buttonShowPublic.setVisibility(View.INVISIBLE);
        }

        if (!isInternal) {
            //show internal button
            buttonShowInternal.setVisibility(View.INVISIBLE);
        }
    }

    /*saves a string in internal file - uses File*/
    /*private void saveInInternalFolder(String stringToSave, String aFileName) {
        File textFile = new File(this.getFilesDir(), aFileName);
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(textFile);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(stringToSave);
            bufferedWriter.flush();
            bufferedWriter.close();
            //save preference
            preferencesEditor.putBoolean("isInternal", true).commit();
            //show button & listener
            buttonShowInternal.setVisibility(View.VISIBLE);
            buttonShowInternal.setOnClickListener(this);
        } catch (IOException e) {
            Toast.makeText(this,
                    "There is a problem saving to the internal file",
                    Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }*/

    /*saves a string in internal file - openFileOutput*/
    private void saveInInternalFolder(String aStringToSave,
                                      String aFileName) {
        FileOutputStream fos = null;
        try {
            fos = openFileOutput(aFileName, this.MODE_PRIVATE);
            fos.write(aStringToSave.getBytes());
            fos.close();
            //save preference
            preferencesEditor.putBoolean("isInternal", true).commit();
            //show button & listener
            buttonShowInternal.setVisibility(View.VISIBLE);
            buttonShowInternal.setOnClickListener(this);

        } catch (IOException e) {
            Toast.makeText(this,
                    "There is a problem saving to the internal file",
                    Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    /*gets a bitmap out of drawable folder and saves it in the public directory*/
    private void saveFileInPublicDirectory(String fileName) {
        //NOTE: disc access should be done on background thread
        //get the bitmap drawable
        Bitmap myImage = BitmapFactory.decodeResource(
                getResources(), R.drawable.demo_image);
        //check if disk available
        String MEDIA_MOUNTED = "mounted";
        String diskState = Environment.getExternalStorageState();
        if (diskState.equals(MEDIA_MOUNTED)) {
            //the disk is mounted - we can proceed
            //get the public directory for photos
            File pictureFolder = Environment.
                    getExternalStoragePublicDirectory
                            (Environment.DIRECTORY_PICTURES);
            // overwrites existing file with same name
            File filePicture = new File(pictureFolder, fileName);
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(filePicture);
                myImage.compress(Bitmap.CompressFormat.JPEG, 80, out);
                out.close();
                //save the preferences
                preferencesEditor.putBoolean("isPublic", true).commit();
                //show the button & listener
                buttonShowPublic.setVisibility(View.VISIBLE);
                buttonShowPublic.setOnClickListener(this);
            } catch (IOException e) {
                Toast.makeText(this,
                        "There is a problem saving to the public file",
                        Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

        } else {
            Toast.makeText(this, "The external disk is not mounted",
                    Toast.LENGTH_SHORT).show();
        }

    }

    /*gets a bitmap out of drawables folder and saves
    as internal cache file*/
    private void saveFileInCache(String aFileName) {
        //NOTE: disc access should be done on background thread
        //get the bitmap out drawable folder
        Bitmap myImage = BitmapFactory.decodeResource(
                getResources(), R.drawable.demo_image2);

        final String cachePath = this.getCacheDir().getPath();
        File myDiscCacheFilePath;

        myDiscCacheFilePath = new File(cachePath);
        File myDiscCacheFile = new File(myDiscCacheFilePath
                + File.separator + aFileName);

        try {
            FileOutputStream out = new FileOutputStream(myDiscCacheFile);
            myImage.compress(Bitmap.CompressFormat.JPEG, 80, out);
            out.close();
            //save preference
            preferencesEditor.putBoolean("isCache", true).commit();
            //show button
            buttonShowCache.setVisibility(View.VISIBLE);
            buttonShowCache.setOnClickListener(this);

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Problem saving file in cache",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /*handle all the button clicks*/
    @Override
    public void onClick(View view) {
        Intent intentDisplayImage = new Intent(this, DisplayImage.class);
        switch (view.getId()) {
            //read buttons
            case R.id.button:
                intentDisplayImage.putExtra("showWhat", 0);
                startActivity(intentDisplayImage);
                break;
            case R.id.button2:
                intentDisplayImage.putExtra("showWhat", 1);
                startActivity(intentDisplayImage);
                break;
            case R.id.button3:
                intentDisplayImage.putExtra("showWhat", 2);
                startActivity(intentDisplayImage);
                break;
            //save buttons
            case R.id.button4:
                saveFileInCache(fileName);
                break;
            case R.id.button5:
                saveFileInPublicDirectory(fileName);
                break;
            case R.id.button6:
                saveInInternalFolder(stringToSave, "myStringFile");
                break;
        }
    }
}
