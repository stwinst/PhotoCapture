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


    ImageView mImageView ,mImageViewGray ,mImageViewCanny;

    private static final String TAG = "ProcessActivity";




    static {
        if(!OpenCVLoader.initDebug()){
            Log.d(TAG, "OpenCV not loaded");
        } else {
            Log.d(TAG, "OpenCV loaded");
        }
    }

    Mat tmp ;
    Mat gtmp ;
    Mat ctmp ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process);

        mImageView = (ImageView) findViewById(R.id.imageView);
        mImageViewGray = (ImageView) findViewById(R.id.imageViewGray);
        mImageViewCanny = (ImageView) findViewById(R.id.imageViewCanny);

        Intent intent= getIntent();
        Bundle b = intent.getExtras();





        if(b!=null)
        {
            String j =(String) b.get("strName");
            Log.d("ProcessActivity",j);

            Uri fileUri = Uri.parse(j);
            mImageView.setImageURI(fileUri);

            final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath());

            tmp = new Mat();
            gtmp = new Mat();

            convertTogray(bitmap);
            convertTocanny(gtmp);
        }




    }

    private void convertTogray(Bitmap bmp) {


        Utils.bitmapToMat(bmp,tmp);
        Imgproc.cvtColor(tmp, gtmp, Imgproc.COLOR_BGR2GRAY);

        Bitmap img = Bitmap.createBitmap(gtmp.cols(), gtmp.rows(),Bitmap.Config.ARGB_8888);

        Utils.matToBitmap(gtmp, img);
        mImageViewGray.setImageBitmap(img);

    }

    private void convertTocanny(Mat m){
        Imgproc.Canny(m ,gtmp ,100,200);

        Bitmap imgCanny = Bitmap.createBitmap(gtmp.cols(), gtmp.rows(),Bitmap.Config.ARGB_8888);

        Utils.matToBitmap(gtmp, imgCanny);
        mImageViewGray.setImageBitmap(imgCanny);


    }
}
