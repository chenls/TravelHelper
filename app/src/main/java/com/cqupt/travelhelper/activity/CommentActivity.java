package com.cqupt.travelhelper.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cqupt.travelhelper.R;
import com.cqupt.travelhelper.module.Attraction;
import com.cqupt.travelhelper.module.MyComment;
import com.cqupt.travelhelper.module.MyUser;
import com.cqupt.travelhelper.utils.CommonUtil;

import cn.bmob.v3.listener.SaveListener;


public class CommentActivity extends AppCompatActivity {

    private Attraction attraction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        attraction = bundle.getParcelable("attraction");

        ImageView picture = (ImageView) findViewById(R.id.picture);
        assert picture != null;
        Glide.with(CommentActivity.this)
                .load(attraction.getPicture().getFileUrl(CommentActivity.this))
                .placeholder(R.mipmap.loading)
                .into(picture);
        TextView tv_dish_name = (TextView) findViewById(R.id.tv_title);
        assert tv_dish_name != null;
        tv_dish_name.setText(attraction.getName());
        TextView tv_price = (TextView) findViewById(R.id.tv_price);
        assert tv_price != null;
        tv_price.setText(attraction.getPrice());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        ActionBar action = getSupportActionBar();
        if (action != null) {
            action.setDisplayHomeAsUpEnabled(true);
            action.setHomeAsUpIndicator(R.mipmap.ic_clear);
        }
        getMenuInflater().inflate(R.menu.done, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.done) {
            if (!CommonUtil.checkNetState(CommentActivity.this)) {
                return true;
            }
            final ProgressDialog progressDialog = new ProgressDialog(CommentActivity.this);
            progressDialog.setMessage("请稍等...");
            progressDialog.show();
            EditText comment = (EditText) findViewById(R.id.comment);
            RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);
            assert comment != null;
            assert ratingBar != null;
            @SuppressLint("DefaultLocale") MyComment myComment = new MyComment(attraction.getMyId()
                    , MyUser.getObjectByKey(CommentActivity.this, "username") + ""
                    , comment.getText().toString().trim()
                    , String.format("%.1f", ratingBar.getRating()));
            myComment.save(CommentActivity.this, new SaveListener() {
                @Override
                public void onSuccess() {
                    progressDialog.dismiss();
                    Toast.makeText(CommentActivity.this, "评论成功", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onFailure(int i, String s) {
                    progressDialog.dismiss();
                    Toast.makeText(CommentActivity.this, "评论失败", Toast.LENGTH_SHORT).show();
                }
            });
            return true;
        } else if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}