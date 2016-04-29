package com.cqupt.travelhelper.fragment;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cqupt.travelhelper.R;
import com.cqupt.travelhelper.activity.SetInformationActivity;
import com.cqupt.travelhelper.activity.WelcomeActivity;
import com.cqupt.travelhelper.module.MyUser;

import cn.bmob.v3.datatype.BmobFile;

public class MineFragment extends Fragment {
    private ImageView pic;
    private TextView name;
    private LinearLayout downloadComment;
    private LinearLayout mineDownload;
    private LinearLayout addTravel;
    private LinearLayout settings;
    private LinearLayout about;
    private LinearLayout logout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        pic = (ImageView) view.findViewById(R.id.pic);
        pic.setImageResource(R.mipmap.ic_face);
        name = (TextView) view.findViewById(R.id.name);
        downloadComment = (LinearLayout) view.findViewById(R.id.download_comment);
        downloadComment.setOnClickListener(new MyOnClick());
        mineDownload = (LinearLayout) view.findViewById(R.id.mine_download);
        mineDownload.setOnClickListener(new MyOnClick());
        addTravel = (LinearLayout) view.findViewById(R.id.add_travel);
        addTravel.setOnClickListener(new MyOnClick());
        settings = (LinearLayout) view.findViewById(R.id.settings);
        settings.setOnClickListener(new MyOnClick());
        about = (LinearLayout) view.findViewById(R.id.about);
        about.setOnClickListener(new MyOnClick());
        logout = (LinearLayout) view.findViewById(R.id.logout);
        logout.setOnClickListener(new MyOnClick());
        changeInformation(null);
        pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SetInformationActivity.class);
                getActivity().startActivityForResult(intent, 1);
            }
        });
        return view;
    }

    class MyOnClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.download_comment:
                    Toast.makeText(getActivity(), "download_comment",
                            Toast.LENGTH_SHORT).show();
                    break;
                case R.id.mine_download:
                    Toast.makeText(getActivity(), "mine_download",
                            Toast.LENGTH_SHORT).show();
                    break;
                case R.id.add_travel:
                    break;
                case R.id.settings:
                    break;
                case R.id.about:
                    break;
                case R.id.logout:
                    logoutMethod();
                    break;
            }
        }

        private void logoutMethod() {
            //退出登录
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(getString(R.string.logout));
            builder.setMessage(getString(R.string.sure_logout));
            builder.setPositiveButton(getString(R.string.sure), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    MyUser.logOut(getActivity());
                    Intent intent = new Intent(getActivity(), WelcomeActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
            });
            builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.create().show();
        }
    }

    private void changeInformation(Bitmap bitmap) {
        if (bitmap != null) {
            pic.setImageBitmap(bitmap);
        }
        MyUser myUser = MyUser.getCurrentUser(getActivity(), MyUser.class);
        BmobFile pic = myUser.getPic();
        if (pic != null) {
            String url = pic.getFileUrl(getActivity());
            setFaceImage(url);
        }
        name.setText((String) MyUser.getObjectByKey(getActivity(), "username"));
    }

    private void setFaceImage(String url) {
        Glide.with(getActivity())
                .load(url)
                .centerCrop()
                .placeholder(R.mipmap.ic_face)
                .into(pic);
    }
}
