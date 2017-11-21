package com.example.femi.emergent;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import Utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AddNewsActivity extends AppCompatActivity {
    @BindView(R.id.add_toolbar) Toolbar toolbar;
    @BindView(R.id.add_submit) Button submit;
    @BindView(R.id.add_cancel) Button cancel;
    @BindView(R.id.news_image) ImageView image;
    // utilities object
    Utils util = new Utils();
    static final int REQUEST_TAKE_PHOTO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_news);
        ButterKnife.bind(this);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            Glide.with(getApplicationContext())
                    .load(util.getCurrentPhotoPath())
                    .centerCrop()
                    .into(image);
        }
    }
}
