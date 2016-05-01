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
import com.cqupt.travelhelper.activity.AttractionDetailsActivity;
import com.cqupt.travelhelper.module.Attraction;
import com.cqupt.travelhelper.utils.DownloadSQLite;
import com.cqupt.travelhelper.utils.SDCardHelper;

import java.util.ArrayList;
import java.util.List;


public class AttractionAdapter extends RecyclerView.Adapter<AttractionAdapter.ViewHolder> {

    private List<Attraction> attractionList;
    private boolean isCanDelete;
    private ArrayList<String> allFileNames;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_attraction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Attraction attraction = attractionList.get(position);
        final Context context = holder.itemView.getContext();
        Glide.with(context)
                .load(attraction.getPicture().getFileUrl(context))
                .placeholder(R.mipmap.loading)
                .into(holder.picture);
        holder.name.setText(attraction.getName());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AttractionDetailsActivity.class);
                Bundle bundle = new Bundle();
                attraction.setMyId(attraction.getObjectId());
                bundle.putParcelable("attraction", attraction);
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
                            attractionList.remove(position);
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
        return attractionList == null ? 0 : attractionList.size();
    }

    public void setAttractionList(List<Attraction> attractionList, boolean isCanDelete
            , ArrayList<String> allFileNames) {
        this.allFileNames = allFileNames;
        this.isCanDelete = isCanDelete;
        this.attractionList = attractionList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView picture;
        public final TextView name;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            picture = (ImageView) view.findViewById(R.id.picture);
            name = (TextView) view.findViewById(R.id.description);
        }
    }
}
