package com.cqupt.travelhelper.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cqupt.travelhelper.R;
import com.cqupt.travelhelper.adapter.TravelsAdapter;
import com.cqupt.travelhelper.module.MyUser;
import com.cqupt.travelhelper.module.Travels;
import com.cqupt.travelhelper.utils.CommonUtil;
import com.cqupt.travelhelper.utils.DownloadSQLite;
import com.cqupt.travelhelper.utils.ObjectUtil;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

public class TravelsFragment extends Fragment {

    private TravelsAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int allIndex;
    private List<Travels> allTravelsList = new ArrayList<>();
    private boolean isLocal;
    private ArrayList<String> allFileNames = new ArrayList<>();
    private boolean isMine;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_travels_list, container, false);
        final PullLoadMoreRecyclerView mPullLoadMoreRecyclerView = (PullLoadMoreRecyclerView)
                view.findViewById(R.id.pullLoadMoreRecyclerView);
        swipeRefreshLayout = mPullLoadMoreRecyclerView.getSwipeRefreshLayout();
        mPullLoadMoreRecyclerView.setLinearLayout();     //设置线性布局
        adapter = new TravelsAdapter();
        mPullLoadMoreRecyclerView.setAdapter(adapter);
        if (isLocal)
            queryTravelsByLocal(0);
        else
            queryTravels(false, 0);
        mPullLoadMoreRecyclerView.setOnPullLoadMoreListener(
                new PullLoadMoreRecyclerView.PullLoadMoreListener() {
                    @Override
                    public void onRefresh() {
                        if (!isLocal)
                            queryTravels(true, 0);
                        mPullLoadMoreRecyclerView.setPullLoadMoreCompleted();
                    }

                    @Override
                    public void onLoadMore() {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (isLocal)
                                    queryTravelsByLocal(allIndex);
                                else
                                    queryTravels(false, allIndex);
                                mPullLoadMoreRecyclerView.setPullLoadMoreCompleted();
                            }
                        }, 1000); //延时1秒，为了清楚的看到加载过程
                    }
                });
        return view;
    }

    public static TravelsFragment newInstanceMine(boolean isMine) {
        TravelsFragment fragment = new TravelsFragment();
        Bundle args = new Bundle();
        args.putBoolean("isMine", isMine);
        fragment.setArguments(args);
        return fragment;
    }

    public static TravelsFragment newInstanceLocal(boolean isLocal) {
        TravelsFragment fragment = new TravelsFragment();
        Bundle args = new Bundle();
        args.putBoolean("isLocal", isLocal);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isLocal = getArguments().getBoolean("isLocal", false);
            isMine = getArguments().getBoolean("isMine", false);
        }
    }

    private void queryTravelsByLocal(int index) {
        List<String> fileNames = DownloadSQLite.query(getActivity(), "travels", index);
        List<Travels> travelsList = new ArrayList<>();
        for (String fileName : fileNames) {
            travelsList.add((Travels) ObjectUtil.readObjectFromFile(fileName));
        }

        allIndex = allIndex + 8;
        allTravelsList.addAll(travelsList);
        allFileNames.addAll(fileNames);
        if (!travelsList.isEmpty())
            adapter.setTravelsList(allTravelsList, true, allFileNames);
        swipeRefreshLayout.setRefreshing(false);
    }

    private void queryTravels(final boolean refresh, int index) {

        if (!CommonUtil.checkNetState(getActivity())) {
            swipeRefreshLayout.setRefreshing(false);
            return;
        }
        final BmobQuery<Travels> bmobQuery = new BmobQuery<>();
        bmobQuery.setSkip(index);
        bmobQuery.setLimit(8);
        bmobQuery.include("myUser");// 希望在查询游记信息的同时也把发布人的信息查询出来
        if (isMine) {
            MyUser myUser = MyUser.getCurrentUser(getActivity(), MyUser.class);
            bmobQuery.addWhereEqualTo("myUser", myUser);
        }
        bmobQuery.order("createdAt"); //按创建时间排序
        //先判断是否强制刷新
        if (refresh) {
            bmobQuery.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);        // 强制在从网络中获取
        } else {
            //先判断是否有缓存
            boolean isCache = bmobQuery.hasCachedResult(getActivity(), Travels.class);
            if (isCache) {
                bmobQuery.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);    // 先从缓存取数据，如果没有的话，再从网络取。
            } else {
                bmobQuery.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);    // 如果没有缓存的话，则先从网络中取
            }
        }
        bmobQuery.findObjects(getActivity(), new FindListener<Travels>() {

            @Override
            public void onSuccess(List<Travels> travelsList) {
                if (refresh) {
                    allTravelsList.clear();
                    allIndex = 0;
                }
                allIndex = allIndex + 8;
                allTravelsList.addAll(travelsList);
                adapter.setTravelsList(allTravelsList, false, null);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onError(int code, String msg) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}
