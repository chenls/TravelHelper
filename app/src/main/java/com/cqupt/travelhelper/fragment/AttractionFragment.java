package com.cqupt.travelhelper.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cqupt.travelhelper.R;
import com.cqupt.travelhelper.adapter.AttractionAdapter;
import com.cqupt.travelhelper.module.Attraction;
import com.cqupt.travelhelper.utils.CommonUtil;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

public class AttractionFragment extends Fragment {

    private AttractionAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int allIndex;
    private List<Attraction> allAttractionList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attraction_list, container, false);
        final PullLoadMoreRecyclerView mPullLoadMoreRecyclerView = (PullLoadMoreRecyclerView)
                view.findViewById(R.id.pullLoadMoreRecyclerView);
        swipeRefreshLayout = mPullLoadMoreRecyclerView.getSwipeRefreshLayout();
        mPullLoadMoreRecyclerView.setGridLayout(2);     //设置网格布局
        adapter = new AttractionAdapter();
        mPullLoadMoreRecyclerView.setAdapter(adapter);
        queryAttraction(false, 0);
        mPullLoadMoreRecyclerView.setOnPullLoadMoreListener(
                new PullLoadMoreRecyclerView.PullLoadMoreListener() {
                    @Override
                    public void onRefresh() {
                        queryAttraction(true, 0);
                        mPullLoadMoreRecyclerView.setPullLoadMoreCompleted();
                    }

                    @Override
                    public void onLoadMore() {
                        queryAttraction(false, allIndex);
                        mPullLoadMoreRecyclerView.setPullLoadMoreCompleted();
                    }
                });
        return view;
    }

    private void queryAttraction(boolean refresh, int index) {

        if (!CommonUtil.checkNetState(getActivity())) {
            swipeRefreshLayout.setRefreshing(false);
            return;
        }
        final BmobQuery<Attraction> bmobQuery = new BmobQuery<>();
        bmobQuery.setSkip(index);
        bmobQuery.setLimit(6);
        bmobQuery.order("updatedAt"); //按更新时间排序
        //先判断是否强制刷新
        if (refresh) {
            bmobQuery.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);        // 强制在从网络中获取
        } else {
            //先判断是否有缓存
            boolean isCache = bmobQuery.hasCachedResult(getActivity(), Attraction.class);
            if (isCache) {
                bmobQuery.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);    // 先从缓存取数据，如果没有的话，再从网络取。
            } else {
                bmobQuery.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);    // 如果没有缓存的话，则先从网络中取
            }
        }
        bmobQuery.findObjects(getActivity(), new FindListener<Attraction>() {

            @Override
            public void onSuccess(List<Attraction> attractionList) {
                allIndex = allIndex + 6;
                allAttractionList.addAll(attractionList);
                adapter.setAttractionList(allAttractionList);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onError(int code, String msg) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}
