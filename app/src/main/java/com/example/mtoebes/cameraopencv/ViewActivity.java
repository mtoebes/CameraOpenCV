package com.example.mtoebes.cameraopencv;

import android.app.Activity;
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

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mtoebes on 10/13/15.
 */
public class ViewActivity extends Activity implements OnItemSelectedListener {
    public static final String EXTRA_FILE_NAME = "extraFileName";

    private static final String tag_none = "none";
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

    private static final String TAG = "ViewActivity";
    private String mFilename;
    private ImageView mImage;
    private Mat mSrcMat;
    private Bitmap mBitmap;
    private int mWidth, mHeight;
    private Map<String,Mat> mMats = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate");
        setContentView(R.layout.activity_view);
        Bundle extras = getIntent().getExtras();
        mFilename = extras.getString(EXTRA_FILE_NAME);
        int[] size = PhotoHelper.getSize(mFilename);
        mWidth = size[1]; mHeight = size[0];
        mImage = (ImageView)this.findViewById(R.id.image);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        Log.v(TAG, "mWidth " + mWidth + " mHeight " + mHeight);
        Log.v(TAG, "mFilename " + mFilename);

    }

    @Override
    protected void onStart() {
        super.onStart();
        mSrcMat = PhotoHelper.getMat(mFilename);
        setImage(mSrcMat);
    }

    protected void setImage(Mat mat) {
        if(mBitmap == null)
           mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        Log.v(TAG, " rows " + mat.rows() + " cols " + mat.cols());
        Utils.matToBitmap(mat, mBitmap);
        //Log.v(TAG, " rows " + mSrcMat.rows() + " cols " + mSrcMat.cols());
        //mSrcMat = PhotoHelper.getMat(mFilename);
        //mBitmap = PhotoHelper.readIntoBitmap(mFilename);
        mImage.setImageBitmap(mBitmap);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String tag = (String) ((TextView) view).getText();
        Log.v(TAG, "onItemSelected tag " + tag + " position " + position + " id " + id);
        setImage(getMat(tag));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    public Mat getMat(String tag) {
        if(mMats.containsKey(tag)) {
            return mMats.get(tag);
        }

        Mat resMat;
        switch(tag) {
            case tag_hough:
                resMat = MatHelper.getHoughMat(getMat(tag_gray));
                break;
            case tag_canny:
                resMat = MatHelper.getCanny(getMat(tag_gaussian)); break;
            case tag_gaussian:
                resMat = MatHelper.getGaussianBlur(getMat(tag_gray)); break;
            case tag_laplacian:
                resMat = MatHelper.getLaplacian(getMat(tag_gaussian)); break;
            case tag_sobel:
                resMat = MatHelper.getSobel(getMat(tag_gaussian),1,1); break;
            case tag_sobel_x:
                resMat = MatHelper.getSobel(getMat(tag_gaussian),1,0); break;
            case tag_sobel_y:
                resMat = MatHelper.getSobel(getMat(tag_gaussian), 0, 1); break;
            case tag_red:
                resMat = MatHelper.getChannel(mSrcMat, true, false, false); break;
            case tag_green:
                resMat = MatHelper.getChannel(mSrcMat, false, true, false); break;
            case tag_blue:
                resMat = MatHelper.getChannel(mSrcMat, false, false, true); break;
            case tag_gray:
                resMat = PhotoHelper.getMat(mFilename, tag_gray); break;
            case tag_none:
                resMat = mSrcMat; break;
            default:
                resMat = getMat(tag_gray); break;
        }
        Log.v(TAG, "resMat " + resMat.cols() + " " + resMat.rows());
        mMats.put(tag, resMat);
        return resMat;
    }
}
