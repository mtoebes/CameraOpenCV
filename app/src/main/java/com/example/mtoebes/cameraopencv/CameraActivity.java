package com.example.mtoebes.cameraopencv;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.Mat;

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

        mFilteredMat = new FilteredMat(getApplicationContext());
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);

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
        PhotoHelper.saveMat(mRgba);
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
}
