package com.example.vsmwatchandroidapplication.ui.logging;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.view.View;
import android.widget.Toast;
import com.example.vsmwatchandroidapplication.R;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class ShareActivity extends AppCompatActivity {
    private String stringFile = Environment.getExternalStorageDirectory().getPath() + File.separator + "Test.pdf";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive);
        ActivityCompat.requestPermissions(this, new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        findViewById(R.id.UploadButton).setOnClickListener(view -> buttonShareText());
    }


    public void buttonShareFile(View view){
        File file = new File(stringFile);
        if (!file.exists()){
            Toast.makeText(this, "File doesn't exists", Toast.LENGTH_LONG).show();
            return;
        }
        Intent intentShare = new Intent(Intent.ACTION_SEND);
        intentShare.setType("application/pdf");
        intentShare.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+file));
        startActivity(Intent.createChooser(intentShare, "Share the file ..."));
    }

    public void buttonShareText(){
        Intent intentShare = new Intent(Intent.ACTION_SEND);
        intentShare.setType("text/plain");
        intentShare.putExtra(Intent.EXTRA_SUBJECT,"My Subject Here ... ");
        intentShare.putExtra(Intent.EXTRA_TEXT,"My Text of the message goes here ... write anything what you want");

        startActivity(Intent.createChooser(intentShare, "Shared the text ..."));
    }
}
