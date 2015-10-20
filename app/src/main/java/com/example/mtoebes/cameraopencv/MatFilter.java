package com.example.mtoebes.cameraopencv;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class used to perform various feature detections
 */
public class MatFilter {
    private static final String TAG = "MatHelper";
    private static int DEPTH = CvType.CV_8U;

    static double D_RHO = 1;
    static double D_THETA = Math.PI/180;

    static int gaussian_ksize = 5;
    static int canny_lowerThreshold = 100;
    static int canny_upperThreshold = 200;
    static int sobel_ksize = 7;
    static int laplacian_ksize = 3;
    static int hough_threshold = 80;
    static int hough_minLinLength = 30;
    static int hough_maxLineGap = 10;

    /**
     * gets the grayscale mat resulting from the given mat
     * @param srcMat a RGB Mat
     * @return grayscale of srcMat
     */
    public static Mat getGrayScale(Mat srcMat) {
        Mat resMat = new Mat();
        Imgproc.cvtColor(srcMat, resMat, Imgproc.COLOR_BGRA2GRAY);
        return resMat;
    }

    /**
     * Performs a Gaussian blur
     * see http://docs.opencv.org/modules/gpu/doc/image_filtering.html?highlight=gaussianblur
     * @param srcMat a grayscale Mat
     * @param ksize blurring kernel size
     * @return GaussianBlur's output
     */
    public static Mat getGaussianBlur(Mat srcMat, int ksize) {
        Mat resMat = new Mat();
        Imgproc.GaussianBlur(srcMat, resMat, new Size(ksize, ksize), 0, 0);
        return resMat;
    }

    /**
     * Performs a Canny edge detection
     * see http://docs.opencv.org/modules/ocl/doc/feature_detection_and_description.html?highlight=canny
     * see http://docs.opencv.org/doc/tutorials/imgproc/imgtrans/canny_detector/canny_detector.html?highlight=canny
     * @param srcMat a grayscale Mat, it is recommened that it has been blurred to eleminate noise
     * @param lowerThreshold lower gradient cutoff
     * @param upperTheshold upper gradient cutoff
     * @return Canny's output
     */
   public static Mat getCanny(Mat srcMat, int lowerThreshold, int upperTheshold) {
        Mat resMat = new Mat();
        Imgproc.Canny(srcMat, resMat, lowerThreshold, upperTheshold);
        return resMat;
    }

    /**
     * Performs a Sobel image filter
     * see http://docs.opencv.org/modules/gpu/doc/image_filtering.html?highlight=sobel
     * see http://docs.opencv.org/master/d2/d2c/tutorial_sobel_derivatives.html#gsc.tab=0
     * @param srcMat a grayscale Mat, it is recommened that it has been blurred to eleminate noise
     * @param dx derivative order for x, recommend to use 0 or 1
     * @param dy derivative order for y, recommend to use 0 or 1
     * @param ksize Size of the Sobel kernel. Possible values are 1, 3, 5 or 7.
     * @return Sobel's output
     */
    public static Mat getSobel(Mat srcMat, int dx, int dy, int ksize) {
        Mat resMat = new Mat();
        Imgproc.Sobel(srcMat, resMat, DEPTH, dx, dy, ksize, 1, 0);
        return resMat;
    }

    /**
     * Performs a Laplacian image filter
     * see http://docs.opencv.org/modules/gpu/doc/image_filtering.html?highlight=sobel#gpu-laplacian
     * see http://docs.opencv.org/doc/tutorials/imgproc/imgtrans/laplace_operator/laplace_operator.html
     * @param srcMat a grayscale Mat, it is recommened that it has been blurred to eleminate noise
     * @param ksize Size of the Laplacian kernal. Possible values are 1 or 3.
     * @return Laplacian's output
     */
    public static Mat getLaplacian(Mat srcMat, int ksize) {
        Mat resMat = new Mat();
        Imgproc.Laplacian(srcMat, resMat, DEPTH, ksize, 1, 0);
        return resMat;
    }

    /**
     * Performs a color mask on the given Mat to show only one channel(red,green,or blue) in grayscale
     * @param srcMat a RBG Mat
     * @param channel channel to mask to (0=red,1=green,2=blue)
     * @return mat of a single color channel taken from srcMat
     */
    public static Mat getChannel(Mat srcMat, int channel) {
        List<Mat> channels = new ArrayList<>(3);
        Core.split(srcMat, channels);
        return channels.get(channel);
    }

    /**
     * Performs a HoughLines transform
     * see http://docs.opencv.org/modules/imgproc/doc/feature_detection.html?highlight=hough#cv2.HoughLinesP
     * see http://docs.opencv.org/doc/tutorials/imgproc/imgtrans/hough_lines/hough_lines.html?highlight=hough
     * @param srcMat a Mat of type CvType.CV_8U (single 8 bit channel) that is a result for a edge detection filter
     * @param threshold minumum points needed to form a line
     * @param minLinLength minimum length of a line
     * @param maxLineGap maximum gap allowed between points in a line
     * @return Mat with lines drawn on the input Mat srcMat
     */
    public static Mat getHoughMat(Mat srcMat, int threshold, int minLinLength, int maxLineGap) {
        Mat resMat = new Mat();
        Imgproc.cvtColor(srcMat, resMat, Imgproc.COLOR_GRAY2BGR);

        Mat lines = new Mat();
        Imgproc.HoughLinesP(srcMat, lines, D_RHO, D_THETA, hough_threshold, hough_minLinLength, hough_maxLineGap);

        for(int index = 0; index < lines.rows(); index++) {
            double[] vec = lines.get(index, 0);
            double x1 = vec[0], y1 = vec[1], x2 = vec[2], y2 = vec[3];
            Point start = new Point(x1, y1);
            Point end = new Point(x2, y2);
            Imgproc.line(resMat, start, end, new Scalar(0, 0, 255), 5, Imgproc.LINE_AA, 0);
        }
        return resMat;
    }


    public static Mat getGaussianBlur(Mat srcMat) {
        return getGaussianBlur(srcMat, gaussian_ksize);
    }

    public static Mat getCanny(Mat srcMat) {
        return getCanny(srcMat, canny_lowerThreshold, canny_upperThreshold);
    }
    public static Mat getSobel(Mat srcMat, int dx, int dy) {
        return getSobel(srcMat, dx, dy, sobel_ksize);
    }

    public static Mat getLaplacian(Mat srcMat) {
        return getLaplacian(srcMat, laplacian_ksize);
    }

    public static Mat getHoughMat(Mat srcMat) {
        return getHoughMat(srcMat, hough_threshold, hough_minLinLength, hough_maxLineGap);
    }
}
