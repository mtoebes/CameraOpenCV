package com.example.mtoebes.cameraopencv;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
/**
 * PhotoHelper assists in saving and reading bitmaps from file
 */
public class PhotoHelper {

    public static final String DEFAULT_TAG = "RBG";
    public static final String THUMBNAIL_TAG ="thumbnail";
    public static final String GRAY_TAG ="gray";

    private static final String LOG_TAG = "PhotoHelper";
    private static final String IMG = "IMG_";
    private static final String TAG = "_TAG_";
    private static final String ROWS = "_ROWS_";
    private static final String COLS = "_COLS_";
    private static final String EXT = ".jpg";

    private static final String IMAGE_DIRECTORY_NAME = "CameraOpenCV";
    private static final String ORIGINAL_DIRECTORY_NAME = "Originals";
    private static final String BITMAP_DIRECTORY_NAME = "Bitmaps";

    private static final File PUBLIC_STORAGE =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
    private static final File IMAGE_DIRECTORY = createDirectory(PUBLIC_STORAGE, IMAGE_DIRECTORY_NAME);
    private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd_HHmmss");

    private static int thumb_width = 256;
    private static int thumb_height = 144;

    public static void saveCameraImage(Mat mat) {
        String filename = saveMat(mat);
        saveThumbnail(mat, filename);
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGRA2GRAY);
        saveMat(mat, filename, GRAY_TAG);
    }

    public static void saveMatToFile(String filename, Mat mat) {
        Imgcodecs.imwrite(new File(IMAGE_DIRECTORY, filename).getPath(), mat);
    }

    public static String saveMat(Mat mat, String baseFilename, String tag) {
        String filename = setTag(baseFilename, tag);
        saveMatToFile(filename, mat);
        return filename;
    }

    public static String saveMat(Mat mat, String tag) {
        String filename = generateFilename(tag, mat.rows(), mat.cols());
        saveMatToFile(filename, mat);
        return filename;
    }

    public static String saveMat(Mat mat) {
        return saveMat(mat, DEFAULT_TAG);
    }

    public static String saveThumbnail(Mat mat, String baseFilename) {
        Mat resizeMat = new Mat();
        Size sz = new Size(thumb_width,thumb_height);
        Imgproc.resize(mat, resizeMat, sz);
        String filename = setTag(baseFilename, THUMBNAIL_TAG);
        filename = setSize(filename, (int) sz.height, (int) sz.width);
        saveMatToFile(filename, resizeMat);
        return filename;
    }

    public static String getThumbnailFile(File file) {
        String filename = setTag(file.getName(), THUMBNAIL_TAG);
        return setSize(filename, thumb_height, thumb_width);
    }

    public static Mat getMat(File file) {
        return Imgcodecs.imread(file.getPath());
    }

    public static Mat getMat(String filename) {
        return Imgcodecs.imread(new File(IMAGE_DIRECTORY,filename).getPath());
    }

    public static Mat getMat(String baseFilename, String tag) {
        String filename = setTag(baseFilename, tag);
        return Imgcodecs.imread(new File(IMAGE_DIRECTORY,filename).getPath());
    }

    public static Bitmap readIntoBitmap(File file) {
        int[] size = getSize(file.getName());
        Log.v(TAG, file.getPath());
        Log.v(TAG, "size[1] " + size[1] + " size[0] " + size[0]);
        Mat mat = Imgcodecs.imread(file.getPath());
        Bitmap bitmap = Bitmap.createBitmap(size[1], size[0],Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, bitmap);
        return bitmap;
    }

    public static Bitmap readIntoBitmap(String filename) {
        return readIntoBitmap(new File(IMAGE_DIRECTORY, filename));
    }

    /**
     *  Retrieves a List of all the bitmap Files that have the given tag
     * @param tag desired tag to filter by
     * @return List of Files
     */
    public static List<File> getBitmapFiles(String tag) {
        BitmapTagFilter filter = new BitmapTagFilter(tag);
        return new ArrayList<>(Arrays.asList(IMAGE_DIRECTORY.listFiles(filter)));
    }

    /**
     * Remove the given File
     * @param file File to remove
     */
    public static void removeFile(File file) {
        file.delete();
    }

    /**
     * Removes the given Files
     * @param files List of Files to remove
     */
    public static void removeFiles(List<File> files) {
        for (File file : files)
            file.delete();
    }

    /**
     * Generates a unique filename based on the time stamp, sets the tag to be DEFAULT_TAG
     * @return unique filename with tag=DEFAULT_TAG
     */
    public static String generateFilename(String tag, int rows, int cols) {
        return IMG + DATE_FORMAT.format(new Date()) + ROWS + rows + COLS + cols + TAG + tag + EXT;
    }

    /**
     * Generates a filename based on the given finename with the original tag replaced with the given tag
     * @param original filename to use at template
     * @param tag new tag to use
     * @return filename with given tag based off given filename
     */
    public static String generateFilename(String original, String tag) {
        return original.substring(0, original.lastIndexOf(TAG) + TAG.length()) + tag + EXT;
    }

    /**
     * gets the tag of the given filename
     * @param filename name of file to prase tag from
     * @return tag of filename
     */
    public static String setTag(String filename, String tag) {
        return filename.substring(0, filename.lastIndexOf(TAG) + TAG.length()) + tag + filename.substring(filename.lastIndexOf(EXT));
    }

    /**
     * gets the tag of the given filename
     * @param filename name of file to prase tag from
     * @return tag of filename
     */
    public static String getTag(String filename) {
        return filename.substring(filename.lastIndexOf(TAG) + TAG.length(), filename.lastIndexOf(EXT));
    }

    public static int[] getSize(String filename) {
        String rows = filename.substring(filename.lastIndexOf(ROWS) + ROWS.length(), filename.lastIndexOf(COLS));
        String cols = filename.substring(filename.lastIndexOf(COLS) + COLS.length(), filename.lastIndexOf(TAG));
        return new int[]{Integer.parseInt(rows), Integer.parseInt(cols)};
    }

    public static String setSize(String filename, int rows, int cols) {
        String pre = filename.substring(0, filename.lastIndexOf(ROWS) + ROWS.length());
        String post = filename.substring(filename.lastIndexOf(TAG));
        return pre + rows + COLS + cols + post;
    }

    /**
     * attemps to create a new directory at parent/directoryName, does nothing if it alreasy exists
     * @param parent parent to create this directory under
     * @param directoryName name of new directory
     * @return path to created directory
     */
    public static File createDirectory(File parent, String directoryName) {
        File outputDirectory = new File(parent, directoryName);
        if (!outputDirectory.exists())
            if (!outputDirectory.mkdirs()) {
                Log.e(TAG, "Failed to create dir " + outputDirectory.getPath());
                return null;
            }
        return outputDirectory;
    }

    /**
     * Filter that can by applied to a File search to get only Files with the desired tag
     */
    public static class BitmapTagFilter implements FileFilter {
        String mTag;

        public BitmapTagFilter(String tag) {
            mTag = tag;
        }

        public boolean accept(File file) {
            String filename = file.getName();
            return filename.endsWith(EXT) && mTag.equals(getTag(filename));
        }
    }

}
