package ir.sinadalvand.barnamenevis;

import androidx.appcompat.app.AppCompatActivity;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentService = new Intent(MainActivity.this,DownloadService.class);
                intentService.putExtra(DownloadService.Url_KEY,"http://dl.barato.ir/app/QuickPic_5.0.0(BaraTo.iR).apk");
                startService(intentService);

            }
        });

        Intent intentService = new Intent(MainActivity.this,DownloadService.class);
        stopService(intentService);




    }
}