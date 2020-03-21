package ir.sinadalvand.barnamenevis;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

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

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate");
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
        return binder;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.e(TAG, "onRebind");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e(TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");
    }

    class DownloadBinder extends Binder {

        void startDownload(final String url) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    download(url);
                }
            }).start();
        }

        void setDownloadListener(DownloadListener listener){
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

                    if(downloadListener!=null){
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
}
