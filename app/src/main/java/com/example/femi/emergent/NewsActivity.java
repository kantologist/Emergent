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

public class NewsActivity extends AppCompatActivity {

    Toolbar toolbar;
    ImageView detail_image;
    TextView detail_title;
    TextView detail_description;
    TextView detail_author;
    FloatingActionButton fab;
    Button more;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        detail_image = (ImageView) findViewById(R.id.detail_image);
        detail_title = (TextView) findViewById(R.id.detail_title);
        detail_description = (TextView) findViewById(R.id.detail_description);
        detail_author = (TextView) findViewById(R.id.detail_author);

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

        toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
            }
        });
        fab = (FloatingActionButton) findViewById(R.id.detail_share);
        more  = (Button) findViewById(R.id.more);

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
