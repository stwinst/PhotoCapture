package com.stwinst.photocapture;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

public class ProcessActivity extends AppCompatActivity {


    ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process);

        mImageView = (ImageView) findViewById(R.id.imageView);

        Intent intent= getIntent();
        Bundle b = intent.getExtras();

        if(b!=null)
        {
            String j =(String) b.get("strName");
            Log.d("ProcessActivity",j);

            Uri fileUri = Uri.parse(j);
            mImageView.setImageURI(fileUri);
        }


    }
}
