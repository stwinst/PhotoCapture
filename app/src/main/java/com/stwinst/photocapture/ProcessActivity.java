package com.stwinst.photocapture;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SeekBar;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class ProcessActivity extends AppCompatActivity {


    ImageView mImageView ,mImageViewGray ,mImageViewCanny ,mImageViewContours;

    private SeekBar mseekbarMin ,mseekbarMax;

    private static final String TAG = "ProcessActivity";

    private static final int MAX_CANNY = 255;
    private static final int MIN_CANNY = 70 ;

    private int mMaxCanny = 110 ,mMinCanny = 70 ;




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


        mImageViewGray = (ImageView) findViewById(R.id.imageViewGray);
        mImageViewCanny = (ImageView) findViewById(R.id.imageViewCanny);
        mImageViewContours = (ImageView) findViewById(R.id.imageViewContours);

        mseekbarMin = (SeekBar) findViewById(R.id.seekBarmin);
        mseekbarMax = (SeekBar) findViewById(R.id.seekBarmax);

        mseekbarMax.setMax(MAX_CANNY);


        mseekbarMin.setMax(120);


        Intent intent= getIntent();
        Bundle b = intent.getExtras();





        if(b!=null)
        {
            String j =(String) b.get("strName");
            Log.d("ProcessActivity",j);

            Uri fileUri = Uri.parse(j);
            //mImageView.setImageURI(fileUri);

            final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath());

            tmp = new Mat();
            gtmp = new Mat();

            convertTogray(bitmap);
            convertTocanny(gtmp);
            drawContours(gtmp);
        }



        mseekbarMax.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){


            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                         if(progress>100) {
                             mMaxCanny = progress;
                         }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                convertTocanny(gtmp);

            }
        });

        mseekbarMin.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){


            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                     if(progress>60) {
                         mMinCanny = progress;
                     }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                convertTocanny(gtmp);
            }
        });


    }

    private void drawContours(Mat m) {



        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

        MatOfPoint2f approxCurve = new MatOfPoint2f();

        Mat hierarchy = new Mat();
        Mat src = m;
// find contours:
        Imgproc.findContours(m, contours, hierarchy, Imgproc.RETR_TREE,Imgproc.CHAIN_APPROX_SIMPLE);
        for (int contourIdx = 0; contourIdx < contours.size(); contourIdx++) {
            Imgproc.drawContours(tmp, contours, contourIdx, new Scalar(0, 0, 255), -1);
        }

        //For each contour found
        for (int i=0; i<contours.size(); i++)
        {
            //Convert contours(i) from MatOfPoint to MatOfPoint2f
            MatOfPoint2f contour2f = new MatOfPoint2f( contours.get(i).toArray() );
            //Processing on mMOP2f1 which is in type MatOfPoint2f
            double approxDistance = Imgproc.arcLength(contour2f, true)*0.02;
            Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true);

            //Convert back to MatOfPoint
            MatOfPoint points = new MatOfPoint( approxCurve.toArray() );

            // Get bounding rect of contour
            Rect rect = Imgproc.boundingRect(points);
            Log.d(TAG,"x"+Integer.toString(rect.x));
            Log.d(TAG,"y"+Integer.toString(rect.y));


            // draw enclosing rectangle (all same color, but you could use variable i to make them unique)

            Imgproc.rectangle(tmp,new Point(rect.x,rect.y),new Point(rect.x+rect.width,rect.y+rect.height),new Scalar(255,0,255), 3);

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
        Imgproc.blur(m,m,new Size(5,5));

        //canny algorithm
        Imgproc.Canny(m ,gtmp ,mMinCanny,mMaxCanny);

        Bitmap imgCanny = Bitmap.createBitmap(gtmp.cols(), gtmp.rows(),Bitmap.Config.ARGB_8888);

        Utils.matToBitmap(gtmp, imgCanny);
        mImageViewCanny.setImageBitmap(imgCanny);


    }
}
