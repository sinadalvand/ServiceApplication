package ir.sinadalvand.barnamenevis;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

public class SecondActivity extends AppCompatActivity implements DownloadBoundedService.DownloadListener {

    private DownloadBoundedService.DownloadBinder binder = null;
    private Boolean isBounded = false;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (DownloadBoundedService.DownloadBinder) service;
            binder.setDownloadListener(SecondActivity.this);
            isBounded = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBounded = false;
            binder = null;
        }
    };


    ProgressBar progressBar;
    Button startDownload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        progressBar = findViewById(R.id.progressbar);
        startDownload = findViewById(R.id.startButton);

        startDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binder != null)
                    binder.startDownload("http://dl.barato.ir/app/QuickPic_5.0.0(BaraTo.iR).apk");
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, DownloadBoundedService.class);
        bindService(intent, connection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isBounded)
            unbindService(connection);
    }

    @Override
    public void downloadedPercent(int percent) {
        progressBar.setProgress(percent);
    }
}