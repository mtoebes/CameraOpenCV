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

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mtoebes on 10/13/15.
 */
public class ViewActivity extends Activity implements OnItemSelectedListener {
    public static final String EXTRA_FILE_PATH = "extraFilePath";

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
    private File mFile;
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
        String filePath = extras.getString(EXTRA_FILE_PATH);
        mFile = new File(filePath);
        mImage = (ImageView)this.findViewById(R.id.image);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mSrcMat = PhotoHelper.getMat(mFile);
        mWidth = mSrcMat.cols(); mHeight = mSrcMat.rows();
        mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        setImage(mSrcMat);
    }

    protected void setImage(Mat mat) {
        Utils.matToBitmap(mat, mBitmap);
        mImage.setImageBitmap(mBitmap);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String tag = (String) ((TextView) view).getText();
        Log.v(TAG, "onItemSelected tag " + tag + " position " + position);
        setImage(getMat(tag));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    public Mat getMat(String tag) {
        Log.v(TAG, "getMat " + tag);

        if(mMats.containsKey(tag)) {
            return mMats.get(tag);
        }

        Mat resMat;
        switch(tag) {
            case tag_hough:
                resMat = MatFilter.getHoughMat(getMat(tag_canny)); break;
            case tag_canny:
                resMat = MatFilter.getCanny(getMat(tag_gaussian)); break;
            case tag_gaussian:
                resMat = MatFilter.getGaussianBlur(getMat(tag_gray)); break;
            case tag_laplacian:
                resMat = MatFilter.getLaplacian(getMat(tag_gaussian)); break;
            case tag_sobel:
                resMat = MatFilter.getSobel(getMat(tag_gaussian),1,1); break;
            case tag_sobel_x:
                resMat = MatFilter.getSobel(getMat(tag_gaussian),1,0); break;
            case tag_sobel_y:
                resMat = MatFilter.getSobel(getMat(tag_gaussian), 0, 1); break;
            case tag_red:
                resMat = MatFilter.getChannel(mSrcMat, true, false, false); break;
            case tag_green:
                resMat = MatFilter.getChannel(mSrcMat, false, true, false); break;
            case tag_blue:
                resMat = MatFilter.getChannel(mSrcMat, false, false, true); break;
            case tag_gray:
                resMat = MatFilter.getGrayScale(mSrcMat); break;
            case tag_none:
                resMat = mSrcMat; break;
            default:
                resMat = getMat(tag_gray); break;
        }
        mMats.put(tag, resMat);
        return resMat;
    }
}
