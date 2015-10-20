package com.example.mtoebes.cameraopencv;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Activity that is started when the app is first created
 * display 2 buttons, one to start the camera, one to open the gallery
 */
public class MainActivity extends Activity {

    static { System.loadLibrary("opencv_java3"); }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * onClick event that is called when the button with id "camera_button" is pressed
     * @param view
     */
    public void openCamera(View view) {
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
    }

    /**
     * onClick event that is called when the button with id "gallery_button" is pressed
     * @param view
     */
    public void openGallery(View view) {
        Intent intent = new Intent(this, GalleryActivity.class);
        startActivity(intent);
    }
}

