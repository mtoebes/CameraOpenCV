package com.example.mtoebes.cameraopencv;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.util.Log;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class FilteredMat {
    private static final String TAG = "FilterMat";
    private static final int DEPTH = CvType.CV_8U;
    private static final double D_RHO = 1;
    private static final double D_THETA = Math.PI/90;

    private static final String
            orignal_tag = "Original", gray_tag = "Gray Mask", color_tag = "Color Mask", blur_tag = "Gaussian",
            sobel_tag = "Sobel", laplacian_tag = "Laplacian", canny_tag = "Canny", hough_tag = "Hough";

    private static int channel_num;
    private static int gaussian_ksize;
    private static int sobel_ksize, sobel_dir, sobel_dx, sobel_dy;
    private static int laplacian_ksize;
    private static int canny_lowerThreshold, canny_upperThreshold;
    private static int hough_threshold, hough_minLinLength, hough_maxLineGap;
    private static String hough_mode;

    Mat mSrcMat;
    String mTag = orignal_tag;
    public FilteredMat(Context context) {
        loadPreferences(context);
        mSrcMat = new Mat();
    }

    public Mat update(Mat srcMat) {
        mSrcMat = srcMat;
        return get();
    }

    public void setTag(String tag) {
        mTag = tag;
    }

    public Mat get() {
        return get(mTag);
    }

    public Mat get(String tag) {
        Mat resMat = new Mat();
        switch (tag) {
            case orignal_tag:
                return mSrcMat;
            case blur_tag:
                return getGaussianBlur(get(gray_tag), resMat);
            case gray_tag:
                return getGrayScale(mSrcMat, resMat);
            case color_tag:
                return getChannel(mSrcMat, resMat);
            case sobel_tag:
                return getSobel(get(blur_tag), resMat);
            case laplacian_tag:
                return getLaplacian(get(blur_tag), resMat);
            case canny_tag:
                return getCanny(get(blur_tag), resMat);
            case hough_tag:
                Mat houghSrcMat = get(hough_mode);
                return getHoughMat(houghSrcMat, resMat);
            default:
                return mSrcMat;
        }
    }

    public Mat getGrayScale(Mat srcMat, Mat resMat) {
        Log.v(TAG, "getGrayScale");
        Imgproc.cvtColor(srcMat, resMat, Imgproc.COLOR_BGRA2GRAY);
        return resMat;
    }

    public Mat getGaussianBlur(Mat srcMat, Mat resMat) {
        Log.v(TAG, "getGaussianBlur gaussian_ksize " + gaussian_ksize);
        Imgproc.GaussianBlur(srcMat, resMat, new Size(gaussian_ksize, gaussian_ksize), 0, 0);
        return resMat;
    }

    public Mat getCanny(Mat srcMat, Mat resMat) {
        Log.v(TAG, "getCanny  canny_lowerThreshold " + canny_lowerThreshold + " canny_upperThreshold " + canny_upperThreshold);
        Imgproc.Canny(srcMat, resMat, canny_lowerThreshold, canny_upperThreshold);
        return resMat;
    }

    public Mat getSobel(Mat srcMat, Mat resMat) {
        Log.v(TAG, "getSobel sobel_dx " + sobel_dx + " sobel_dy " + sobel_dy + " sobel_ksize " + sobel_ksize);
        Imgproc.Sobel(srcMat, resMat, DEPTH, sobel_dx, sobel_dy, sobel_ksize, 1, 0);
        return resMat;
    }

    public Mat getLaplacian(Mat srcMat, Mat resMat) {
        Log.v(TAG, "getLaplacian laplacian_ksize " + laplacian_ksize);
        Imgproc.Laplacian(srcMat, resMat, DEPTH, laplacian_ksize, 1, 0);
        return resMat;
    }

    public Mat getChannel(Mat srcMat, Mat resMat) {
        Log.v(TAG, "getChannel channel_num " + channel_num);
        if(channel_num < 3) {
            List<Mat> channels = new ArrayList<>(3);
            Core.split(srcMat, channels);
            resMat = channels.get(channel_num);
        } else {
            getGrayScale(srcMat, resMat);
        }
        return resMat;
    }

    public Mat getHoughMat(Mat srcMat, Mat resMat) {
        Log.v(TAG, "getHoughMat hough_threshold " + hough_threshold + " hough_minLinLength " + hough_minLinLength + " hough_maxLineGap " + hough_maxLineGap);
        Log.v(TAG,hough_mode);
        Imgproc.cvtColor(srcMat, resMat, Imgproc.COLOR_GRAY2BGR);

        Mat lines = new Mat();
        Imgproc.HoughLinesP(srcMat, lines, D_RHO, D_THETA, hough_threshold, hough_minLinLength, hough_maxLineGap);

        Log.v(TAG, "getHoughMat found " + lines.rows() + " lines");
        for(int index = 0; index < lines.rows(); index++) {
            double[] vec = lines.get(index, 0);
            double x1 = vec[0], y1 = vec[1], x2 = vec[2], y2 = vec[3];
            Point start = new Point(x1, y1);
            Point end = new Point(x2, y2);
            Imgproc.line(resMat, start, end, new Scalar(0, 0, 255), 1, Imgproc.LINE_AA, 0);
        }
        return resMat;
    }

    public static void loadPreferences(Context context) {
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(context);
        channel_num = Integer.parseInt(SP.getString("color_channel_pref", "0"));
        gaussian_ksize = Integer.parseInt(SP.getString("gaussian_ksize_pref", "5"));
        sobel_ksize = Integer.parseInt(SP.getString("sobel_ksize_pref", "5"));
        sobel_dir = Integer.parseInt(SP.getString("sobel_dir_pref", "3"));
        sobel_dx = sobel_dir%2;
        sobel_dy = sobel_dir/2;
        laplacian_ksize = Integer.parseInt(SP.getString("laplacian_ksize_pref", "5"));
        canny_lowerThreshold = Integer.parseInt(SP.getString("canny_lower_thresh_pref", "100"));
        canny_upperThreshold = Integer.parseInt(SP.getString("canny_upper_thresh_pref", "200"));
        hough_threshold = Integer.parseInt(SP.getString("hough_threshold_pref", "80"));
        hough_minLinLength = Integer.parseInt(SP.getString("hough_min_len_pref", "10"));
        hough_maxLineGap = Integer.parseInt(SP.getString("hough_max_gap_pref", "30"));
        hough_mode = SP.getString("hough_detection_mode_pref", canny_tag);
    }
}
