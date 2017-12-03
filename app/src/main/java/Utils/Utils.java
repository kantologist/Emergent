package Utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import com.example.femi.emergent.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by femi on 09/06/2017.
 */

public class Utils {
    String mCurrentPhotoPath;

    private File createImageFile(Context context) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public Intent dispatchTakePictureIntent(Context context) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null){
            File photoFile = null;
            try{
                photoFile = createImageFile(context);
            } catch (IOException e) {
                Toast.makeText(context, context.getString(R.string.file_not_created), Toast.LENGTH_SHORT).show();
            }

            if(photoFile != null ){
                Uri photoURI = FileProvider.getUriForFile(context,
                        "com.example.android.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

            }
        }
        return takePictureIntent;
    }

    public String getCurrentPhotoPath(){
        return this.mCurrentPhotoPath;
    }
}
