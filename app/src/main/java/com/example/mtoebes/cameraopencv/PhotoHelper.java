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

    // Identifiers that make up a filename
    private static final String IMG = "IMG_";
    private static final String TAG = "_TAG_";
    private static final String EXT = ".jpg";

    // Name of the directory to store the images in
    private static final String IMAGE_DIRECTORY_NAME = "CameraOpenCV";

    // Directory of public storage for images
    private static final File PUBLIC_STORAGE =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

    // Directory of storage for our images
    private static final File IMAGE_DIRECTORY = createDirectory(PUBLIC_STORAGE, IMAGE_DIRECTORY_NAME);

    private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd_HHmmss");

    /**
     * Saves a mat to a file
     * @param mat mat to save
     * @param file location to save to
     */
    public static void saveMatToFile(Mat mat, File file) {
        Imgcodecs.imwrite(file.getPath(), mat);
    }

    /**
     * Saves a mat to a file who's name has been updated with newTag
     * @param mat mat to save
     * @param file location to save at
     * @param newTag tag to update the filename with
     * @return location mat was saved to
     */
    public static File saveMat(Mat mat, File file, String newTag) {
        File newFile = generateFile(file.getParentFile(), file.getName(), newTag);
        saveMatToFile(mat, file);
        return newFile;
    }

    /**
     * Saves a mat to file under IMAGE_DIRECTORY
     * filename will be based on current time and have the default tag
     * @param mat mat to save
     * @return location mat was saved to
     */
    public static File saveMat(Mat mat) {
        File newFile = generateFile(IMAGE_DIRECTORY, null, DEFAULT_TAG);
        saveMatToFile(mat, newFile);
        return newFile;
    }

    /**
     * Reads mat from a file
     * @param file file to read from
     * @return mat saved in file
     */
    public static Mat getMat(File file) {
        return Imgcodecs.imread(file.getPath());
    }

    /**
     * Reads mat from a file and returns it as a Bitmap
     * @param file file to read from
     * @return Bitmap of mat saved in file
     */
    public static Bitmap readIntoBitmap(File file) {
        Mat mat = getMat(file);
        Bitmap bitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
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
     * returns a filename based on the given img and tag values
     * @param img IMG value
     * @param tag TAG value
     * @return filename of the formate IMG_img_TAG_tag.jpg
     */
    public static String generateFilename(String img, String tag) {
        return IMG + img + TAG + tag + EXT;
    }
    /**
     * returns a unique filename based on the timestamp and with the given tag
     * @return unique filename with the given tag
     */
    public static String generateFilename(String tag) {
        return generateFilename(DATE_FORMAT.format(new Date()),tag);
    }

    /**
     * returns a new filename with the same IMG values as orgFilename but with TAg value newTag
     * @param orgFilename name of file to grab IMG value from
     * @param newTag TAG value to use in the new filename
     * @return new filename with the IMG value of orgFilename and the TAG value of newTag
     */
    public static String replaceTag(String orgFilename, String newTag) {
        return generateFilename(getIMG(orgFilename), newTag);
    }

    /**
     * returns a File who's parent is directory and name is filename with TAG value newTag
     * if filename is null, a new filename is created
     * @param directory parent to create the file under
     * @param filename filename to use the IMG value from
     * @param newTag TAG value to use
     * @return File with save path as directory/filename but with the TAG newTag
     */
    public static File generateFile(File directory, String filename, String newTag) {
        String newFilename;

        if(filename == null)
            newFilename = generateFilename(newTag);
         else
            newFilename = replaceTag(filename, newTag);

        return new File(directory, newFilename);
    }

    /**
     * gets the TAG value of the given filename
     * @param filename name of file to get tag from
     * @return tag of filename
     */
    public static String getTAG(String filename) {
        return filename.substring(filename.lastIndexOf(TAG) + TAG.length(), filename.lastIndexOf(EXT));
    }

    /**
     * gets the IMG value of the given filename
     * @param filename name of the file to get img from
     * @return img of the filename
     */
    public static String getIMG(String filename) {
        return filename.substring(filename.lastIndexOf(IMG) + IMG.length(), filename.lastIndexOf(TAG));
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
            return filename.endsWith(EXT) && mTag.equals(getTAG(filename));
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
