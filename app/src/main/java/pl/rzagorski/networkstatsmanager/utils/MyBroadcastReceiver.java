package pl.rzagorski.networkstatsmanager.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by bob on 2017-06-21.
 */

public class MyBroadcastReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        Log.i("BOBBERNIER","onReceive" + context.getPackageName().toString() +  intent.getAction().toString() );
        Log.i("BOBBERNIER", "received");
    }
}
