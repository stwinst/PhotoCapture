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
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class ProcessActivity extends AppCompatActivity {


    ImageView mImageView ,mImageViewGray ,mImageViewCanny ,mImageViewContours;

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
        mImageViewContours = (ImageView) findViewById(R.id.imageViewContours);

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
            drawContours(gtmp);
        }




    }

    private void drawContours(Mat m) {


        //downsampling
        //Imgproc.pyrDown(m ,m);
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();
        Mat src = m;
// find contours:
        Imgproc.findContours(m, contours, hierarchy, Imgproc.RETR_TREE,Imgproc.CHAIN_APPROX_SIMPLE);
        for (int contourIdx = 0; contourIdx < contours.size(); contourIdx++) {
            Imgproc.drawContours(tmp, contours, contourIdx, new Scalar(0, 0, 255), -1);
        }

        Bitmap imgContours = Bitmap.createBitmap(m.cols(), m.rows(),Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(tmp, imgContours);
        mImageViewContours.setImageBitmap(imgContours);
    }

    private void convertTogray(Bitmap bmp) {


        Utils.bitmapToMat(bmp,tmp);
        Imgproc.pyrDown(tmp ,tmp);
        Imgproc.cvtColor(tmp, gtmp, Imgproc.COLOR_BGR2GRAY);

        Bitmap img = Bitmap.createBitmap(gtmp.cols(), gtmp.rows(),Bitmap.Config.ARGB_8888);

        Utils.matToBitmap(gtmp, img);
        mImageViewGray.setImageBitmap(img);

    }

    private void convertTocanny(Mat m){

        //blur image
        Imgproc.blur(m,m,new Size(3,3));

        //canny algorithm
        Imgproc.Canny(m ,gtmp ,80,150);

        Bitmap imgCanny = Bitmap.createBitmap(gtmp.cols(), gtmp.rows(),Bitmap.Config.ARGB_8888);

        Utils.matToBitmap(gtmp, imgCanny);
        mImageViewCanny.setImageBitmap(imgCanny);


    }
}
