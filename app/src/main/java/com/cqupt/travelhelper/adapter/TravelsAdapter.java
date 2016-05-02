package com.cqupt.travelhelper.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cqupt.travelhelper.R;
import com.cqupt.travelhelper.activity.TravelsDetailsActivity;
import com.cqupt.travelhelper.module.MyUser;
import com.cqupt.travelhelper.module.Travels;
import com.cqupt.travelhelper.utils.DownloadSQLite;
import com.cqupt.travelhelper.utils.SDCardHelper;

import java.util.ArrayList;
import java.util.List;


public class TravelsAdapter extends RecyclerView.Adapter<TravelsAdapter.ViewHolder> {

    private List<Travels> travelsList;
    private ArrayList<String> allFileNames;
    private boolean isCanDelete;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_travles, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Travels travels = travelsList.get(position);
        final MyUser myUser = travels.getMyUser();
        final Context context = holder.itemView.getContext();
        Glide.with(context)
                .load(travels.getPicture().getFileUrl(context))
                .placeholder(R.mipmap.loading)
                .into(holder.picture);
        holder.description.setText(travels.getDescription());
        if (myUser.getPic() != null) {
            Glide.with(context)
                    .load(myUser.getPic().getFileUrl(context))
                    .placeholder(R.mipmap.loading)
                    .into(holder.user_pic);
        }
        holder.user_name.setText(myUser.getUsername());
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, TravelsDetailsActivity.class);
                Bundle bundle = new Bundle();
                travels.setMyId(travels.getObjectId());
                bundle.putParcelable("travels", travels);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
        if (isCanDelete) {
            holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    deleteMethod();
                    return true;
                }

                private void deleteMethod() {
                    //删除对话框
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(context.getString(R.string.delete));
                    builder.setMessage(context.getString(R.string.sure_delete));
                    builder.setPositiveButton(context.getString(R.string.sure), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //删除
                            int position = holder.getLayoutPosition();
                            String fileName = allFileNames.get(position);
                            DownloadSQLite.delete(context, fileName);
                            SDCardHelper.deleteFile(fileName);
                            travelsList.remove(position);
                            notifyItemRemoved(position);
                        }
                    });
                    builder.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    builder.create().show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return travelsList == null ? 0 : travelsList.size();
    }

    public void setTravelsList(List<Travels> travelsList, boolean isCanDelete, ArrayList<String> allFileNames) {
        this.travelsList = travelsList;
        this.allFileNames = allFileNames;
        this.isCanDelete = isCanDelete;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView picture, user_pic;
        public final TextView description, user_name;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            picture = (ImageView) view.findViewById(R.id.picture);
            description = (TextView) view.findViewById(R.id.description);
            user_pic = (ImageView) view.findViewById(R.id.user_pic);
            user_name = (TextView) view.findViewById(R.id.user_name);
        }
    }
}
