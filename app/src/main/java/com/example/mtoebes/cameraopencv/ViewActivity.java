package com.example.mtoebes.cameraopencv;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * ViewActivity uses EXTRA_FILE_PATH to open the Mat sotred at that location
 * inorder to allow the use to perform a variety of image processsing on it
 */
public class ViewActivity extends Activity implements OnItemSelectedListener {
    private static final String TAG = "ViewActivity";

    // extra passed with intent so we know which file to open
    public static final String EXTRA_FILE_PATH = "extraFilePath";

    // List of tags we are expected to handle (should include all from @array/transforms)
    private static final String tag_red = "red";
    private static final String tag_green = "green";
    private static final String tag_blue = "blue";
    private static final String tag_gray = "gray";
    private static final String tag_sobel = "sobel";
    private static final String tag_sobel_x = "sobel_x";
    private static final String tag_sobel_y = "sobel_y";
    private static final String tag_laplacian = "laplacian";
    private static final String tag_gaussian = "gaussian";
    private static final String tag_canny = "canny";
    private static final String tag_hough = "hough";

    private Mat mSrcMat; // Unaltered Mat to use as base
    private File mFile; // File to get mSrcMat from
    private Bitmap mBitmap; // Bitmap use hold Mat's in View friendly form
    private ImageView mImage; // View to display mBitmap
    private Spinner mSpinner;
    private FilteredMat mFilteredMat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        // grab the extra denoting the filepath and use it to create a File
        Bundle extras = getIntent().getExtras();
        String filePath = extras.getString(EXTRA_FILE_PATH);
        mFile = new File(filePath);

        mImage = (ImageView) this.findViewById(R.id.image);

        // get the Mat at mFile, create a bitmap from it, and set it as the display image
        mSrcMat = PhotoHelper.getMat(mFile);
        mBitmap = Bitmap.createBitmap(mSrcMat.cols(), mSrcMat.rows(), Bitmap.Config.ARGB_8888);

        mSpinner = (Spinner) findViewById(R.id.filter_spinner);
        mSpinner.setOnItemSelectedListener(this);

        mFilteredMat = new FilteredMat(getApplicationContext());
        mFilteredMat.update(mSrcMat);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "onResume");
        View view = mSpinner.getSelectedView();
        String tag = "";
        if(view != null) {
            tag = (String) ((TextView) view).getText();
        }
        setImage(mFilteredMat.get(tag));
    }

    /**
     * Sets mImage to display the given mat (by converting it to a bitmap first)
     *
     * @param mat mat to display
     */
    protected void setImage(Mat mat) {
        Utils.matToBitmap(mat, mBitmap);
        mImage.setImageBitmap(mBitmap);
    }

    /**
     * Invoked when an item in the spinner has been selected, set the image to the corresponding mat
     *
     * @param parent   the spinner view
     * @param view     the view that was selected
     * @param position the position of the view in the spinner
     * @param id       the id of the view
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String tag = (String) ((TextView) view).getText();
        setImage(mFilteredMat.get(tag));
    }

    /**
     * invoked when the selection disappears from the spinner (will not happen)
     *
     * @param parent the spinner view
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) { /* do nothing */ }

    public void openPrefs(View view) {
        Intent intent = new Intent(this, FilterPreferenceActivity.class);
        startActivity(intent);
    }
}
