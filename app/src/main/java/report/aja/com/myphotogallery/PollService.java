package report.aja.com.myphotogallery;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.SystemClock;
import android.util.Log;

import java.util.List;

/**
 * Created by Aja Sharma on 10/13/2016.
 */

public class PollService extends IntentService {

    private static final String LOG_TAG=PollService.class.getSimpleName();

    public static final int POLLING_INTERVAL=1000*60;

    public static Intent newIntent(Context context)
    {
        return new Intent(context, PollService.class);
    }

    public static void setServiceAlarm(Context context, boolean isOn)
    {
        Intent i=PollService.newIntent(context);
        PendingIntent pendingIntent=PendingIntent.getService(context, 0, i, 0);
        AlarmManager alarmManager=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        if(isOn)
        {
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime()
                    ,POLLING_INTERVAL, pendingIntent);
        }else
        {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }

    public static boolean isServiceAlarmOn(Context context)
    {
        Intent intent=PollService.newIntent(context);
        PendingIntent pi=PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_NO_CREATE);
        return  pi !=null;
    }


    public PollService() {
        super(LOG_TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(!isNetworkAvailableAndConnected())
        {
            return;
        }

    }

    private boolean isNetworkAvailableAndConnected(){
        ConnectivityManager connectivityManager=(ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        boolean isNetworkAvailable=connectivityManager.getActiveNetworkInfo()!=null;
        boolean isNetworkConnected=isNetworkAvailable && connectivityManager.getActiveNetworkInfo().isConnected();

        return isNetworkConnected;
    }
}
