package com.nerdcastle.nazmulhasan.samplefilesystem;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by clive on 2/1/14.
 */
public class DisplayImage extends Activity {

    private ImageView imageView;
    private String fileName = "MyCachedImage.jpg";
    private TextView textView;


    private final int PREFERENCE_MODE_PRIVATE = 0;
    private String MY_BUTTON_PREFERENCES_FILE = "button_prefs_file";
    private SharedPreferences preferenceSettings;
    private SharedPreferences.Editor preferencesEditor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);

        imageView = (ImageView) findViewById(R.id.imageView);
        textView = (TextView) findViewById(R.id.textView);

        Intent intent = getIntent();
        int showWhat = intent.getIntExtra("showWhat", 0);

        switch (showWhat) {
            case 0:
                getFileOutOfCache(fileName);
                break;
            case 1:
                getBitmapFromPublicFile();
                break;
            case 2:
                getInternalTextFileString();
                break;
        }

    }

    /*read an internal text file*/
    private void getInternalTextFileString() {
        try {
            FileInputStream inStream = this.openFileInput("myStringFile");
            InputStreamReader inputStreamReader = new InputStreamReader(inStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuilder finalString = new StringBuilder();
            String oneLine;

            while ((oneLine = bufferedReader.readLine()) != null) {
                finalString.append(oneLine);
            }

            bufferedReader.close();
            inStream.close();
            inputStreamReader.close();

            textView.setVisibility(View.VISIBLE);
            textView.setText(finalString.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*read a cached bitmap file and display the bitmap*/
    private void getFileOutOfCache(String fileName) {
        final String cachePath = this.getCacheDir().getPath();
        File myDiskCacheFilePath = new File(cachePath);
        File myCachedFile = new File(myDiskCacheFilePath
                + File.separator + fileName);
        Bitmap myBitmap = null;
        if (myCachedFile.exists()) {
            myBitmap = BitmapFactory.decodeFile(myCachedFile.toString());
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageBitmap(myBitmap);
        } else {
            Toast.makeText(this, "The cached file does not exist",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void getBitmapFromPublicFile() {
        //NOTE: disc access should be done on background thread
        //check if disk available
        String MEDIA_MOUNTED = "mounted";
        String diskState = Environment.getExternalStorageState();

        if (diskState.equals(MEDIA_MOUNTED)) {
            // path to picture taken by camera
            File pictureFolder = Environment
                    .getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES);
            File filePicture = new File(pictureFolder,
                    "MyCachedImage.jpg");

            Bitmap bitmapToDisplay = null;
            if (filePicture.exists()) {
                // file exists - resize the bitmap
                bitmapToDisplay = BitmapFactory.
                        decodeFile(filePicture.toString());
                // display the appropriate image
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageBitmap(bitmapToDisplay);
            } else {
                Toast.makeText(this, "The public file does not exists",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "The external disk is not mounted",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //get the shared preferences
        preferenceSettings = getSharedPreferences(MY_BUTTON_PREFERENCES_FILE, PREFERENCE_MODE_PRIVATE);
        preferencesEditor = preferenceSettings.edit();
        preferencesEditor.putBoolean("isCache", false);
        preferencesEditor.putBoolean("isPublic", false);
        preferencesEditor.putBoolean("isInternal", false);
        preferencesEditor.commit();
    }

}
