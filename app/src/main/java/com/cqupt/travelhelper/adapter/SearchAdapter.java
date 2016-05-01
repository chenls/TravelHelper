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
import com.cqupt.travelhelper.activity.AttractionDetailsActivity;
import com.cqupt.travelhelper.activity.StrategyDetailsActivity;
import com.cqupt.travelhelper.activity.TravelsDetailsActivity;
import com.cqupt.travelhelper.module.Attraction;
import com.cqupt.travelhelper.module.MyUser;
import com.cqupt.travelhelper.module.Strategy;
import com.cqupt.travelhelper.module.Travels;

import java.util.Map;


public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Map<Integer, Object> searchMap;
    private Context mContext;
    private final int TYPE_TITLE = 0;
    private final int TYPE_ATTRACTION = 1;
    private final int TYPE_TRAVELS = 2;
    private final int TYPE_STRATEGY = 3;
    private final int TYPE_BUTTON = 4;

    public SearchAdapter(Context mContext) {
        this.mContext = mContext;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ATTRACTION) {
            return new AttractionViewHolder(
                    LayoutInflater.from(mContext).inflate(R.layout.fragment_attraction, parent, false));
        } else if (viewType == TYPE_TRAVELS) {
            return new TravelsViewHolder(
                    LayoutInflater.from(mContext).inflate(R.layout.fragment_travles, parent, false));
        } else if (viewType == TYPE_STRATEGY) {
            return new StrategyViewHolder(
                    LayoutInflater.from(mContext).inflate(R.layout.fragment_strategy, parent, false));
        } else if (viewType == TYPE_TITLE) {
            return new TitleViewHolder(
                    LayoutInflater.from(mContext).inflate(R.layout.search_title, parent, false));
        } else if (viewType == TYPE_BUTTON) {
            return new ButtonViewHolder(
                    LayoutInflater.from(mContext).inflate(R.layout.search_button, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof AttractionViewHolder) {
            final Attraction attraction = (Attraction) searchMap.get(position);
            final Context context = holder.itemView.getContext();
            Glide.with(context)
                    .load(attraction.getPicture().getFileUrl(context))
                    .placeholder(R.mipmap.loading)
                    .into(((AttractionViewHolder) holder).picture);
            ((AttractionViewHolder) holder).name.setText(attraction.getName());

            ((AttractionViewHolder) holder).mView.setOnClickListener(new View.OnClickListener() {
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
        } else if (holder instanceof TravelsViewHolder) {
            final Travels travels = (Travels) searchMap.get(position);
            final MyUser myUser = travels.getMyUser();
            final Context context = holder.itemView.getContext();
            Glide.with(context)
                    .load(travels.getPicture().getFileUrl(context))
                    .placeholder(R.mipmap.loading)
                    .into(((TravelsViewHolder) holder).picture);
            ((TravelsViewHolder) holder).description.setText(travels.getDescription());
            if (myUser != null) {
                Glide.with(context)
                        .load(myUser.getPic().getFileUrl(context))
                        .placeholder(R.mipmap.loading)
                        .into(((TravelsViewHolder) holder).user_pic);
                ((TravelsViewHolder) holder).user_name.setText(myUser.getUsername());
            }
            ((TravelsViewHolder) holder).mView.setOnClickListener(new View.OnClickListener() {
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
        } else if (holder instanceof StrategyViewHolder) {
            final Strategy strategy = (Strategy) searchMap.get(position);
            final Context context = holder.itemView.getContext();
            Glide.with(context)
                    .load(strategy.getPicture().getFileUrl(context))
                    .placeholder(R.mipmap.loading)
                    .into(((StrategyViewHolder) holder).picture);
            ((StrategyViewHolder) holder).name.setText(strategy.getName());
            ((StrategyViewHolder) holder).mView.setOnClickListener(new View.OnClickListener() {
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
        } else if (holder instanceof TitleViewHolder) {
            ((TitleViewHolder) holder).title.setText((String) searchMap.get(position));
        } else if (holder instanceof ButtonViewHolder) {
            ((ButtonViewHolder) holder).button.setText((String) searchMap.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return searchMap == null ? 0 : searchMap.size();
    }

    @Override
    public int getItemViewType(int position) {
        Object object = searchMap.get(position);
        if (object instanceof Attraction) {
            return TYPE_ATTRACTION;
        } else if (object instanceof Travels) {
            return TYPE_TRAVELS;
        } else if (object instanceof Strategy) {
            return TYPE_STRATEGY;
        } else {
            if (object instanceof String)
                if (((String) object).contains("查看更多"))
                    return TYPE_BUTTON;
        }
        return TYPE_TITLE;
    }

    public void setSearchMap(Map<Integer, Object> searchMap) {
        this.searchMap = searchMap;
        notifyDataSetChanged();
    }

    public class AttractionViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView picture;
        public final TextView name;

        public AttractionViewHolder(View view) {
            super(view);
            mView = view;
            picture = (ImageView) view.findViewById(R.id.picture);
            name = (TextView) view.findViewById(R.id.description);
        }
    }

    public class TravelsViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView picture, user_pic;
        public final TextView description, user_name;

        public TravelsViewHolder(View view) {
            super(view);
            mView = view;
            picture = (ImageView) view.findViewById(R.id.picture);
            description = (TextView) view.findViewById(R.id.description);
            user_pic = (ImageView) view.findViewById(R.id.user_pic);
            user_name = (TextView) view.findViewById(R.id.user_name);
        }
    }

    public class StrategyViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView picture;
        public final TextView name;

        public StrategyViewHolder(View view) {
            super(view);
            mView = view;
            picture = (ImageView) view.findViewById(R.id.picture);
            name = (TextView) view.findViewById(R.id.description);
        }
    }

    public class TitleViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView title;

        public TitleViewHolder(View view) {
            super(view);
            mView = view;
            title = (TextView) view.findViewById(R.id.title);
        }
    }

    public class ButtonViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView button;

        public ButtonViewHolder(View view) {
            super(view);
            mView = view;
            button = (TextView) view.findViewById(R.id.button);
        }
    }
}
