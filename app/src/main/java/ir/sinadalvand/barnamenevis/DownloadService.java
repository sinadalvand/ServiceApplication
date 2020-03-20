package ir.sinadalvand.barnamenevis;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public class DownloadService extends Service {

    public static String Url_KEY = "URL_KEY";
    private final int NOTIF_ID = 124;
    private final String CHANEL_ID = "DOWNLOAD_CHANNEL";
    private NotificationManager notifmanager;

    @Override
    public void onCreate() {
        super.onCreate();
        notifmanager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Log.e("DownloadService", "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String url = "";
        if (intent.getExtras() != null) {
            url = intent.getExtras().getString(Url_KEY);
        }

        Log.e("DownloadService", url);

        final String finalUrl = url;

        new Thread(new Runnable() {
            @Override
            public void run() {


                startForeground(NOTIF_ID, getNotification(0));
                download(finalUrl, "");
                stopForeground(false);
//                stopSelf();


            }
        }).start();


        return START_REDELIVER_INTENT;
    }

    public void download(String url, String path) {
        try {
            URL u = new URL(url);
            URLConnection conn = u.openConnection();
            int contentLength = conn.getContentLength();
            DataInputStream stream = new DataInputStream(u.openStream());
            String filePath = Environment.getExternalStorageDirectory().getPath().toString() + "/app.apk";
            File f = new File(filePath);
            DataOutputStream fos = new DataOutputStream(new FileOutputStream(f, false));
            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while ((count = stream.read(data)) != -1) {
                total += count;
                if (contentLength > 0) {
                    int percent = ((int) (total * 100 / contentLength));
                    Log.e("Downloader", "Percent: " + percent);

                    notifmanager.notify(NOTIF_ID, getNotification(percent));

                }
                fos.write(data, 0, count);
            }
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.e("DownloadService", "DownloadService");

    }

    private Notification getNotification(int percent) {
        return new NotificationCompat.Builder(this, CHANEL_ID)
                .setContentTitle("Download Manager")
                .setContentText(String.format("%d Percent Downloaded!", percent))
                .setSmallIcon(R.drawable.ic_download)
                .build();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
