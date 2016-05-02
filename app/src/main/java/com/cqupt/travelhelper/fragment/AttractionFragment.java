package com.cqupt.travelhelper.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cqupt.travelhelper.R;
import com.cqupt.travelhelper.adapter.AttractionAdapter;
import com.cqupt.travelhelper.module.Attraction;
import com.cqupt.travelhelper.utils.CommonUtil;
import com.cqupt.travelhelper.utils.DownloadSQLite;
import com.cqupt.travelhelper.utils.ObjectUtil;
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
    private boolean isLocal;
    private ArrayList<String> allFileNames = new ArrayList<>();
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_attraction_list, container, false);
            final PullLoadMoreRecyclerView mPullLoadMoreRecyclerView = (PullLoadMoreRecyclerView)
                    rootView.findViewById(R.id.pullLoadMoreRecyclerView);
            swipeRefreshLayout = mPullLoadMoreRecyclerView.getSwipeRefreshLayout();
            mPullLoadMoreRecyclerView.setGridLayout(2);     //设置网格布局
            adapter = new AttractionAdapter();
            mPullLoadMoreRecyclerView.setAdapter(adapter);
            if (isLocal)
                queryAttractionByLocal(0);
            else
                queryAttraction(false, 0);
            mPullLoadMoreRecyclerView.setOnPullLoadMoreListener(
                    new PullLoadMoreRecyclerView.PullLoadMoreListener() {
                        @Override
                        public void onRefresh() {
                            if (!isLocal)
                                queryAttraction(true, 0);
                            mPullLoadMoreRecyclerView.setPullLoadMoreCompleted();
                        }

                        @Override
                        public void onLoadMore() {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (isLocal)
                                        queryAttractionByLocal(allIndex);
                                    else
                                        queryAttraction(false, allIndex);
                                    mPullLoadMoreRecyclerView.setPullLoadMoreCompleted();
                                }
                            }, 1000); //延时1秒，为了清楚的看到加载过程
                        }
                    });
        }
        return rootView;
    }

    public static AttractionFragment newInstance(boolean isLocal) {
        AttractionFragment fragment = new AttractionFragment();
        Bundle args = new Bundle();
        args.putBoolean("isLocal", isLocal);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            isLocal = getArguments().getBoolean("isLocal", false);
    }

    private void queryAttractionByLocal(int index) {
        List<String> fileNames = DownloadSQLite.query(getActivity(), "attraction", index);
        List<Attraction> attractionList = new ArrayList<>();
        for (String fileName : fileNames) {
            attractionList.add((Attraction) ObjectUtil.readObjectFromFile(fileName));
        }
        allIndex = allIndex + 8;
        allFileNames.addAll(fileNames);
        allAttractionList.addAll(attractionList);
        if (!attractionList.isEmpty())
            adapter.setAttractionList(allAttractionList, true, allFileNames);
        swipeRefreshLayout.setRefreshing(false);
    }

    private void queryAttraction(final boolean refresh, int index) {

        if (!CommonUtil.checkNetState(getActivity())) {
            swipeRefreshLayout.setRefreshing(false);
            return;
        }
        final BmobQuery<Attraction> bmobQuery = new BmobQuery<>();
        bmobQuery.setSkip(index);
        bmobQuery.setLimit(8);
        bmobQuery.order("createdAt"); //按创建时间排序
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
                if (refresh) {
                    allAttractionList.clear();
                    allIndex = 0;
                }
                allIndex = allIndex + 8;
                allAttractionList.addAll(attractionList);
                adapter.setAttractionList(allAttractionList, false, null);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onError(int code, String msg) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (null != rootView) {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }
    }
}
