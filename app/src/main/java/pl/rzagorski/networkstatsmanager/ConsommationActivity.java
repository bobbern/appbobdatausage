package pl.rzagorski.networkstatsmanager;

import android.*;
import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AppOpsManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.icu.text.DateFormat;
import android.icu.text.DecimalFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.media.audiofx.BassBoost;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import java.util.Date;
import java.lang.Class;
import java.lang.reflect.Method;
import pl.rzagorski.networkstatsmanager.utils.NetworkStatsHelper;
import pl.rzagorski.networkstatsmanager.view.ListActivity;
import pl.rzagorski.networkstatsmanager.view.StatsActivity;

public class ConsommationActivity extends AppCompatActivity {

   // @Override
    public static final String PREFS = "PREFS";
    public static final String PREF_KEY_DATA_LIMITE_MAX = "PREF_KEY_DATA_LIMITE_MAX";
    public static final String PREF_KEY_DATA_LIMITE_MAX_FLOAT = "PREF_KEY_DATA_LIMITE_MAX_FLOAT";
    public static final String PREF_KEY_DEBUT_CYCLE = "PREF_KEY_DEBUT_CYCLE";
    public static final int REQUESTCHANGEPARAMETERS = 1;
    private static final int READ_PHONE_STATE_REQUEST = 37;
    private static final int NOTI_PRIMARY1 = 1100;
    private static final int NOTI_PRIMARY2 = 1101;
    private static final int NOTI_SECONDARY1 = 1200;
    private static final int NOTI_SECONDARY2 = 1201;

    SharedPreferences shf ;
    NetworkStatsManager networkStatsManager;
    NetworkStatsHelper networkStatHelper;
    TextView tv1,tv2,tv3, tv4 , tv5, tv6, tv7;
    Calendar calendar;
    Float fPC;
    String strPC;
    String strPcPeriode;
    String strGigs;

    float fGigd;
    int ijour_debut_cycle;
    int idata_limite_max;
    float fdata_limite_max;
    Intent i;
    Bundle paquetRetour;

