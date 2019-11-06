package com.example.camdetect;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.Bitmap;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView imageview;
    private TextView i_b,i_g,i_r;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         i_b = this.findViewById(R.id.i_b);
         i_g = this.findViewById(R.id.i_g);
         i_r = this.findViewById(R.id.i_r);
        imageview = this.findViewById(R.id.imageview);
        imageview.setVisibility(View.INVISIBLE);

        imageview.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int r,g,b;
                //int[] viewCoords = new int[2];
                //imageview.getLocationOnScreen(viewCoords);
                int  h1= imageview.getHeight() ;
                int w1 = imageview.getWidth();
                int x = Math.round(event.getX());
                int y = Math.round(event.getY());
                //int imageX = x - viewCoords[0];
                //int imageY = y - viewCoords[1];
                ImageView image = ((ImageView)v);
                Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
                int w = bitmap.getWidth();
                int h = bitmap.getHeight();
                x = (w*x)/w1;
                y = (h*y)/h1;
                r = Color.red(bitmap.getPixel(x,y));
                g = Color.green(bitmap.getPixel(x,y));
                b = Color.blue(bitmap.getPixel(x,y));
                i_r.setText(String.valueOf(r));
                i_g.setText(String.valueOf(g));
                i_b.setText(String.valueOf(b));
                return true;
            }
        });

        Button opencam = this.findViewById(R.id.opencam);
        opencam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageview.setVisibility(View.VISIBLE);
                Intent takePictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent,REQUEST_IMAGE_CAPTURE);
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK)
        {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap)extras.get("data");
            imageview.setImageBitmap(imageBitmap);
        }
    }
}
