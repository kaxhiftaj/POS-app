package com.techease.posapp.ui.fragments;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.techease.posapp.R;

import java.util.ArrayList;

public class UploadFragments extends Fragment {
    private int count;
    private Bitmap[] thumbnails;
    private boolean[] thumbnailsselection;
    private String[] arrPath;
    private  ImageAdapter imageAdapter;
    private static final int PICK_FROM_CAMERA = 1;
    ArrayList<String> IPath = new ArrayList<String>();
    public static Uri uri;
    public  static boolean isPictureSelected = false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_upload_fragments, container, false);
        final String[] columns = { MediaStore.Images.Media.DATA,
                MediaStore.Images.Media._ID };
        final String orderBy = MediaStore.Images.Media._ID;
        Cursor imagecursor = getActivity().managedQuery(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
                null, orderBy);
        int image_column_index = imagecursor
                .getColumnIndex(MediaStore.Images.Media._ID);
        this.count = imagecursor.getCount();
        this.thumbnails = new Bitmap[this.count];
        this.arrPath = new String[this.count];
        this.thumbnailsselection = new boolean[this.count];
        for (int i = 0; i < this.count; i++) {
            imagecursor.moveToPosition(i);
            int id = imagecursor.getInt(image_column_index);
            int dataColumnIndex = imagecursor
                    .getColumnIndex(MediaStore.Images.Media.DATA);
            thumbnails[i] = MediaStore.Images.Thumbnails.getThumbnail(
                    getActivity().getContentResolver(), id,
                    MediaStore.Images.Thumbnails.MICRO_KIND, null);
            arrPath[i] = imagecursor.getString(dataColumnIndex);
        }
        GridView imagegrid = (GridView) view.findViewById(R.id.PhoneImageGrid);
        imageAdapter = new ImageAdapter();
        imagegrid.setAdapter(imageAdapter);
        imagecursor.close();

        final Button uploadBtn = (Button) view.findViewById(R.id.uploadDONE);
        uploadBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                final int len = thumbnailsselection.length;
                int cnt = 0;
                String selectImages = "";
                for (int i = 0; i < len; i++) {
                    if (thumbnailsselection[i]) {
                        cnt++;
                        selectImages = arrPath[i];
                        IPath.add(selectImages);
                    }
                }

                if (cnt == 0) {
                    Toast.makeText(getActivity(),
                            "Please select at least one image",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(),
                            "You've selected Total " + cnt + " images",
                            Toast.LENGTH_LONG).show();
                    isPictureSelected = true;
                    Fragment fragment = new JobCompletedFragment();
                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList("IMAGE",IPath);
                    fragment.setArguments(bundle);
                    getFragmentManager().beginTransaction().replace(R.id.fragment_main, fragment).commit();

                }
            }
        });
        return view;
    }
    public class ImageAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public ImageAdapter() {
            mInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            return count;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            //UploadActivity.ViewHolder holder;
            ViewHolder holder = new ViewHolder();
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.galleryitem, null);
                holder.imageview = (ImageView) convertView
                        .findViewById(R.id.thumbImage);
               // holder.imageview.setLayoutParams(new GridView.LayoutParams(570, 420,Gravity.CENTER));
                holder.checkbox = (CheckBox) convertView
                        .findViewById(R.id.itemCheckBox);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.checkbox.setId(position);
            holder.imageview.setId(position);
            holder.checkbox.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    CheckBox cb = (CheckBox) v;
                    int id = cb.getId();
                    if (thumbnailsselection[id]) {
                        cb.setChecked(false);
                        thumbnailsselection[id] = false;
                    } else {
                        cb.setChecked(true);
                        thumbnailsselection[id] = true;
                    }
                }
            });


            holder.imageview.setImageBitmap(thumbnails[position]);
            holder.checkbox.setChecked(thumbnailsselection[position]);
            holder.id = position;
            return convertView;
        }
    }


    class ViewHolder {
        ImageView imageview;
        CheckBox checkbox;
        int id;
    }
}