    private Notification.Builder nb = null;
    private pl.rzagorski.networkstatsmanager.utils.NotificationHelper noti;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consommation);
        requestPermissions();
        networkStatsManager = (NetworkStatsManager) getApplicationContext().getSystemService(Context.NETWORK_STATS_SERVICE);
        networkStatHelper = new NetworkStatsHelper(networkStatsManager);
        noti = new  pl.rzagorski.networkstatsmanager.utils.NotificationHelper(this);
       // requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);

        String strPC;
        tv1 = (TextView) findViewById(R.id.textView);
        tv2 = (TextView) findViewById(R.id.textView2);
        tv3 = (TextView) findViewById(R.id.textView3);
        tv4 = (TextView) findViewById(R.id.textView4);
        tv5 = (TextView) findViewById(R.id.textView5);
        tv6 = (TextView) findViewById(R.id.textView6);
        tv7 = (TextView) findViewById(R.id.textView7);
        shf  = getSharedPreferences(PREFS, MODE_PRIVATE);
        if (shf.contains(PREF_KEY_DEBUT_CYCLE) &&  shf.contains(PREF_KEY_DATA_LIMITE_MAX) &&  shf.contains(PREF_KEY_DATA_LIMITE_MAX_FLOAT))
        {
            ijour_debut_cycle = shf.getInt(PREF_KEY_DEBUT_CYCLE, 4 );
            idata_limite_max  = shf.getInt(PREF_KEY_DATA_LIMITE_MAX, 44);
            fdata_limite_max  = shf.getFloat(PREF_KEY_DATA_LIMITE_MAX_FLOAT, 45);
        }
        else
        {
            shf
                    .edit()
                    .putInt(PREF_KEY_DEBUT_CYCLE,2)
                    .putInt(PREF_KEY_DATA_LIMITE_MAX, 10)
                    .putFloat(PREF_KEY_DATA_LIMITE_MAX_FLOAT, 10)
                    .apply();
        }

        ijour_debut_cycle = shf.getInt(PREF_KEY_DEBUT_CYCLE, 5 );
        idata_limite_max  = shf.getInt(PREF_KEY_DATA_LIMITE_MAX, 55);
        fdata_limite_max = shf.getFloat(PREF_KEY_DATA_LIMITE_MAX_FLOAT, 56);

        Calendar currentDate = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, ijour_debut_cycle );
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.SECOND, 1);
        calendar.set(Calendar.AM_PM, 0);
        if( calendar.get(Calendar.DAY_OF_MONTH) > currentDate.get(Calendar.DAY_OF_MONTH)) calendar.set(Calendar.MONTH , calendar.get(Calendar.MONTH) - 1);
        if (calendar.get(Calendar.MONTH) == 0) {
            calendar.set(Calendar.MONTH, 12);
            calendar.set(Calendar.YEAR , calendar.get(Calendar.YEAR) - 1);
        }
        tv5.setText(String.valueOf(ijour_debut_cycle));
        float flgRx = getAllRxBytesMobile(this);
        float flgTx = getAllTxBytesMobile(this);
        DecimalFormat d2Format = new DecimalFormat("0.00");
        fGigd = (flgRx + flgTx) / 1024 / 1024 / 1024;
        strGigs = String.format("%.2f GIGS", (fGigd));
        tv1.setText(d2Format.format(flgRx / 1024 / 1024 / 1024) + " Gigs ");
        tv2.setText(d2Format.format(flgTx / 1024 / 1024 / 1024) + " Gigs");
        tv3.setText((d2Format.format((flgRx + flgTx) / 1024 / 1024 / 1024)) + " Gigs");
        fPC =    (fGigd / fdata_limite_max * 100);
        strPC = String.format("%.2f %%", fPC);
        tv6.setText(strPC);
        tv4.setText(String.valueOf(fdata_limite_max)); // 28 29 30 31 ...

        Calendar cToday = Calendar.getInstance();
        int iDateJour =  cToday.get(Calendar.DAY_OF_MONTH);
        int iMaxJoursMoisDate;
        int iNbrJourUtilise;
        if (iDateJour < ijour_debut_cycle)
        {
            cToday.add(Calendar.MONTH, -1);
            iMaxJoursMoisDate =  cToday.getActualMaximum(Calendar.DAY_OF_MONTH);
            iNbrJourUtilise = iMaxJoursMoisDate - ( ijour_debut_cycle - 1) +( iDateJour - 1);
        }
        else
        {
             iNbrJourUtilise = iDateJour - ijour_debut_cycle ;
             iMaxJoursMoisDate = cToday.getActualMaximum(Calendar.DAY_OF_MONTH);
        }
        fPC =   (float) ( (((float)iNbrJourUtilise * 24) + cToday.get(Calendar.HOUR_OF_DAY )  )/  ((float)iMaxJoursMoisDate *24) * (100));
            strPcPeriode = String.format("%.2f %%", fPC);

//
//        if (iDateJour == 1) iDateJour = iMaxJoursMoisDate;
//          {
//             }
//
//             fPC =   (float) ( (float)iNbrJourUtilise /  (float)iMaxJoursMoisDate * 100);
//             strPcPeriode = String.format("%.2f %%", fPC);

//        int iMaxJoursMoisDate = cToday.getActualMaximum(Calendar.DAY_OF_MONTH);
//

     //   if (iDateJour == 1) iMaxJoursMoisDate = cToday.getActualMaximum(cToday.get(Calendar.MONTH -1));
//
//        int iNbrJourUtilise = iDateJour - 1;
//
//        if (iDateJour == 1) iMaxJoursMoisDate = cToday.getActualMaximum(cToday.get(Calendar.MONTH -1));   ///;
//
////      iNbrJourUtilise = iDateJour - 2;
//        if (iDateJour == 1) iNbrJourUtilise = iMaxJoursMoisDate - 1;
//
////        int iMaxJoursMoisDate = cToday.getActualMaximum(Calendar.DAY_OF_MONTH);

         tv7.setText(strPcPeriode);

      //   NetworkPolicyManager manager = (NetworkPolicyManager) getSystemService("netpolicy");
