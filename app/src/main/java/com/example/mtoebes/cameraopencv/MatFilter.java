package com.example.mtoebes.cameraopencv;

import android.util.Log;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mtoebes on 10/13/15.
 */

//http://docs.opencv.org/modules/imgproc/doc/feature_detection.html?
public class MatFilter {
    private static final String TAG = "MatHelper";

    static Scalar black_scalar = new Scalar(0,0,0);

    static int ksize = 5;
    static int scale = 1;
    static int delta = 0;
    static int depth = CvType.CV_8U;
    static double d_rho = 1;
    static double d_theta = Math.PI/180;
    static int hough_threshold = 100;
    static int hough_minLinLength = 50;
    static int hough_maxLineGap = 25;

    public static Mat getGrayScale(Mat srcMat) {
        Mat resMat = new Mat();
        Imgproc.cvtColor(srcMat, resMat, Imgproc.COLOR_BGRA2GRAY);
        return resMat;
    }

    // http://docs.opencv.org/master/da/d22/tutorial_py_canny.html#gsc.tab=0
    public static Mat getCanny(Mat srcMat) {
        Mat resMat = new Mat();
        Imgproc.Canny(srcMat, resMat, 100, 255);
        //resMat.convertTo(resMat, CvType.CV_8UC3);
        return resMat;
    }

    public static Mat getGaussianBlur(Mat srcMat) {
        Mat resMat = new Mat();
        Imgproc.GaussianBlur(srcMat, resMat, new Size(ksize, ksize), 0, 0);
        return resMat;
   }

    public static Mat getSobel(Mat srcMat, int dx, int dy) {
        Mat resMat = new Mat();
        Imgproc.Sobel(srcMat, resMat, depth, dx, dy, ksize, scale, delta);
        //resMat.convertTo(resMat, CvType.CV_8UC3);
        return resMat;
    }

    //http://docs.opencv.org/doc/tutorials/imgproc/imgtrans/laplace_operator/laplace_operator.html
    public static Mat getLaplacian(Mat srcMat) {
        Mat resMat = new Mat();
        Imgproc.Laplacian(srcMat, resMat, depth, ksize, scale, delta);
        //resMat.convertTo(resMat, srcMat.type());
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


    //http://docs.opencv.org/doc/tutorials/imgproc/imgtrans/hough_lines/hough_lines.html?highlight=hough
    public static Mat getHoughMat(Mat srcMat) {
        Mat resMat = new Mat();
        Imgproc.cvtColor(srcMat, resMat, Imgproc.COLOR_GRAY2BGR);

        Mat lines = new Mat();
        Imgproc.HoughLinesP(srcMat, lines, d_rho, d_theta, hough_threshold, hough_minLinLength, hough_maxLineGap);

        Log.v(TAG, " lines " + lines.rows());
        for(int index = 0; index < lines.rows(); index++) {
            double[] vec = lines.get(index, 0);
            double x1 = vec[0], y1 = vec[1], x2 = vec[2], y2 = vec[3];
            Log.v(TAG, x1 + " " + y1 + " " + x2 + " " + y2);
            Point start = new Point(x1, y1);
            Point end = new Point(x2, y2);
            Imgproc.line(resMat, start, end, new Scalar(0, 0, 255), 5, Imgproc.LINE_AA, 0);
        }
        Log.v(TAG, "cols " + resMat.cols() + " " + resMat.rows());
        return resMat;
    }

}
