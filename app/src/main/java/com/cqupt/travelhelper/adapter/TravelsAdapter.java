package com.cqupt.travelhelper.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import java.util.List;


public class TravelsAdapter extends RecyclerView.Adapter<TravelsAdapter.ViewHolder> {

    private List<Travels> travelsList;

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
        Glide.with(context)
                .load(myUser.getPic().getFileUrl(context))
                .placeholder(R.mipmap.loading)
                .into(holder.user_pic);
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
    }

    @Override
    public int getItemCount() {
        return travelsList == null ? 0 : travelsList.size();
    }

    public void setTravelsList(List<Travels> travelsList) {
        this.travelsList = travelsList;
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
