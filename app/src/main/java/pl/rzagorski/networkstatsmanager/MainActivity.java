package pl.rzagorski.networkstatsmanager;

import android.*;
import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.drawable.Icon;
import android.icu.text.DateFormat;
import android.icu.text.DecimalFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.*;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.annotation.TargetApi;
import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.RemoteException;
import android.telephony.TelephonyManager;
import android.widget.TextView;
import android.support.v4.content.ContextCompat;
import android.graphics.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.Date;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.ActivityCompat;
import android.content.Intent;
import android.app.PendingIntent;
import android.app.Notification;
import android.app.NotificationManager;
import org.w3c.dom.Text;
import pl.rzagorski.networkstatsmanager.utils.NetworkStatsHelper;

import android.content.BroadcastReceiver;
import android.util.Log;
import android.content.IntentFilter;
import java.lang.Object.*;
 import android.os.Environment;
import java.io.OutputStream;
import java.io.File;
import java.io.FileOutputStream;
import android.provider.MediaStore;

@TargetApi(Build.VERSION_CODES.M)
public class MainActivity extends AppCompatActivity {


    NetworkStatsManager networkStatsManager;
    NetworkStatsHelper networkStatHelper;
    TextView tv1;
    TextView tv2;
    TextView tv3;
    TextView tv4;
    Calendar calendar;
    String strPC;
    String strGigs;
    float fGigd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        setContentView(R.layout.activity_main);
        networkStatsManager = (NetworkStatsManager) getApplicationContext().getSystemService(Context.NETWORK_STATS_SERVICE);
        networkStatHelper = new NetworkStatsHelper(networkStatsManager);
        String strPC;
        tv1 = (TextView) findViewById(R.id.textView);
        tv2 = (TextView) findViewById(R.id.textView2);
        tv3 = (TextView) findViewById(R.id.textView3);
        tv4 = (TextView) findViewById(R.id.textView4);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 2);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.SECOND, 1);
        calendar.set(Calendar.AM_PM, 0);
        tv4.setText("Depuis le" + df.format(calendar));

        float flgRx = getAllRxBytesMobile(this);
        float flgTx = getAllTxBytesMobile(this);
        DecimalFormat d2Format = new DecimalFormat("0.00");
        fGigd = (flgRx + flgTx) / 1024 / 1024 / 1024;
        strGigs = String.format("%.2f GIGS", (fGigd));
        tv1.setText(d2Format.format(flgRx / 1024 / 1024 / 1024) + " Gigs Tx ");
        tv2.setText(d2Format.format(flgTx / 1024 / 1024 / 1024) + " Gigs Rx");
        tv3.setText((d2Format.format((flgRx + flgTx) / 1024 / 1024 / 1024)) + " Gigs");
       strPC = String.format("%.2f %%", (fGigd / 10 * 100));
      //  strPC = String.format("%d%%", (int) (fGigd / 10 * 100));
        Bitmap bitmap = textAsBitmap(strPC, 40, Color.parseColor("#000000"));
//        SaveBitmap(bitmap);

        applyStatusBar("Consommation DATA ", 10, bitmap, (int) (fGigd / 10 * 100), tv3.getText().toString());

        Log.i("BOBBERNIER", "started");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    @Override

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
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(ICONESPOURCENTS_RESSOURCES [p_pc])
                .setLargeIcon(bitmap)
                .setContentTitle(sTitle)
                .setContentInfo("setcontentinfo....")
                .setContentText("Consommé : " + sgigs)
                .setPriority(NotificationCompat.PRIORITY_MAX);

        Intent resultIntent = new Intent(this, MainActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        Notification notification = mBuilder.build();
        notification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT | Notification.PRIORITY_MAX;

        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(notificationId, notification);
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

    public int GetPerCentIcon(int p_TxAll) {
        return ICONESPOURCENTS_RESSOURCES [p_TxAll];
    }


        private static final int[] ICONESPOURCENTS_RESSOURCES =
            {
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
            R.drawable.icopc99
      };

}