//           NetworkPolicy[] networkPolicies = manager.getNetworkPolicies();
//           Log.d("NetworkPolicy", "limitBytes is " + networkPolicies[0].limitBytes);
//           Log.d("NetworkPolicy", "warningBytes is " + networkPolicies[0].warningBytes);
//        try
//        {
//
////        Object npm = java.lang.Class.forName("android.net.NetworkPolicyManager").getDeclaredMethod("from", Context.class).invoke(null, this);
////            Class classToInvestigate = Class.forName("android.net.NetworkPolicyManager");
////            Method[] aClassMethods = classToInvestigate.getDeclaredMethods();
////            for(Method m : aClassMethods)
////            {
////                // Found a method m
////                Log.i("BERNIERMETHODES", "getName m " + m.getName());
////                Log.i("BERNIERMETHODES", "Found a method m " + m.isAccessible());
////            }
//        }
//        catch (Exception e)
//        {
//            Log.d("BOBBERNIER", "error is " + e.getMessage());
//            Log.d("BOBBERNIER", "getMessage is " + e.toString());
//        }



        Bitmap bitmap = textAsBitmap(strPC, 40, Color.parseColor("#000000"));
        int iPourcentage =  Math.min( ((int) (fGigd / fdata_limite_max * 100)), 100);
        applyStatusBar("Consommation mobile DATA ", 10, bitmap, iPourcentage, tv3.getText().toString());
        Log.i("BOBBERNIER", "started");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i = new Intent(ConsommationActivity.this, DialogActivity.class);
                i.putExtra(PREF_KEY_DATA_LIMITE_MAX , idata_limite_max);
                i.putExtra(PREF_KEY_DATA_LIMITE_MAX_FLOAT , fdata_limite_max);
                i.putExtra(PREF_KEY_DEBUT_CYCLE , ijour_debut_cycle);
                startActivityForResult(i, REQUESTCHANGEPARAMETERS );
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
    if (requestCode == REQUESTCHANGEPARAMETERS)
    {
        if (resultCode ==   RESULT_OK)
        {
            ijour_debut_cycle = data.getIntExtra( PREF_KEY_DEBUT_CYCLE,6);
            idata_limite_max = data.getIntExtra( PREF_KEY_DATA_LIMITE_MAX,66);
            fdata_limite_max = data.getFloatExtra( PREF_KEY_DATA_LIMITE_MAX_FLOAT,67);
            shf  = getSharedPreferences(PREFS, MODE_PRIVATE);
            shf
                    .edit()
                    .putInt(PREF_KEY_DEBUT_CYCLE, ijour_debut_cycle)
                    .putInt(PREF_KEY_DATA_LIMITE_MAX, idata_limite_max)
                    .putFloat(PREF_KEY_DATA_LIMITE_MAX_FLOAT, fdata_limite_max)
                    .apply();
        }
    }
}

    @Override
    protected void onResume()
    {
        super.onResume();
        if (!hasPermissions()) {
            return;
        }
        shf  = getSharedPreferences(PREFS, MODE_PRIVATE);
        ijour_debut_cycle = shf.getInt(PREF_KEY_DEBUT_CYCLE, 5 );
        idata_limite_max  = shf.getInt(PREF_KEY_DATA_LIMITE_MAX, 55);
        fdata_limite_max  = shf.getFloat(PREF_KEY_DATA_LIMITE_MAX_FLOAT, 56);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Calendar currentDate = Calendar.getInstance();
        calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, ijour_debut_cycle );
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.SECOND, 1);
        calendar.set(Calendar.AM_PM, 0);
        if( calendar.get(Calendar.DAY_OF_MONTH) > currentDate.get(Calendar.DAY_OF_MONTH)) calendar.set(Calendar.MONTH , calendar.get(Calendar.MONTH) - 1);
        if (calendar.get(Calendar.MONTH) == 0) {
            calendar.set(Calendar.MONTH, 12);
            calendar.set(Calendar.YEAR , calendar.get(Calendar.YEAR) - 1);
        }

        tv5.setText(String.valueOf(ijour_debut_cycle));
        float flgRx = getAllRxBytesMobile(this);
        float flgTx = getAllTxBytesMobile(this);
        DecimalFormat d2Format = new DecimalFormat("0.00");
        fGigd = (flgRx + flgTx) / 1024 / 1024 / 1024;
        strGigs = String.format("%.2f GIGS", (fGigd));
        tv1.setText(d2Format.format(flgRx / 1024 / 1024 / 1024) + " Gigs ");
        tv2.setText(d2Format.format(flgTx / 1024 / 1024 / 1024) + " Gigs");
        tv3.setText((d2Format.format((flgRx + flgTx) / 1024 / 1024 / 1024)) + " Gigs");
        fPC =    (fGigd / fdata_limite_max * 100);
        strPC = String.format("%.2f %%", fPC);
        tv6.setText(strPC);
        tv4.setText(String.valueOf(fdata_limite_max));
        Bitmap bitmap = textAsBitmap(strPC, 40, Color.parseColor("#000000"));
        int iPourcentage =  Math.min( ((int) (fGigd / fdata_limite_max * 100)), 100);
        applyStatusBar("Consommation mobile DATA ", 10, bitmap, iPourcentage, tv3.getText().toString());
        Log.i("BOBBERNIER", "started");
    }
    protected void onDestroy() {
        super.onDestroy();
    }

    public long getAllRxBytesMobile(Context context) {

        NetworkStats.Bucket bucket;
        try {
            bucket = networkStatsManager.querySummaryForDevice(ConnectivityManager.TYPE_MOBILE,
                    getSubscriberId(context, ConnectivityManager.TYPE_MOBILE),
                    calendar.getTimeInMillis(),
                    System.currentTimeMillis());
        } catch (RemoteException e) {
            return -1;
        }
        return bucket.getRxBytes();
    }

    public long getAllTxBytesMobile(Context context) {
        NetworkStats.Bucket bucket;
        try {
            bucket = networkStatsManager.querySummaryForDevice(ConnectivityManager.TYPE_MOBILE,
                    getSubscriberId(context, ConnectivityManager.TYPE_MOBILE),
                    calendar.getTimeInMillis(),
                    System.currentTimeMillis());
        } catch (RemoteException e) {
            return -1;
        }
        return bucket.getTxBytes();
    }

    private String getSubscriberId(Context context, int networkType) {
        if (ConnectivityManager.TYPE_MOBILE == networkType) {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return tm.getSubscriberId();
        }
        return "";
    }

    private void applyStatusBar(String sTitle, int notificationId, Bitmap bitmap, int p_pc, String sgigs) {
//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
//                .setSmallIcon(ICONESPOURCENTS_RESSOURCES [p_pc])
//                .setLargeIcon(bitmap)
//                .setContentTitle(sTitle)
//                .setContentInfo("setcontentinfo....")
//                .setContentText("Consommé : " + sgigs)
//                .setPriority(NotificationCompat.PRIORITY_MAX);
//
//        Intent resultIntent = new Intent(this, ConsommationActivity.class);
//        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        mBuilder.setContentIntent(resultPendingIntent);
//        Notification notification = mBuilder.build();
//        notification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT | Notification.PRIORITY_MAX;
//
//        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        mNotifyMgr.notify(notificationId, notification);
        // Create an explicit content Intent that starts the main Activity.
        Intent notificationIntent = new Intent(getApplicationContext(), ConsommationActivity.class);

        // Construct a task stack.
       TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        // Add the main Activity to the task stack as the parent.
        stackBuilder.addParentStack(ConsommationActivity.class);

        // Push the content Intent onto the stack.
      stackBuilder.addNextIntent(notificationIntent);

        // Get a PendingIntent containing the entire back stack.
       PendingIntent notificationPendingIntent =
              stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        java.util.Calendar c = java.util.Calendar.getInstance();
        java.text.SimpleDateFormat dateformat = new java.text.SimpleDateFormat("yyyy-MMM-dd hh:mm:ss aa");
        String datetime = dateformat.format(c.getTime());

        nb = noti.getNotification2(sTitle, "Consommé : " + sgigs);

         nb.setSmallIcon(ICONESPOURCENTS_RESSOURCES [p_pc])
                .setLargeIcon(bitmap)
                .setContentTitle(sTitle)

                 .setContentIntent(notificationPendingIntent)
                .setContentText("Consommé : " + sgigs)
         .setAutoCancel(false)
         ;

            nb.setStyle(new Notification.BigTextStyle().bigText("BigTextStyleTime : " + datetime)

              .setBigContentTitle("BigTextStyleBIG TITLEBigTextStyleBIG TITLEBigTextStyleBIG TITLEBigTextStyleBIG TITLEBigTextStyleBIG TITLEBigTextStyleBIG TITLEBigTextStyleBIG TITLEBigTextStyleBIG TITLEBigTextStyleBIG TITLEBigTextStyleBIG TITLEBigTextStyleBIG TITLEBigTextStyleBIG TITLEBigTextStyleBIG TITLEBigTextStyleBIG TITLE")
            .setSummaryText("SvUMMARY TEXTSvUMMARY TEXTSvUMMARY TEXTSvUMMARY TEXTSvUMMARY TEXTSvUMMARY TEXTSvUMMARY TEXTSvUMMARY TEXTv")
            .bigText("BigTextStyleljljjljlhjlhjlh big textBigTextStyleljljjljlhjlhjlh big textBigTextStyleljljjljlhjlhjlh big textBigTextStyleljljjljlhjlhjlh big textBigTextStyleljljjljlhjlhjlh big textBigTextStyleljljjljlhjlhjlh big textBigTextStyleljljjljlhjlhjlh big textBigTextStyleljljjljlhjlhjlh big textBigTextStyleljljjljlhjlhjlh big textBigTextStyleljljjljlhjlhjlh big textBigTextStyleljljjljlhjlhjlh big textBigTextStyleljljjljlhjlhjlh big textBigTextStyleljljjljlhjlhjlh big textBigTextStyleljljjljlhjlhjlh big textBigTextStyleljljjljlhjlhjlh big textBigTextStyleljljjljlhjlhjlh big textBigTextStyleljljjljlhjlhjlh big textBigTextStyleljljjljlhjlhjlh big textBigTextStyleljljjljlhjlhjlh big textBigTextStyleljljjljlhjlhjlh big textBigTextStyleljljjljlhjlhjlh big textBigTextStyleljljjljlhjlhjlh big textBigTextStyleljljjljlhjlhjlh big textBigTextStyleljljjljlhjlhjlh big textBigTextStyleljljjljlhjlhjlh big textBigTextStyleljljjljlhjlhjlh big textBigTextStyleljljjljlhjlhjlh big text")

            );


             //  .setPriority(NotificationCompat.PRIORITY_MAX);
     //   nb.setContentIntent(notificationPendingIntent);

//        nb.setStyle(new Notification.BigTextStyle().bigText("Time : " + datetime)
//                .setBigContentTitle("BIG TITLE")
//                .setSummaryText("SUMMARY TEXT"));

        if (nb != null) {
            noti.notify(NOTI_PRIMARY2, nb);
        }


    }

    public static Bitmap textAsBitmap(String text, float textSize, int textColor) {
        // adapted from https://stackoverflow.com/a/8799344/1476989
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(text) + 0.0f); // round
        int height = (int) (baseline + paint.descent() + 0.0f);

        int trueWidth = width;
        if (width > height) height = width;
        else width = height;
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(image);
        canvas.drawText(text, width / 2 - trueWidth / 2, baseline, paint);
        return image;
    }

    private void requestPermissions() {
        if (!hasPermissionToReadNetworkHistory()) {
            return;
        }
        if (!hasPermissionToReadPhoneStats()) {
            requestPhoneStateStats();
            return;
        }
    }

    private boolean hasPermissions() {
        return hasPermissionToReadNetworkHistory() && hasPermissionToReadPhoneStats();
    }

    private void requestPhoneStateStats() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, READ_PHONE_STATE_REQUEST);
    }

    private boolean hasPermissionToReadNetworkHistory() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        final AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), getPackageName());
        if (mode == AppOpsManager.MODE_ALLOWED) {
            return true;
        }
        appOps.startWatchingMode(AppOpsManager.OPSTR_GET_USAGE_STATS,
                getApplicationContext().getPackageName(),
                new AppOpsManager.OnOpChangedListener() {
                    @Override
                    @TargetApi(Build.VERSION_CODES.M)
                    public void onOpChanged(String op, String packageName) {
                        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                                android.os.Process.myUid(), getPackageName());
                        if (mode != AppOpsManager.MODE_ALLOWED) {
                            return;
                        }
                        appOps.stopWatchingMode(this);
                        Intent intent = new Intent(ConsommationActivity.this, ConsommationActivity.class);
                        if (getIntent().getExtras() != null) {
                            intent.putExtras(getIntent().getExtras());
                        }
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplicationContext().startActivity(intent);
                    }
                });
        requestReadNetworkHistoryAccess();
        return false;
    }
    private boolean hasPermissionToReadPhoneStats() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_DENIED) {
            return false;
        } else {
            return true;
        }
    }
    private void requestReadNetworkHistoryAccess() {
        this.finish();
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS );
        startActivity(intent);
    }
    private static final int[] ICONESPOURCENTS_RESSOURCES =
            {
                    R.drawable.icopc0,
                    R.drawable.icopc1,
                    R.drawable.icopc2,
                    R.drawable.icopc3,
                    R.drawable.icopc4,
                    R.drawable.icopc5,
                    R.drawable.icopc6,
                    R.drawable.icopc7,
                    R.drawable.icopc8,
                    R.drawable.icopc9,
                    R.drawable.icopc10,
                    R.drawable.icopc11,
                    R.drawable.icopc12,
                    R.drawable.icopc13,
                    R.drawable.icopc14,
                    R.drawable.icopc15,
                    R.drawable.icopc16,
                    R.drawable.icopc17,
                    R.drawable.icopc18,
                    R.drawable.icopc19,
                    R.drawable.icopc20,
                    R.drawable.icopc21,
                    R.drawable.icopc22,
                    R.drawable.icopc23,
                    R.drawable.icopc24,
                    R.drawable.icopc25,
                    R.drawable.icopc26,
                    R.drawable.icopc27,
                    R.drawable.icopc28,
                    R.drawable.icopc29,
                    R.drawable.icopc30,
                    R.drawable.icopc31,
                    R.drawable.icopc32,
                    R.drawable.icopc33,
                    R.drawable.icopc34,
                    R.drawable.icopc35,
                    R.drawable.icopc36,
                    R.drawable.icopc37,
                    R.drawable.icopc38,
                    R.drawable.icopc39,
                    R.drawable.icopc40,
                    R.drawable.icopc41,
                    R.drawable.icopc42,
                    R.drawable.icopc43,
                    R.drawable.icopc44,
                    R.drawable.icopc45,
                    R.drawable.icopc46,
                    R.drawable.icopc47,
                    R.drawable.icopc48,
                    R.drawable.icopc49,
                    R.drawable.icopc50,
                    R.drawable.icopc51,
                    R.drawable.icopc52,
                    R.drawable.icopc53,
                    R.drawable.icopc54,
                    R.drawable.icopc55,
                    R.drawable.icopc56,
                    R.drawable.icopc57,
                    R.drawable.icopc58,
                    R.drawable.icopc59,
                    R.drawable.icopc60,
                    R.drawable.icopc61,
                    R.drawable.icopc62,
                    R.drawable.icopc63,
                    R.drawable.icopc64,
                    R.drawable.icopc65,
                    R.drawable.icopc66,
                    R.drawable.icopc67,
                    R.drawable.icopc68,
                    R.drawable.icopc69,
                    R.drawable.icopc70,
                    R.drawable.icopc71,
                    R.drawable.icopc72,
                    R.drawable.icopc73,
                    R.drawable.icopc74,
                    R.drawable.icopc75,
                    R.drawable.icopc76,
                    R.drawable.icopc77,
                    R.drawable.icopc78,
                    R.drawable.icopc79,
                    R.drawable.icopc80,
                    R.drawable.icopc81,
                    R.drawable.icopc82,
                    R.drawable.icopc83,
                    R.drawable.icopc84,
                    R.drawable.icopc85,
                    R.drawable.icopc86,
                    R.drawable.icopc87,
                    R.drawable.icopc88,
                    R.drawable.icopc89,
                    R.drawable.icopc90,
                    R.drawable.icopc91,
                    R.drawable.icopc92,
                    R.drawable.icopc93,
                    R.drawable.icopc94,
                    R.drawable.icopc95,
                    R.drawable.icopc96,
                    R.drawable.icopc97,
                    R.drawable.icopc98,
                    R.drawable.icopc99,
                    R.drawable.icopc999

            };
}
