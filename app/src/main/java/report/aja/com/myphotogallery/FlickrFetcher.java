package report.aja.com.myphotogallery;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aja Sharma on 10/12/2016.
 */

public class FlickrFetcher  {

    private static final String LOG_TAG=FlickrFetcher.class.getSimpleName();
    private static final String API_KEY="0cd92eb41ad87e53fe02d7be485c0237";

    public byte[] getURLBytes(String urlSpec) throws IOException {
        URL url=new URL(urlSpec);
        HttpURLConnection connection=(HttpURLConnection)url.openConnection();
        try
        {
            ByteArrayOutputStream out=new ByteArrayOutputStream();
            InputStream inputStream=connection.getInputStream();
            if(connection.getResponseCode()!=HttpURLConnection.HTTP_OK)
            {
                throw new IOException(connection.getResponseMessage()+" :with "+urlSpec);
            }
            int bytesRead=0;
            byte[] buffer=new byte[1024];
            while((bytesRead= inputStream.read(buffer))>0)
            {
                out.write(buffer,0,bytesRead);
            }
            out.close();
            return out.toByteArray();
        }finally {
            connection.disconnect();
        }
    }

    public String getURLString(String urlSpec) throws IOException{
        return new String(getURLBytes(urlSpec));
    }

    public List<GalleryItem> fetchItems()
    {
        List<GalleryItem> items=new ArrayList<>();

        try {
            String url = Uri.parse("https://api.flickr.com/services/rest/")
                    .buildUpon()
                    .appendQueryParameter("method", "flickr.photos.getRecent")
                    .appendQueryParameter("api_key", API_KEY)
                    .appendQueryParameter("format", "json")
                    .appendQueryParameter("nojsoncallback", "1")
                    .appendQueryParameter("extras", "url_s")
                    .build().toString();
            String jsonString = getURLString(url);
            Log.i(LOG_TAG, "Recieved JSON" + jsonString);
            JSONObject jsonBody = new JSONObject(jsonString);
            parseItems(items, jsonBody);
        }catch(JSONException je)
        {
            Log.e(LOG_TAG, "Failed to parse",je);

        }catch (IOException ioe)
        {
            Log.e(LOG_TAG, "Failed to fetch", ioe);
        }
        return items;
    }

    private void parseItems(List<GalleryItem> items, JSONObject jsonBody)
            throws IOException, JSONException{
        JSONObject photosJsonObject=jsonBody.getJSONObject("photos");
        JSONArray photoJsonArray=photosJsonObject.getJSONArray("photo");
        for(int i=0;i<photoJsonArray.length(); i++)
        {
            JSONObject photoJsonObject=photoJsonArray.getJSONObject(i);
            GalleryItem item=new GalleryItem();
            item.setId(photoJsonObject.getString("id"));
            item.setCaption(photoJsonObject.getString("title"));

            if(!photoJsonObject.has("url_s"))
            {
                continue;
            }
            item.setUrl(photoJsonObject.getString("url_s"));
            items.add(item);
        }
    }
}
