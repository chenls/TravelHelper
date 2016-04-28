package com.cqupt.travelhelper.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cqupt.travelhelper.R;
import com.cqupt.travelhelper.module.Attraction;

public class AttractionDetailsActivity extends AppCompatActivity {
    private CollapsingToolbarLayout collapsingToolbarLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attraction_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        assert toolbar != null;
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        Attraction attraction = bundle.getParcelable("attraction");
        assert attraction != null;
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        collapsingToolbarLayout.setTitle(attraction.getName());
        ImageView picture = (ImageView) findViewById(R.id.picture);
        assert picture != null;
//        picture.setImageResource(R.mipmap.loading);
        Glide.with(AttractionDetailsActivity.this)
                .load(attraction.getPicture().getFileUrl(AttractionDetailsActivity.this))
                .placeholder(R.mipmap.loading)
                .into(picture);
        RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        TextView price = (TextView) findViewById(R.id.price);
        TextView open_time = (TextView) findViewById(R.id.open_time);
        TextView mAbstract = (TextView) findViewById(R.id.mAbstract);
        TextView description = (TextView) findViewById(R.id.description);
        assert ratingBar != null;
        ratingBar.setRating(Float.parseFloat(attraction.getStar()));
        assert price != null;
        price.setText(attraction.getPrice());
        assert open_time != null;
        open_time.setText(attraction.getOpen_time());
        assert mAbstract != null;
        mAbstract.setText(attraction.getmAbstract());
        assert description != null;
        description.setText(attraction.getDescription());
    }
}
