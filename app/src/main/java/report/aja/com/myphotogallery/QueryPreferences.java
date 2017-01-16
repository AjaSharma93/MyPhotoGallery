package report.aja.com.myphotogallery;

import android.content.Context;
import android.preference.Preference;
import android.preference.PreferenceManager;

/**
 * Created by Aja Sharma on 10/13/2016.
 */

public class QueryPreferences {

    public static final String PREF_SEARCH_QUERY="searchQuery";

    public static final String PREF_LAST_RESULT_ID="lastResultId";

    public static String getStoredQuery(Context context)
    {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_SEARCH_QUERY,null);
    }

    public void setStoredQuery(Context context, String query)
    {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_SEARCH_QUERY, query)
                .apply();

    }

    public static String getLastResultId(Context context)
    {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_LAST_RESULT_ID,null);
    }

    public void setLasResultId(Context context, String lastResultId)
    {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_LAST_RESULT_ID, lastResultId)
                .apply();

    }
}
