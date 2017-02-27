package com.example.anish.uiworks2.imagePickUpdemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.anish.uiworks2.Helper;
import com.example.anish.uiworks2.R;
import com.gun0912.tedpermission.PermissionListener;
import com.mlsdev.rximagepicker.RxImageConverters;
import com.mlsdev.rximagepicker.RxImagePicker;
import com.mlsdev.rximagepicker.Sources;

import java.io.File;
import java.util.ArrayList;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func1;

public class ImagePicUpActivity extends AppCompatActivity {
    private ImageView img;
    private Button frmGallery, fromCam;
    private Subscription subscription;
    private File imgfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_pic_up);
        img = (ImageView) findViewById(R.id.img);
        frmGallery = (Button) findViewById(R.id.btnFetchImgGallery);
        fromCam = (Button) findViewById(R.id.btnFetchImgCam);


        frmGallery.setOnClickListener(v -> {
            pickImageFromSource(Sources.GALLERY);
        });
        fromCam.setOnClickListener(v -> {
            pickImageFromSource(Sources.CAMERA);

        });

//        if (RxImagePicker.with(this).getActiveSubscription() != null) {
//            RxImagePicker.with(this).getActiveSubscription().subscribe(this::onImagePicked);
//        }

    }

    private void onImagePicked(Object result) {
        Toast.makeText(this, String.format("Result: %s", result), Toast.LENGTH_LONG).show();
        if (result instanceof Bitmap) {
            img.setImageBitmap((Bitmap) result);
        } else {
            Glide.with(this)
                    .load(result) // works for File or Uri
                    .crossFade()
                    .into(img);
        }
    }


    private void pickImageFromSource(Sources source) {
        Helper.setPermission(ImagePicUpActivity.this,
                new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        pickImage(source);
                    }

                    @Override
                    public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                        Toast.makeText(ImagePicUpActivity.this, "Permission Denied", Toast.LENGTH_SHORT);
                    }
                });

    }

    private void pickImage(Sources source) {
        RxImagePicker.with(this).requestImage(source)
                .flatMap(new Func1<Uri, Observable<File>>() {
                    @Override
                    public Observable<File> call(Uri uri) {
                        return RxImageConverters.uriToFile(ImagePicUpActivity.this, uri, createTempFile());
                    }
                })
                .subscribe(new Subscriber<File>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(File s) {

                        imgfile = s;

//                        loadRoundedImage(s.getAbsolutePath()); // resulting image
                    }
                });
    }


    private File createTempFile() {
        return new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), System.currentTimeMillis() + "_image.jpeg");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }

}


//                    return RxImageConverters.uriToFile(ImagePicUpActivity.this, uri, createTempFile());
//return RxImageConverters.uriToBitmap(ImagePicUpActivity.this, uri);
//                    if () {
//                        return RxImageConverters.uriToFile(MainActivity.this, uri, createTempFile());
//                    }
//                    case R.id.radio_bitmap:
//                            return RxImageConverters.uriToBitmap(MainActivity.this, uri);
//                        default:
//