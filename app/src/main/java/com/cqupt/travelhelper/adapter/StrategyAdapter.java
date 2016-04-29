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
import com.cqupt.travelhelper.activity.StrategyDetailsActivity;
import com.cqupt.travelhelper.module.Strategy;

import java.util.List;


public class StrategyAdapter extends RecyclerView.Adapter<StrategyAdapter.ViewHolder> {

    private List<Strategy> strategyList;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_strategy, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Strategy strategy = strategyList.get(position);
        final Context context = holder.itemView.getContext();
        Glide.with(context)
                .load(strategy.getPicture().getFileUrl(context))
                .placeholder(R.mipmap.loading)
                .into(holder.picture);
        holder.name.setText(strategy.getName());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, StrategyDetailsActivity.class);
                Bundle bundle = new Bundle();
                strategy.setMyId(strategy.getObjectId());
                bundle.putParcelable("strategy", strategy);
                intent.putExtras(bundle);
                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return strategyList == null ? 0 : strategyList.size();
    }

    public void setStrategyList(List<Strategy> strategyList) {
        this.strategyList = strategyList;
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
