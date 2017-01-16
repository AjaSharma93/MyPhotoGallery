package report.aja.com.myphotogallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.lang.annotation.Target;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by Aja Sharma on 10/13/2016.
 */

public class ThumbnailDownloader<T> extends HandlerThread{
    public static final String LOG_TAG=ThumbnailDownloader.class.getSimpleName();
    private static final int MESSAGE_DOWNLOAD=0;

    private Handler mRequestHandler;
    private ConcurrentMap<T, String> mRequestMap=new ConcurrentHashMap<>();

    private Handler mResponseHandler;

    private ThumbnailDownloadListener<T> mThumbnailDownloadListener;

    public interface ThumbnailDownloadListener<T>{
        void onThumbnailDownload(T target, Bitmap bitmap);
    }

    public void setThumbnailDownloadListener(ThumbnailDownloadListener<T> listener)
    {
        mThumbnailDownloadListener=listener;
    }



    public ThumbnailDownloader(Handler responseHandler) {
        super(LOG_TAG);
        mResponseHandler=responseHandler;

    }

    @Override
    protected void onLooperPrepared() {
        mRequestHandler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what==MESSAGE_DOWNLOAD)
                {
                    T target=(T) msg.obj;
                    Log.i(LOG_TAG, "got a request of url"+mRequestMap.get(target));
                    handleRequest(target);
                }
            }
        };
    }

    private void handleRequest(final T target)
    {
        try
        {
            final String url=mRequestMap.get(target);
            if(url==null)
                return;
            byte[] bitmapbytes= new FlickrFetcher().getURLBytes(url);
            final Bitmap bitmap=BitmapFactory
                    .decodeByteArray(bitmapbytes,0,bitmapbytes.length);
            Log.i(LOG_TAG,"Bitmap created");
            mResponseHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(mRequestMap.get(target)!=url)
                    {
                        return;
                    }

                    mRequestMap.remove(target);
                    mThumbnailDownloadListener.onThumbnailDownload(target, bitmap);
                }
            });


        }catch (IOException ioe)
        {
            Log.e(LOG_TAG, "Error downloading image", ioe );
        }
    }

    public void queueThumbnail(T target, String url)
    {
        Log.i(LOG_TAG, "Got a URL: "+url);
        if(url==null)
        {
            mRequestMap.remove(target);
        }
        else
        {
            mRequestMap.put(target, url);
            mRequestHandler.obtainMessage(MESSAGE_DOWNLOAD, target)
                    .sendToTarget();
        }
    }

    public void clearQueue()
    {
        mResponseHandler.removeMessages(MESSAGE_DOWNLOAD);
    }



}
