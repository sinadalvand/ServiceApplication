package ir.sinadalvand.barnamenevis;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
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

public class DownloadBoundedService extends Service {

    private final String TAG = "DownloadBoundedService";
    private DownloadBinder binder = new DownloadBinder();
    private DownloadListener downloadListener = null;
    private final int NOTIF_ID = 124;
    private boolean isBounded = false;
    private NotificationManager notifmanager;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate");
        notifmanager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "onBind");
        isBounded = true;
        stopForeground(true);
        return binder;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        isBounded = true;
        stopForeground(true);
        Log.e(TAG, "onRebind");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e(TAG, "onUnbind");
        isBounded = false;
        startForeground(NOTIF_ID, getNotification(0));
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");
    }

    class DownloadBinder extends Binder {

        void startDownload(final String url) {
            startService(new Intent(DownloadBoundedService.this, DownloadBoundedService.class));
            new Thread(new Runnable() {
                @Override
                public void run() {
                    download(url);
                    stopSelf();
                }
            }).start();
        }

        void setDownloadListener(DownloadListener listener) {
            downloadListener = listener;
        }

    }

    interface DownloadListener {
        void downloadedPercent(int percent);
    }

    public void download(String url) {
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

                    if (!isBounded)
                        notifmanager.notify(NOTIF_ID, getNotification(percent));

                    if (downloadListener != null) {
                        downloadListener.downloadedPercent(percent);
                    }
                }
                fos.write(data, 0, count);
            }
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Notification getNotification(int percent) {
        return new NotificationCompat.Builder(this, "CHANEL_ID")
                .setContentTitle("Download Manager")
                .setContentText(String.format("%d Percent Downloaded!", percent))
                .setSmallIcon(R.drawable.ic_download)
                .build();
    }
}
