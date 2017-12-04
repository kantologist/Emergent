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

import models.News;
import utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import icepick.Icepick;
import icepick.State;
import timber.log.Timber;

public class AddNewsActivity extends AppCompatActivity {
    @BindView(R.id.add_toolbar) Toolbar toolbar;
    @BindView(R.id.add_submit) Button submit;
    @BindView(R.id.add_cancel) Button cancel;
    @BindView(R.id.news_image) ImageView image;
    @BindView(R.id.edit_title) EditText title;
    @BindView(R.id.edit_description) EditText desc;
    @BindView(R.id.edit_author) EditText authorView;
    // utilities object
    Utils util = new Utils();
    static final int REQUEST_TAKE_PHOTO = 1;
    private DatabaseReference mDataBasereference;
    private StorageReference mStoragereference;
    @State Uri photoPath;
    @State String currentPhotoPath;
    @State(News.class) News news;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_news);
        ButterKnife.bind(this);
        Timber.plant(new Timber.DebugTree());
        Icepick.restoreInstanceState(this, savedInstanceState);

        if(savedInstanceState != null){
            Glide.with(getApplicationContext())
                    .load(currentPhotoPath)
                    .centerCrop()
                    .into(image);
        }



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
        authorView.setEnabled(enabled);
        if (enabled) {
            submit.setVisibility(View.VISIBLE);
            cancel.setVisibility(View.VISIBLE);
        } else {
            submit.setVisibility(View.GONE);
            cancel.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    private void submitNews(){
        final String titleText = title.getText().toString();
        final String description = desc.getText().toString();
        final String author = authorView.getText().toString();
        if (photoPath == null ) {
            Toast.makeText(this, getString(R.string.photo_first), Toast.LENGTH_SHORT).show();
        } else {
            setEditingEnabled(false);

            Toast.makeText(this, getString(R.string.posting_news), Toast.LENGTH_SHORT).show();
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
                            news = new News(author, titleText, description, " ", imageurl, " ", false);
                            writeNews();
                            setEditingEnabled(true);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddNewsActivity.this, getString(R.string.photo_unsuccessful), Toast.LENGTH_SHORT).show();
                        }
                    });

        }


    }

    private void writeNews() {
        mDataBasereference.child("news").push().setValue(news).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), getString(R.string.news_successful), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.news_unsuccessful), Toast.LENGTH_SHORT).show();
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
            currentPhotoPath = util.getCurrentPhotoPath();
            Glide.with(getApplicationContext())
                    .load(util.getCurrentPhotoPath())
                    .centerCrop()
                    .into(image);
        }
    }
}
