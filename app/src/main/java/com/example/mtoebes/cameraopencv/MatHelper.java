package com.example.mtoebes.cameraopencv;

import android.util.Log;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Size;
import org.opencv.core.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mtoebes on 10/13/15.
 */

//http://docs.opencv.org/modules/imgproc/doc/feature_detection.html?
public class MatHelper extends Mat {
    private static final String TAG = "MatHelper";

    static Scalar black_scalar = new Scalar(0,0,0);

    static int ksize = 5;
    static int scale = 1;
    static int delta = 0;
    static int depth = CvType.CV_64F;
    static double d_rho = 1;
    static double d_theta = Math.PI/180;
    static int hough_threshold = 100;

    // http://docs.opencv.org/master/da/d22/tutorial_py_canny.html#gsc.tab=0
    public static Mat getCanny(Mat srcMat) {
        Mat resMat = new Mat();
        Imgproc.Canny(srcMat, resMat, 100, 255);
        resMat.convertTo(resMat, CvType.CV_8UC3);
        return resMat;
    }

    public static Mat getGaussianBlur(Mat srcMat) {
        Mat resMat = new Mat();
        Imgproc.GaussianBlur(srcMat, resMat, new Size(ksize, ksize), 0, 0);
        resMat.convertTo(resMat, CvType.CV_8UC3);
        return resMat;
   }

    public static Mat getSobel(Mat srcMat, int dx, int dy) {
        Mat resMat = new Mat();
        Imgproc.Sobel(srcMat, resMat, depth, dx, dy, ksize, scale, delta);
        resMat.convertTo(resMat, CvType.CV_8UC3);
        return resMat;
    }

    //http://docs.opencv.org/doc/tutorials/imgproc/imgtrans/laplace_operator/laplace_operator.html
    public static Mat getLaplacian(Mat srcMat) {
        Mat resMat = new Mat();
        Imgproc.Laplacian(srcMat, resMat, depth, ksize, scale, delta);
        resMat.convertTo(resMat, srcMat.type());
        return resMat;
    }

    public static Mat getChannel(Mat srcMat, boolean isRed, boolean isGreen, boolean isBlue) {
        List<Mat> channels = new ArrayList<>(3);
        Log.v(TAG, "srcMat " + srcMat.cols() + " " + srcMat.rows());
        Core.split(srcMat, channels);
        int channel;
        if(isRed)
            channel = 0;
        else if(isGreen)
            channel = 1;
        else
            channel = 2;
        return channels.get(channel);
    }


    // http://docs.opencv.org/doc/tutorials/imgproc/imgtrans/hough_lines/hough_lines.html
    // http://docs.opencv.org/doc/tutorials/core/basic_geometric_drawing/basic_geometric_drawing.html
    public static Mat getHoughMat(Mat srcMat) {
        Mat resMat = srcMat.clone();
        Mat lines = getHoughLines(srcMat);
        Log.v(TAG, lines.cols() + "  " + lines.rows());
        for(int index = 0; index < lines.cols(); index++) {
            double[] vec = lines.get(0, index);
            double x1 = vec[0], y1 = vec[1], x2 = vec[2], y2 = vec[3];
            Point start = new Point(x1, y1);
            Point end = new Point(x2, y2);
            Imgproc.line(resMat, start, end, new Scalar(0, 0, 255));
        }
        return resMat;
    }

    public static Mat getHoughLines(Mat srcMat) {
        Log.v(TAG, " srcMAt type " + srcMat.type());
        Mat houghlines = new Mat();
        Log.v(TAG,  CvType.CV_8UC1 + " " + CvType.CV_8UC2 + " " + CvType.CV_8UC3 + " " + CvType.CV_8UC4);
        Imgproc.Canny(srcMat, srcMat, 100, 250);
        Log.v(TAG, " srcMAt type " + srcMat.type());

        Imgproc.cvtColor(difference, grey, Imgproc.COLOR_BGR2GRAY);
        Imgproc.HoughLines(srcMat, houghlines, d_rho, d_theta, hough_threshold);
        return houghlines;
    }
}
