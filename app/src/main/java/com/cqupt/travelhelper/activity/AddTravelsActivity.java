package com.cqupt.travelhelper.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bmob.BmobProFile;
import com.bmob.btp.callback.UploadListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cqupt.travelhelper.R;
import com.cqupt.travelhelper.module.MyUser;
import com.cqupt.travelhelper.module.Travels;
import com.cqupt.travelhelper.utils.CommonUtil;

import java.io.File;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;

public class AddTravelsActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private ImageView image;
    private File picture_file;
    private static final int GALLERY = 1;
    private static final int CAMERA = 2;
    private static final int PHOTO_ZOOM = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_travels);
        image = (ImageView) findViewById(R.id.image);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(AddTravelsActivity.this);
                builder.setIcon(R.mipmap.select);
                builder.setTitle(AddTravelsActivity.this.getString(R.string.choose));
                final String s[] = new String[]{getString(R.string.gallery),
                        getString(R.string.picture)};
                builder.setSingleChoiceItems(s, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        picture_file = new File(AddTravelsActivity.this.getExternalCacheDir(), System.currentTimeMillis() + ".jpg");
                        switch (which) {
                            case 0:
                                Intent intent = new Intent(Intent.ACTION_PICK, null);
                                intent.setDataAndType(
                                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                        "image/*");
                                startActivityForResult(intent, GALLERY);
                                break;
                            case 1:
                                if (CommonUtil.isExternalStorageWritable()) {
                                    Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    intent2.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(picture_file));
                                    startActivityForResult(intent2, CAMERA);
                                }
                                break;
                        }
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case GALLERY:
                    startPhotoZoom(data.getData());
                    break;
                case CAMERA:
                    startPhotoZoom(Uri.fromFile(picture_file));
                    break;
                case PHOTO_ZOOM:

                    Glide.with(AddTravelsActivity.this)
                            .load(picture_file)
                            .placeholder(R.mipmap.loading)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)//不缓存
                            .into(image);
                    break;
            }
        }
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri uri
     */

    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 4);
        intent.putExtra("aspectY", 3);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 800);
        intent.putExtra("outputY", 600);
        intent.putExtra("return-data", false); //不返回数据
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(picture_file)); //输出路径
        startActivityForResult(intent, PHOTO_ZOOM);
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
            if (!CommonUtil.checkNetState(AddTravelsActivity.this)) {
                return true;
            }
            if (picture_file == null) {
                Toast.makeText(AddTravelsActivity.this, "还没有选择图片", Toast.LENGTH_SHORT).show();
                return true;
            }
            uploadImage();//上传图片
            progressDialog = new ProgressDialog(AddTravelsActivity.this);
            progressDialog.setMessage("上传中...");
            progressDialog.show();
            return true;
        } else if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void uploadImage() {
        BmobProFile.getInstance(AddTravelsActivity.this)
                .upload(picture_file.getAbsolutePath(), new UploadListener() {
                    @Override
                    public void onSuccess(String fileName, String url, BmobFile file) {
                        addTravels(file); //图片绑定到用户上
                    }

                    @Override
                    public void onProgress(int ratio) {
                    }

                    @Override
                    public void onError(int code, String msg) {
                        progressDialog.dismiss();
                        Toast.makeText(AddTravelsActivity.this, "上传图片出错" + code + " " + msg,
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addTravels(BmobFile file) {
        MyUser myUser = BmobUser.getCurrentUser(this, MyUser.class);
        EditText editText = (EditText) findViewById(R.id.editText);
        assert editText != null;
        Travels travels = new Travels(editText.getText().toString().trim(), file, myUser);
        travels.save(AddTravelsActivity.this, new SaveListener() {
            @Override
            public void onSuccess() {
                progressDialog.dismiss();
                Toast.makeText(AddTravelsActivity.this, "发布游记成功", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(int i, String s) {
                progressDialog.dismiss();
                Toast.makeText(AddTravelsActivity.this, "发布游记失败", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
