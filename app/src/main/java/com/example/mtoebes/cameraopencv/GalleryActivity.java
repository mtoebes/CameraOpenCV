package com.example.mtoebes.cameraopencv;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

//TODO lazyload
public class GalleryActivity extends ListActivity  {
    Bitmap mBitmap;
    private static final String TAG = "GalleryActivity";
    private Context mContext;
    GalleryListAdapter mListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = this.getApplicationContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        mListAdapter = new GalleryListAdapter(R.layout.gallery_list_item, PhotoHelper.getBitmapFiles(PhotoHelper.DEFAULT_TAG));
        this.setListAdapter(mListAdapter);
    }

    private class GalleryListAdapter extends ArrayAdapter<File> {
        int layoutResourceId;
        List<File> data = null;

        public GalleryListAdapter(int layoutResourceId, List<File> data) {
            super(mContext, layoutResourceId, data);
            this.layoutResourceId = layoutResourceId;
            this.data = data;
        }

        @Override
        public View getView(int position, View row, ViewGroup parent) {
            GalleryItemHolder holder;
            if(row == null) {
                LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(layoutResourceId, parent, false);
                holder = new GalleryItemHolder(row);
                row.setTag(holder);
            }
            else {
                holder = (GalleryItemHolder)row.getTag();
            }

            File file = data.get(position);
            Log.v(TAG, file.getPath());
            holder.setFile(file);
            return row;
        }
    }

    private class GalleryItemHolder implements View.OnClickListener {
        ImageView mThumbnail;
        TextView mFileName;
        Button mDeleteButton;
        File mFile;

        public GalleryItemHolder(View view) {
            mThumbnail = (ImageView)view.findViewById(R.id.thumbnail);
            mFileName = (TextView)view.findViewById(R.id.file_name);
            mDeleteButton = (Button)view.findViewById(R.id.delete_button);
        }

        public void setFile(File file) {
            mFile = file;
            mFileName.setText(file.getName());
            mThumbnail.setImageBitmap(PhotoHelper.readIntoBitmap(file));
            mDeleteButton.setOnClickListener(this);
            mThumbnail.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v.getId() == (mDeleteButton.getId())) {
                mListAdapter.remove(mFile);
                PhotoHelper.removeFile(mFile);
            } else {
                Intent intent = new Intent(mContext, ViewActivity.class);
                intent.putExtra(ViewActivity.EXTRA_FILE_PATH, mFile.getPath());
                startActivity(intent);
            }
        }
    }
}
