package com.example.mtoebes.cameraopencv;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.Mat;

// http://www.jayrambhia.com/blog/beginning-android-opencv/
public class CameraActivity extends Activity implements CvCameraViewListener2 {
    private static final String TAG = "OCVSample::Activity";
    private Mat mRgba;
    private CameraBridgeViewBase mOpenCvCameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById ( R.id.surface_view );
        mOpenCvCameraView.setCvCameraViewListener(this);
    }

    public void takePhoto(View view) {
        PhotoHelper.saveMat(mRgba);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        mOpenCvCameraView.enableView();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat();
    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        return mRgba;
    }
}
