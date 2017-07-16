package com.stwinst.photocapture;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class ProcessActivity extends AppCompatActivity {


    ImageView mImageView ,mImageViewGray;

    private static final String TAG = "ProcessActivity";


    static {
        if(!OpenCVLoader.initDebug()){
            Log.d(TAG, "OpenCV not loaded");
        } else {
            Log.d(TAG, "OpenCV loaded");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process);

        mImageView = (ImageView) findViewById(R.id.imageView);
        mImageViewGray = (ImageView) findViewById(R.id.imageViewGray);


        Intent intent= getIntent();
        Bundle b = intent.getExtras();





        if(b!=null)
        {
            String j =(String) b.get("strName");
            Log.d("ProcessActivity",j);

            Uri fileUri = Uri.parse(j);
            mImageView.setImageURI(fileUri);

            final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath());

            convertTogray(bitmap);
        }




    }

    private void convertTogray(Bitmap bmp) {

        Mat tmp = new Mat();
        Mat gtmp = new Mat();
        Utils.bitmapToMat(bmp,tmp);
        Imgproc.cvtColor(tmp, gtmp, Imgproc.COLOR_BGR2GRAY);

        Bitmap img = Bitmap.createBitmap(gtmp.cols(), gtmp.rows(),Bitmap.Config.ARGB_8888);

        Utils.matToBitmap(gtmp, img);
        mImageViewGray.setImageBitmap(img);

    }
}
