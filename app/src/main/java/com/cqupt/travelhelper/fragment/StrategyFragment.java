package com.cqupt.travelhelper.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cqupt.travelhelper.R;
import com.cqupt.travelhelper.adapter.StrategyAdapter;
import com.cqupt.travelhelper.module.Strategy;
import com.cqupt.travelhelper.utils.CommonUtil;
import com.cqupt.travelhelper.utils.DownloadSQLite;
import com.cqupt.travelhelper.utils.ObjectUtil;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

public class StrategyFragment extends Fragment {

    private StrategyAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int allIndex;
    private List<Strategy> allStrategyList = new ArrayList<>();
    private boolean isLocal;
    private ArrayList<String> allFileNames =new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_strategy_list, container, false);
        final PullLoadMoreRecyclerView mPullLoadMoreRecyclerView = (PullLoadMoreRecyclerView)
                view.findViewById(R.id.pullLoadMoreRecyclerView);
        swipeRefreshLayout = mPullLoadMoreRecyclerView.getSwipeRefreshLayout();
        mPullLoadMoreRecyclerView.setGridLayout(2);     //设置网格布局
        adapter = new StrategyAdapter();
        mPullLoadMoreRecyclerView.setAdapter(adapter);
        if (isLocal)
            queryStrategyByLocal(0);
        else
            queryStrategy(false, 0);
        mPullLoadMoreRecyclerView.setOnPullLoadMoreListener(
                new PullLoadMoreRecyclerView.PullLoadMoreListener() {
                    @Override
                    public void onRefresh() {
                        if (!isLocal)
                            queryStrategy(true, 0);
                        mPullLoadMoreRecyclerView.setPullLoadMoreCompleted();
                    }

                    @Override
                    public void onLoadMore() {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (isLocal)
                                    queryStrategyByLocal(allIndex);
                                else
                                    queryStrategy(false, allIndex);
                                mPullLoadMoreRecyclerView.setPullLoadMoreCompleted();
                            }
                        }, 1000); //延时1秒，为了清楚的看到加载过程
                    }
                });
        return view;
    }

    public static StrategyFragment newInstance(boolean isLocal) {
        StrategyFragment fragment = new StrategyFragment();
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

    private void queryStrategyByLocal(int index) {
        List<String> fileNames = DownloadSQLite.query(getActivity(), "strategy", index);
        List<Strategy> strategyList = new ArrayList<>();
        for (String fileName : fileNames) {
            strategyList.add((Strategy) ObjectUtil.readObjectFromFile(fileName));
        }
        allIndex = allIndex + 8;
        allStrategyList.addAll(strategyList);
        allFileNames.addAll(fileNames);
        if (!strategyList.isEmpty())
            adapter.setStrategyList(allStrategyList, true, allFileNames);
        swipeRefreshLayout.setRefreshing(false);
    }

    private void queryStrategy(final boolean refresh, int index) {

        if (!CommonUtil.checkNetState(getActivity())) {
            swipeRefreshLayout.setRefreshing(false);
            return;
        }
        final BmobQuery<Strategy> bmobQuery = new BmobQuery<>();
        bmobQuery.setSkip(index);
        bmobQuery.setLimit(8);
        bmobQuery.order("createdAt"); //按创建时间排序
        //先判断是否强制刷新
        if (refresh) {
            bmobQuery.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);        // 强制在从网络中获取
        } else {
            //先判断是否有缓存
            boolean isCache = bmobQuery.hasCachedResult(getActivity(), Strategy.class);
            if (isCache) {
                bmobQuery.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);    // 先从缓存取数据，如果没有的话，再从网络取。
            } else {
                bmobQuery.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);    // 如果没有缓存的话，则先从网络中取
            }
        }
        bmobQuery.findObjects(getActivity(), new FindListener<Strategy>() {

            @Override
            public void onSuccess(List<Strategy> strategyList) {
                if (refresh) {
                    allStrategyList.clear();
                    allIndex = 0;
                }
                allIndex = allIndex + 8;
                allStrategyList.addAll(strategyList);
                adapter.setStrategyList(allStrategyList, false, null);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onError(int code, String msg) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}
