package com.cqupt.travelhelper.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cqupt.travelhelper.R;
import com.cqupt.travelhelper.module.Attraction;

import java.util.List;


public class AttractionAdapter extends RecyclerView.Adapter<AttractionAdapter.ViewHolder> {

    private List<Attraction> attractionList;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_attraction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Attraction attraction = attractionList.get(position);
        final Context context = holder.itemView.getContext();
        Glide.with(context)
                .load(attraction.getPicture().getFileUrl(context))
                .placeholder(R.mipmap.loading)
                .into(holder.picture);
        holder.name.setText(attraction.getName());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, holder.getAdapterPosition() + "",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return attractionList == null ? 0 : attractionList.size();
    }

    public void setAttractionList(List<Attraction> attractionList) {
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
            name = (TextView) view.findViewById(R.id.name);
        }
    }
}
