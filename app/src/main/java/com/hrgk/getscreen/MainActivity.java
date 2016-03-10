package com.hrgk.getscreen;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;

public class MainActivity extends Activity {

    public final static int SMALL_CAPTURE = 0;
    public final static int BIG_CAPTURE = 1;
    public final static int REQUEST_IMAGE = 2;
    public final static int REQUEST_IMAGE_OLD = 3;

    private Uri outputFileUri;

    private Button btn1;
    private Button btn2;
    private Button btn3;
    private ImageView img;
    private ImageView bigimg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        img = (ImageView) findViewById(R.id.img);
        bigimg = (ImageView) findViewById(R.id.big_img);
        btn1 = (Button) findViewById(R.id.btn_1);
        btn2 = (Button) findViewById(R.id.btn_2);
        btn3 = (Button) findViewById(R.id.btn_3);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, SMALL_CAPTURE);
                }
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = FileUtils.createImageFile();
                outputFileUri = Uri.fromFile(file);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, BIG_CAPTURE);
                }
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    startActivityForResult(intent, REQUEST_IMAGE);
                } else {
                    startActivityForResult(intent, REQUEST_IMAGE_OLD);
                }
            }
        });

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bigimg.setVisibility(View.VISIBLE);
                bigimg.setImageBitmap(PictureUtil
                        .getSmallBitmap(outputFileUri.getPath(), 480, 800));
            }
        });

        bigimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bigimg.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /**
         * 1返回缩略图
         */
        if (resultCode == RESULT_OK && requestCode == SMALL_CAPTURE) {
            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
            img.setImageBitmap(imageBitmap);

            /**
             * 2返回Uri
             */
        } else if (resultCode == RESULT_OK && requestCode == BIG_CAPTURE) {
            img.setImageBitmap(PictureUtil
                    .getSmallBitmap(outputFileUri.getPath(), 480, 800));
            /**
             * 3返回Uri
             */
        } else if (resultCode == RESULT_OK && requestCode == REQUEST_IMAGE) {
            outputFileUri = data.getData();
            Log.i("qqliLog", "outputFileUri:" + outputFileUri);


            img.setImageBitmap(PictureUtil
                    .getSmallBitmap(getRealPathFromURI(outputFileUri), 480, 800));

            Log.i("qqliLog", "realUri:" + getRealPathFromURI(outputFileUri));
        }

    }


    /**
     *
     * android4.4以后返回的URI只有图片编号
     * 获取图片真实路径
     *
     * @param contentURI
     * @return
     */
    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

}