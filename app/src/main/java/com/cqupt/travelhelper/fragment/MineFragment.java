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

import com.bumptech.glide.Glide;
import com.cqupt.travelhelper.R;
import com.cqupt.travelhelper.activity.AboutActivity;
import com.cqupt.travelhelper.activity.AddTravelsActivity;
import com.cqupt.travelhelper.activity.DownloadCommentActivity;
import com.cqupt.travelhelper.activity.MyTravelsActivity;
import com.cqupt.travelhelper.activity.SetInformationActivity;
import com.cqupt.travelhelper.activity.SettingsActivity;
import com.cqupt.travelhelper.activity.WelcomeActivity;
import com.cqupt.travelhelper.module.MyUser;

import cn.bmob.v3.datatype.BmobFile;

public class MineFragment extends Fragment {
    private ImageView pic;
    private TextView name;
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_mine, container, false);
            pic = (ImageView) rootView.findViewById(R.id.pic);
            pic.setImageResource(R.mipmap.ic_face);
            name = (TextView) rootView.findViewById(R.id.description);
            LinearLayout downloadComment = (LinearLayout) rootView.findViewById(R.id.download_comment);
            downloadComment.setOnClickListener(new MyOnClick());
            LinearLayout mineTravel = (LinearLayout) rootView.findViewById(R.id.mine_travel);
            mineTravel.setOnClickListener(new MyOnClick());
            LinearLayout addTravel = (LinearLayout) rootView.findViewById(R.id.add_travel);
            addTravel.setOnClickListener(new MyOnClick());
            LinearLayout settings = (LinearLayout) rootView.findViewById(R.id.settings);
            settings.setOnClickListener(new MyOnClick());
            LinearLayout about = (LinearLayout) rootView.findViewById(R.id.about);
            about.setOnClickListener(new MyOnClick());
            LinearLayout logout = (LinearLayout) rootView.findViewById(R.id.logout);
            logout.setOnClickListener(new MyOnClick());
            changeInformation(null, null);
            pic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), SetInformationActivity.class);
                    getActivity().startActivityForResult(intent, 1);
                }
            });
        }
        return rootView;
    }

    class MyOnClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.download_comment:
                    Intent intent0 = new Intent(getActivity(), DownloadCommentActivity.class);
                    startActivity(intent0);
                    break;
                case R.id.mine_travel:
                    Intent intent1 = new Intent(getActivity(), MyTravelsActivity.class);
                    startActivity(intent1);
                    break;
                case R.id.add_travel:
                    Intent intent2 = new Intent(getActivity(), AddTravelsActivity.class);
                    startActivity(intent2);
                    break;
                case R.id.settings:
                    Intent intent3 = new Intent(getActivity(), SettingsActivity.class);
                    startActivity(intent3);
                    break;
                case R.id.about:
                    Intent intent4 = new Intent(getActivity(), AboutActivity.class);
                    startActivity(intent4);
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

    public void changeInformation(Bitmap bitmap, String nameValue) {
        MyUser myUser = MyUser.getCurrentUser(getActivity(), MyUser.class);
        if (bitmap != null) {
            pic.setImageBitmap(bitmap);
        } else {
            BmobFile pic = myUser.getPic();
            if (pic != null) {
                String url = pic.getFileUrl(getActivity());
                setFaceImage(url);
            }
        }
        if (nameValue != null)
            name.setText(nameValue);
        else
            name.setText((String) MyUser.getObjectByKey(getActivity(), "username"));
    }

    private void setFaceImage(String url) {
        Glide.with(getActivity())
                .load(url)
                .centerCrop()
                .placeholder(R.mipmap.loading)
                .into(pic);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (null != rootView) {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }
    }
}
