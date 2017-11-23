package com.example.femi.emergent;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import Models.News;
import Utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class AddNewsActivity extends AppCompatActivity {
    @BindView(R.id.add_toolbar) Toolbar toolbar;
    @BindView(R.id.add_submit) Button submit;
    @BindView(R.id.add_cancel) Button cancel;
    @BindView(R.id.news_image) ImageView image;
    @BindView(R.id.edit_title) EditText title;
    @BindView(R.id.edit_description) EditText desc;
    @BindView(R.id.edit_author) EditText author;
    // utilities object
    Utils util = new Utils();
    static final int REQUEST_TAKE_PHOTO = 1;
    private DatabaseReference mDataBasereference;
    private StorageReference mStoragereference;
    private Uri photoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_news);
        ButterKnife.bind(this);
        Timber.plant(new Timber.DebugTree());

        mDataBasereference = FirebaseDatabase.getInstance().getReference();


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitNews();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(util.dispatchTakePictureIntent(getApplicationContext()), REQUEST_TAKE_PHOTO);
            }
        });

    }

    private void setEditingEnabled(boolean enabled) {
        title.setEnabled(enabled);
        desc.setEnabled(enabled);
        author.setEnabled(enabled);
        if (enabled) {
            submit.setVisibility(View.VISIBLE);
            cancel.setVisibility(View.VISIBLE);
        } else {
            submit.setVisibility(View.GONE);
            cancel.setVisibility(View.GONE);
        }
    }

    private void submitNews(){
        final String titleText = title.getText().toString();
        final String description = desc.getText().toString();
        final String author = desc.getText().toString();
        if (photoPath == null ) {
            Toast.makeText(this, "Take a photo first", Toast.LENGTH_SHORT).show();
        } else {
            setEditingEnabled(false);

            Toast.makeText(this, "Posting news", Toast.LENGTH_SHORT).show();
            File photo = new File("" + photoPath);
            mStoragereference = FirebaseStorage.getInstance().getReference("news_photos/" + photo.getName());

            image.setDrawingCacheEnabled(true);
            image.buildDrawingCache();
            Bitmap bitmap = image.getDrawingCache();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            mStoragereference.putBytes(data)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Uri downLoadUrl = taskSnapshot.getDownloadUrl();
                            String imageurl = downLoadUrl.toString();
                            News news = new News(author, titleText, description, " ", imageurl, " ", false);
                            writeNews(news);
                            setEditingEnabled(true);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddNewsActivity.this, "Photo was not added successfully", Toast.LENGTH_SHORT).show();
                        }
                    });

        }


    }

    private void writeNews(News news) {
        mDataBasereference.child("news").push().setValue(news).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "news written successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "news not written successfully", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Timber.e(e);
            }
        });

        Timber.d("transaction successful.");

        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            photoPath = Uri.parse(util.getCurrentPhotoPath());
            Glide.with(getApplicationContext())
                    .load(util.getCurrentPhotoPath())
                    .centerCrop()
                    .into(image);
        }
    }
}
