package com.cqupt.travelhelper.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.cqupt.travelhelper.R;
import com.cqupt.travelhelper.adapter.AttractionAdapter;
import com.cqupt.travelhelper.module.Attraction;
import com.cqupt.travelhelper.utils.CommonUtil;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

public class SearchActivity extends AppCompatActivity {
    private AttractionAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int allIndex;
    private List<Attraction> allAttractionList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Map map =new HashMap();
        map.put("1",1);
        map.put("2","2");
        final PullLoadMoreRecyclerView mPullLoadMoreRecyclerView = (PullLoadMoreRecyclerView)
                findViewById(R.id.pullLoadMoreRecyclerView);
        assert mPullLoadMoreRecyclerView != null;
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
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                queryAttraction(false, allIndex);
                                mPullLoadMoreRecyclerView.setPullLoadMoreCompleted();
                            }
                        }, 1000); //延时1秒，为了清楚的看到加载过程
                    }
                });
    }

    private void queryAttraction(final boolean refresh, int index) {

        if (!CommonUtil.checkNetState(SearchActivity.this)) {
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
            boolean isCache = bmobQuery.hasCachedResult(SearchActivity.this, Attraction.class);
            if (isCache) {
                bmobQuery.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);    // 先从缓存取数据，如果没有的话，再从网络取。
            } else {
                bmobQuery.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);    // 如果没有缓存的话，则先从网络中取
            }
        }
        bmobQuery.findObjects(SearchActivity.this, new FindListener<Attraction>() {

            @Override
            public void onSuccess(List<Attraction> attractionList) {
                if (refresh) {
                    allAttractionList.clear();
                    allIndex = 0;
                }
                allIndex = allIndex + 8;
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        final MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        assert searchView != null;
        searchView.setIconified(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)) {
//                    queryDish(newText);
                    //TODO
                    swipeRefreshLayout.setProgressViewOffset(false, 0, 40);
                    swipeRefreshLayout.setRefreshing(true);
                }
                return true;
            }
        });
        return true;
    }
}
