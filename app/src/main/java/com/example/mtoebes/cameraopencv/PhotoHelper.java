package com.example.mtoebes.cameraopencv;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

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
    private static final String LOG_TAG = "PhotoHelper";

    public static final String DEFAULT_TAG = "RBG";

    private static final String IMG = "IMG_";
    private static final String TAG = "_TAG_";
    private static final String EXT = ".jpg";

    private static final String IMAGE_DIRECTORY_NAME = "CameraOpenCV";

    private static final File PUBLIC_STORAGE =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
    private static final File IMAGE_DIRECTORY = createDirectory(PUBLIC_STORAGE, IMAGE_DIRECTORY_NAME);
    private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd_HHmmss");

    public static void saveMatToFile(File file, Mat mat) {
        Imgcodecs.imwrite(file.getPath(), mat);
    }

    public static File saveMat(Mat mat, File file, String newTag) {
        File newFile = generateFile(file.getParentFile(), file.getName(), newTag);
        saveMatToFile(file, mat);
        return newFile;
    }

    public static File saveMat(Mat mat, String tag) {
        File newFile = generateFile(IMAGE_DIRECTORY, null, tag);
        saveMatToFile(newFile, mat);
        return newFile;
    }

    public static File saveMat(Mat mat) {
        return saveMat(mat, DEFAULT_TAG);
    }

    public static Mat getMat(File file) {
        return Imgcodecs.imread(file.getPath());
    }

    public static Bitmap readIntoBitmap(File file) {
        Mat mat = getMat(file);
        Bitmap bitmap = Bitmap.createBitmap(mat.cols(), mat.rows(),Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, bitmap);
        return bitmap;
    }


    /**
     * Remove the given File
     * @param file File to remove
     */
    public static void removeFile(File file) {
        file.delete();
    }

    /**
     * Generates a unique filename based on the time stamp, sets the tag to be DEFAULT_TAG
     * @return unique filename with tag=DEFAULT_TAG
     */
    public static String generateFilename(String tag) {
        return IMG + DATE_FORMAT.format(new Date()) + TAG + tag + EXT;
    }

    public static String replaceTag(String filename, String tag) {
        return filename.substring(0, filename.lastIndexOf(TAG) + TAG.length()) + tag + EXT;
    }

    public static File generateFile(File directory, String filename, String tag) {
        String newFilename;

        if(filename == null)
            newFilename = generateFilename(tag);
         else
            newFilename = replaceTag(filename, tag);

        return new File(directory, newFilename);
    }

    /**
     * gets the tag of the given filename
     * @param filename name of file to parse tag from
     * @return tag of filename
     */
    public static String getTag(String filename) {
        return filename.substring(filename.lastIndexOf(TAG) + TAG.length(), filename.lastIndexOf(EXT));
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

    /**
     * attempt to create a new directory at parent/directoryName, does nothing if it alreasy exists
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
}
