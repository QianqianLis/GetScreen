package com.hrgk.getscreen;

import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2016/3/1.
 */

public class FileUtils {

    public static File createImageFile() {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        try {
            File imageFile = File.createTempFile(imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    Environment.getExternalStorageDirectory()      /* directory */);
            return imageFile;
        } catch (IOException e) {
            //do noting
            return null;
        }
    }

}