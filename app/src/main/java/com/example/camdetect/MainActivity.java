package com.example.camdetect;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.Bitmap;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView imageview;
    private TextView i_b,i_g,i_r;
    Uri imageUri;
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

                if((ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)  != PackageManager.PERMISSION_GRANTED))
                {
                    if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE))
                    {
                        Toast.makeText(MainActivity.this, "For Better Image", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_IMAGE_CAPTURE);
                    }
                }
                else
                {
                    MainActivity.this.capture();
                }

            }
        });

    }

    private void capture() {

        imageview.setVisibility(View.VISIBLE);
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent takePictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent,REQUEST_IMAGE_CAPTURE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case REQUEST_IMAGE_CAPTURE:
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    capture();
                }
                else
                {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK)
        {
           try
            {
               Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imageUri);
               imageview.setImageBitmap(imageBitmap);
            }
           catch (IOException e)
           {
               e.printStackTrace();
           }
        }
    }

    private String saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDirg
        File directory = cw.getExternalFilesDir("ImageDir");
        // Create imageDir
        //File filepath = Environment.getExternalStorageDirectory();
        //File directory = cw.getFilesDir("imageDir",Context.MODE_PRIVATE);
        File mypath=new File(directory,"profile.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
             bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        loadImageFromStorage(directory.getAbsolutePath());
        return directory.getAbsolutePath();
    }
    private void loadImageFromStorage(String path)
    {

        try {
            File f=new File(path, "profile.jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            imageview.setImageBitmap(b);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

    }
}
