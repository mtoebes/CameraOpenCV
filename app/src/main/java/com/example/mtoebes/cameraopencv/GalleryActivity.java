package com.example.mtoebes.cameraopencv;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

/*
 * GalleryActivity displays a list of all the available unaltered images.
 * Upon clicking a image, ViewActivity is started to display it.
 */
public class GalleryActivity extends ListActivity  {
    private static final String TAG = "GalleryActivity";

    private Context mContext;
    GalleryListAdapter mListAdapter;
    static int GALLERY_ITEM_LAYOUT_ID = R.layout.gallery_list_item;

    // Called as part of the activity lifecycle when this activity is starting.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = this.getApplicationContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        // get list of the files we want to display in gallery
        List<File> data = PhotoHelper.getBitmapFiles(PhotoHelper.DEFAULT_TAG);

        // create the ArrayAdapter that will populate this view
        mListAdapter = new GalleryListAdapter(data);
        this.setListAdapter(mListAdapter);
    }

    /**
     * GalleryListAdapter is the Factory that creates each view in GalleryActivity's list
     * This is the glue between the list and the items it holds
     */
    private class GalleryListAdapter extends ArrayAdapter<File> {
        LayoutInflater mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        /**
         * Creates a new GalleryListAdapter
         * @param data list of files to create items from
         */
        public GalleryListAdapter(List<File> data) {
            super(mContext, GALLERY_ITEM_LAYOUT_ID, data);
        }

        /**
         * Get a View that displays the data at the specified position in the data list.
         * @param position The position of the view within the adapter's data.
         * @param row The old view to reuse, if possible.
         * @param parent The parent that this view will eventually be attached to
         * @return A View corresponding to the data at the specified position.
         */
        @Override
        public View getView(int position, View row, ViewGroup parent) {
            GalleryItem rowItem;

            // if the row doesn't exist, create it and attach a new GalleryItem
            if(row == null) {
                row = mInflater.inflate(GALLERY_ITEM_LAYOUT_ID, parent, false);
                rowItem = new GalleryItem(row);
            } else { // else get the GalleryItem for that row
                rowItem = (GalleryItem) row.getTag();
            }

            // populate the row using the file at the given position
            File file = this.getItem(position);
            rowItem.setFile(file);
            return row;
        }
    }

    /**
     * GalleryItem populates a single row in the Gallery List.
     * It contains a image, filename, and delete button.
     */
    private class GalleryItem implements View.OnClickListener {
        ImageView mThumbnail;
        TextView mFileName;
        Button mDeleteButton;
        File mFile;

        /**
         * create a new GalleryItem
         * @param row View to create the GalleryItem at
         */
        public GalleryItem(View row) {
            row.setTag(this);
            mThumbnail = (ImageView)row.findViewById(R.id.thumbnail);
            mFileName = (TextView)row.findViewById(R.id.file_name);
            mDeleteButton = (Button) row.findViewById(R.id.delete_button);
            mDeleteButton.setOnClickListener(this);
            mThumbnail.setOnClickListener(this);
        }

        /**
         * set the File for this GalleryItem to display info of
         * @param file file to display info of
         */
        public void setFile(File file) {
            mFile = file;
            mFileName.setText(file.getName());
            mThumbnail.setImageBitmap(PhotoHelper.readIntoBitmap(file));
        }

        /**
         * Called when a view has been clicked.
         * clicking mDeleteButton will remove this GalleryItem from the List and delete the file
         * clicking mThumbnail will start the ViewActivity for the file.
         * @param v view that has been clicked
         */
        @Override
        public void onClick(View v) {
            if(v.getId() == (mDeleteButton.getId())) { // delete mFile
                mListAdapter.remove(mFile);
                PhotoHelper.removeFile(mFile);
            } else { // start ViewActivity
                Intent intent = new Intent(mContext, ViewActivity.class);
                intent.putExtra(ViewActivity.EXTRA_FILE_PATH, mFile.getPath());
                startActivity(intent);
            }
        }
    }
}
