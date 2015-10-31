package com.example.mtoebes.cameraopencv;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.Mat;

import java.io.File;
import android.support.v7.widget.Toolbar;

/*
 * CameraActivity opens the back camera and displays the view to the screen.
 * By calling takePhoto(), the current view is saved to a jpg file in mat format.
 */
public class CameraActivity extends Activity implements CvCameraViewListener2, AdapterView.OnItemSelectedListener {
    private static final String TAG = "CameraActivity";
    private CameraBridgeViewBase mOpenCvCameraView;

    private Mat mRgba, mRes; // Mat to hold current camera frame in
    private FilteredMat mFilteredMat;

    // Called as part of the activity lifecycle when this activity is starting.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        Spinner spinner = (Spinner) findViewById(R.id.filter_spinner);
        spinner.setOnItemSelectedListener(this);

        mFilteredMat = new FilteredMat(getApplicationContext());

        mOpenCvCameraView = (CameraBridgeViewBase)findViewById(R.id.surface_view);
        mOpenCvCameraView.setCvCameraViewListener(this);
    }

    // Called as part of the activity lifecycle when this activity is no longer visible.
    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    // Called as part of the activity lifecycle when this activity becomes visible.
    @Override
    public void onResume() {
        super.onResume();
        mOpenCvCameraView.enableView();
    }

    // onClick event that is called when the button with id "take_photo_button" is pressed
    public void takePhoto(View view) {
        File imgFile = PhotoHelper.saveMat(mRgba);
        Intent intent = new Intent(this.getBaseContext(), ViewActivity.class);
        intent.putExtra(ViewActivity.EXTRA_FILE_PATH, imgFile.getPath());
        startActivity(intent);
    }

    // This method is invoked when camera preview has started.
    @Override
    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat();
    }

    // This method is invoked when camera preview has been stopped for some reason.
    @Override
    public void onCameraViewStopped() { }

    // This method is invoked when delivery of the frame needs to be done.
    @Override
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        mRes = mFilteredMat.update(mRgba);
        return mRes;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String tag = (String) ((TextView) view).getText();
        Log.v(TAG, "tag" + tag);
        mFilteredMat.setTag(tag);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void openPrefs(View view) {
        Intent intent = new Intent(this, FilterPreferenceActivity.class);
        startActivity(intent);
    }
}
