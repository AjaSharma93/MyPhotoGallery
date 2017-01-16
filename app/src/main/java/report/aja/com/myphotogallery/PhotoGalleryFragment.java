package report.aja.com.myphotogallery;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aja Sharma on 10/12/2016.
 */

public class PhotoGalleryFragment extends Fragment {
    private static final String LOG_TAG=PhotoGalleryFragment.class.getSimpleName();
    private RecyclerView mPhotoRecyclerView;
    private List<GalleryItem> mItems=new ArrayList<>();
    private ThumbnailDownloader<PhotoHolder> mThumbnailDownloader;

    public static PhotoGalleryFragment newInstance()
    {
        return new PhotoGalleryFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        PollService.setServiceAlarm(getActivity(), true);

        new FetchItemsTask().execute();

        Handler responseHandler=new Handler();
        mThumbnailDownloader=new ThumbnailDownloader<>(responseHandler);
        mThumbnailDownloader.setThumbnailDownloadListener(
                new ThumbnailDownloader.ThumbnailDownloadListener<PhotoHolder>()
                {
                    @Override
                    public void onThumbnailDownload(PhotoHolder target, Bitmap bitmap) {
                        Drawable drawable=new BitmapDrawable(getResources(), bitmap);
                        target.bindDrawable(drawable);
                    }
                }
        );

        mThumbnailDownloader.start();
        mThumbnailDownloader.getLooper();
        Log.i(LOG_TAG, "Background thread started");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_photo_gallery, container, false);
        mPhotoRecyclerView=(RecyclerView)v.findViewById(R.id.fragment_photo_gallery_recycler_view);
        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        setupAdapter();
        return v;
    }
    private void setupAdapter(){
        if(isAdded())
        {
            mPhotoRecyclerView.setAdapter(new PhotoAdapter(mItems));
        }
    }

    private class FetchItemsTask extends AsyncTask<Void, Void, List<GalleryItem>>{
        @Override
        protected List<GalleryItem> doInBackground(Void... params) {
            return new FlickrFetcher().fetchItems();

        }

        @Override
        protected void onPostExecute(List<GalleryItem> galleryItems) {
            mItems=galleryItems;
            setupAdapter();
        }
    }

    private class PhotoHolder extends RecyclerView.ViewHolder{
        private ImageView mItemImageView;

        public PhotoHolder(View itemView) {
            super(itemView);
            mItemImageView=(ImageView)itemView.findViewById(R.id.fragment_photo_gallery_image_view);
        }

        public void bindDrawable(Drawable drawable)
        {
            mItemImageView.setImageDrawable(drawable);
        }

    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder>{
        private List<GalleryItem> mGalleryItems;

        public PhotoAdapter(List<GalleryItem> galleryItems)
        {
            mGalleryItems=galleryItems;
        }

        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
           LayoutInflater inflater=LayoutInflater.from(getActivity());
            View view=inflater.inflate(R.layout.gallery_item, parent, false);
            return new PhotoHolder(view);
        }

        @Override
        public void onBindViewHolder(PhotoHolder holder, int position) {
            GalleryItem galleryItem=mGalleryItems.get(position);
            Drawable placeholder=getResources().getDrawable(R.drawable.bill_up_close);
            holder.bindDrawable(placeholder);
            mThumbnailDownloader.queueThumbnail(holder, galleryItem.getUrl());
        }

        @Override
        public int getItemCount() {
            return mGalleryItems.size();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mThumbnailDownloader.quit();
        Log.i(LOG_TAG, "background thread destroyed");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mThumbnailDownloader.clearQueue();
    }
}
