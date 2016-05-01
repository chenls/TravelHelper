package com.cqupt.travelhelper.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cqupt.travelhelper.R;
import com.cqupt.travelhelper.module.Comment;
import com.cqupt.travelhelper.module.MyComment;
import com.cqupt.travelhelper.module.MyUser;
import com.cqupt.travelhelper.module.Travels;
import com.cqupt.travelhelper.utils.CommonUtil;
import com.cqupt.travelhelper.utils.DownloadSQLite;
import com.cqupt.travelhelper.utils.ObjectUtil;
import com.cqupt.travelhelper.utils.SDCardHelper;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

public class TravelsDetailsActivity extends AppCompatActivity {
    private Travels travels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travels_details);
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
        travels = bundle.getParcelable("travels");
        assert travels != null;
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TravelsDetailsActivity.this, CommentActivity.class);
                Bundle bundle = new Bundle();
                Comment comment = new Comment(travels.getMyId(), "游记"
                        , "", travels.getPicture());
                bundle.putParcelable("comment", comment);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        assert collapsingToolbarLayout != null;
        collapsingToolbarLayout.setTitle("游记");
        ImageView picture = (ImageView) findViewById(R.id.picture);
        assert picture != null;
        Glide.with(TravelsDetailsActivity.this)
                .load(travels.getPicture().getFileUrl(TravelsDetailsActivity.this))
                .placeholder(R.mipmap.loading)
                .into(picture);
        TextView description = (TextView) findViewById(R.id.description);
        assert description != null;
        description.setText(travels.getDescription());
        MyUser myUser = travels.getMyUser();
        ImageView user_pic = (ImageView) findViewById(R.id.user_pic);
        assert user_pic != null;
        if (myUser != null) {
            Glide.with(TravelsDetailsActivity.this)
                    .load(myUser.getPic().getFileUrl(TravelsDetailsActivity.this))
                    .placeholder(R.mipmap.loading)
                    .into(user_pic);
            TextView user_name = (TextView) findViewById(R.id.user_name);
            assert user_name != null;
            user_name.setText(myUser.getUsername());
        }
        queryMyComment();
    }

    private void queryMyComment() {
        if (!CommonUtil.checkNetState(TravelsDetailsActivity.this)) {
            return;
        }
        final BmobQuery<MyComment> bmobQuery = new BmobQuery<>();
        bmobQuery.setLimit(100);
        bmobQuery.addWhereEqualTo("dishObjectId", travels.getMyId());
        bmobQuery.order("-updatedAt");
        boolean isCache = bmobQuery.hasCachedResult(TravelsDetailsActivity.this, MyComment.class);
        if (isCache) {
            bmobQuery.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);    // 先从缓存取数据，如果没有的话，再从网络取。
        } else {
            bmobQuery.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);    // 如果没有缓存的话，则先从网络中取
        }
        bmobQuery.findObjects(TravelsDetailsActivity.this, new FindListener<MyComment>() {
            TextView comment = (TextView) findViewById(R.id.comment);

            @Override
            public void onSuccess(List<MyComment> myCommentList) {
                SpannableStringBuilder commentValue = new SpannableStringBuilder();
                for (MyComment myComment : myCommentList) {
                    int commentValueLength = commentValue.length();
                    String name = myComment.getName();
                    commentValue.append(String.format("%s：%s分  %s",
                            name, myComment.getStart(), myComment.getComment())).append("\n\n");
                    int endLength = commentValueLength + name.length() + 1;
                    commentValue.setSpan(new ForegroundColorSpan(
                                    getResources().getColor(R.color.colorAccent))
                            , commentValueLength
                            , endLength
                            //setSpan时需要指定的 flag,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE(前后都不包括).
                            , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    commentValue.setSpan(new ForegroundColorSpan(
                                    getResources().getColor(R.color.colorPrimary))
                            , endLength
                            , endLength + 4
                            //setSpan时需要指定的 flag,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE(前后都不包括).
                            , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                int length = commentValue.length();
                if (length > 2) {
                    commentValue.delete(length - 2, length);//删除最后两个\n\n字符
                }
                comment.setText(commentValue);
            }

            @Override
            public void onError(int code, String msg) {
                comment.setText(R.string.no_comment);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share)
                        + travels.toString());
                startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_app)));
                break;
            case R.id.action_download:
                Context context = TravelsDetailsActivity.this;
                String filesDir = SDCardHelper.getSDCardPrivateFilesDir(context, "data");
                if (filesDir != null) {
                    String fileName = filesDir + "/" + travels.hashCode();
                    DownloadSQLite.add(context, "travels", fileName);
                    ObjectUtil.writeObjectToFile(travels, fileName);
                    Toast.makeText(context, "保存成功", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

