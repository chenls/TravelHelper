package com.cqupt.travelhelper.activity;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.cqupt.travelhelper.R;
import com.cqupt.travelhelper.adapter.SearchAdapter;
import com.cqupt.travelhelper.module.Attraction;
import com.cqupt.travelhelper.module.Strategy;
import com.cqupt.travelhelper.module.Travels;
import com.cqupt.travelhelper.utils.CommonUtil;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

public class SearchActivity extends AppCompatActivity {
    private SearchAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    Map<Integer, Object> searchMap = new HashMap<>();
    int index; //位置索引
    int flag;  //数据加载状态标志

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        final PullLoadMoreRecyclerView mPullLoadMoreRecyclerView = (PullLoadMoreRecyclerView)
                findViewById(R.id.pullLoadMoreRecyclerView);
        assert mPullLoadMoreRecyclerView != null;
        mPullLoadMoreRecyclerView.setPushRefreshEnable(false); //不要上拉刷新
        mPullLoadMoreRecyclerView.setPullRefreshEnable(false); //不要下拉刷新
        swipeRefreshLayout = mPullLoadMoreRecyclerView.getSwipeRefreshLayout();
        mPullLoadMoreRecyclerView.setLinearLayout(); //设置线性布局
        adapter = new SearchAdapter(SearchActivity.this);
        mPullLoadMoreRecyclerView.setAdapter(adapter);
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
                    if (!CommonUtil.checkNetState(SearchActivity.this)) {
                        return true;
                    }
                    search(newText);
                    swipeRefreshLayout.setRefreshing(true);
                }
                return true;
            }

            private void search(String newText) {
                flag = 0;
                index = 0;
                searchMap.clear();
                queryAttraction(newText);
                queryTravels(newText);
                queryStrategy(newText);
            }
        });
        return true;
    }

    private void queryAttraction(String newText) {

        final BmobQuery<Attraction> bmobQuery = new BmobQuery<>();
        bmobQuery.setLimit(2);
        bmobQuery.addWhereContains("description", newText);
        bmobQuery.order("createdAt"); //按创建时间排序
        //先判断是否有缓存
        boolean isCache = bmobQuery.hasCachedResult(SearchActivity.this, Attraction.class);
        if (isCache) {
            bmobQuery.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);    // 先从缓存取数据，如果没有的话，再从网络取。
        } else {
            bmobQuery.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);    // 如果没有缓存的话，则先从网络中取
        }
        bmobQuery.findObjects(SearchActivity.this, new FindListener<Attraction>() {

            @Override
            public void onSuccess(List<Attraction> attractionList) {
                if (!attractionList.isEmpty()) {
                    flag = flag + 11;
                    searchMap.put(index++, "相关景点");
                    for (Attraction attraction : attractionList) {
                        searchMap.put(index++, attraction);
                    }
                    searchMap.put(index++, "查看更多景点");
                } else
                    flag = flag + 1;
                setData();
            }

            @Override
            public void onError(int code, String msg) {
                flag = flag + 1;
                setData();
            }
        });
    }

    private void queryTravels(String newText) {

        final BmobQuery<Travels> bmobQuery = new BmobQuery<>();
        bmobQuery.setLimit(2);
        bmobQuery.addWhereContains("description", newText);
        bmobQuery.include("myUser");// 希望在查询游记信息的同时也把发布人的信息查询出来
        bmobQuery.order("createdAt"); //按创建时间排序
        //先判断是否有缓存
        boolean isCache = bmobQuery.hasCachedResult(SearchActivity.this, Travels.class);
        if (isCache) {
            bmobQuery.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);    // 先从缓存取数据，如果没有的话，再从网络取。
        } else {
            bmobQuery.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);    // 如果没有缓存的话，则先从网络中取
        }
        bmobQuery.findObjects(SearchActivity.this, new FindListener<Travels>() {

            @Override
            public void onSuccess(List<Travels> travelsList) {
                if (!travelsList.isEmpty()) {
                    flag = flag + 11;
                    searchMap.put(index++, "相关游记");
                    for (Travels travels : travelsList) {
                        searchMap.put(index++, travels);
                    }
                    searchMap.put(index++, "查看更多游记");
                } else
                    flag = flag + 1;
                setData();
            }

            @Override
            public void onError(int code, String msg) {
                flag = flag + 1;
                setData();
            }
        });
    }

    private void queryStrategy(String newText) {

        final BmobQuery<Strategy> bmobQuery = new BmobQuery<>();
        bmobQuery.setLimit(2);
        bmobQuery.addWhereContains("description", newText);
        bmobQuery.order("createdAt"); //按创建时间排序
        //先判断是否有缓存
        boolean isCache = bmobQuery.hasCachedResult(SearchActivity.this, Strategy.class);
        if (isCache) {
            bmobQuery.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);    // 先从缓存取数据，如果没有的话，再从网络取。
        } else {
            bmobQuery.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);    // 如果没有缓存的话，则先从网络中取
        }
        bmobQuery.findObjects(SearchActivity.this, new FindListener<Strategy>() {

            @Override
            public void onSuccess(List<Strategy> strategyList) {
                if (!strategyList.isEmpty()) {
                    flag = flag + 11;
                    searchMap.put(index++, "相关攻略");
                    for (Strategy strategy : strategyList) {
                        searchMap.put(index++, strategy);
                    }
                    searchMap.put(index++, "查看更多攻略");
                } else
                    flag = flag + 1;
                setData();
            }

            @Override
            public void onError(int code, String msg) {
                flag = flag + 1;
                setData();
            }
        });
    }

    /*
    flag 个位为3时，表示加载完毕，十位大于0时，表示有数据
     */
    private void setData() {
        Log.d("myLog", flag + " size " + searchMap.size());
        if (flag % 10 == 3) {
            swipeRefreshLayout.setRefreshing(false);
            if (flag / 10 > 0)
                adapter.setSearchMap(searchMap);
            else
                Toast.makeText(SearchActivity.this, "世界这么大，换个内容再试试", Toast.LENGTH_SHORT).show();
        }
    }
}
