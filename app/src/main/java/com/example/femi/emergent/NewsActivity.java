package com.example.femi.emergent;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewsActivity extends AppCompatActivity {

    @BindView(R.id.detail_toolbar) Toolbar toolbar;
    @BindView(R.id.detail_image) ImageView detail_image;
    @BindView(R.id.detail_title) TextView detail_title;
    @BindView(R.id.detail_description) TextView detail_description;
    @BindView(R.id.detail_author) TextView detail_author;
    @BindView(R.id.detail_share) FloatingActionButton fab;
    @BindView(R.id.more) Button more;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        ButterKnife.bind(this);


        final Intent current = getIntent();
        if(current != null && current.hasExtra("urlToImage")){
            Glide.with(getBaseContext())
                    .load(current.getStringExtra("urlToImage"))
                    .into(detail_image);
        }
        if(current != null && current.hasExtra("title")){
            detail_title.setText(current.getStringExtra("title"));
        }
        if(current != null && current.hasExtra("description")){
            detail_description.setText(current.getStringExtra("description"));
        }
        if(current != null && current.hasExtra("author")){
            detail_author.setText(current.getStringExtra("author"));
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
            }
        });

        if (current!=null && current.hasExtra("url") && current.getStringExtra("url") != null){
            more.setVisibility(View.VISIBLE);
            more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(current.getStringExtra("url")));
                    startActivity(intent);

                }
            });

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_TEXT, "#watchman " + current.getStringExtra("url"));
                    startActivity(Intent.createChooser(i, "Share"));

                }
            });

        } else {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_TEXT, "#watchman " + current.getStringExtra("description"));
                    startActivity(Intent.createChooser(i, "Share"));
                }
            });

        }
    }
}
