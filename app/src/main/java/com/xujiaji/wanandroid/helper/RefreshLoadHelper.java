package com.xujiaji.wanandroid.helper;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;

import com.xujiaji.mvvmquick.base.MQQuickAdapter;
import com.xujiaji.wanandroid.RefreshLoadViewModel;
import com.xujiaji.wanandroid.config.C;
import com.xujiaji.wanandroid.model.RefreshLoadModel;
import com.xujiaji.wanandroid.repository.bean.PageBean;
import com.xujiaji.wanandroid.repository.bean.Result;
import com.xujiaji.wanandroid.repository.remote.Net;

/**
 * author: xujiaji
 * created on: 2018/8/7 13:59
 * description:
 */
public class RefreshLoadHelper {
    public static void init(MQQuickAdapter adapter, RecyclerView list) {
        adapter.bindToRecyclerView(list, true);
        EmptyViewHelper.initEmpty(list);
    }


    public static  <T> Observer<RefreshLoadModel<MutableLiveData<Result<PageBean<T>>>>> listener(LifecycleOwner owner, RecyclerView recyclerView, MQQuickAdapter adapter, SwipeRefreshLayout refresh, RefreshLoadViewModel<T> viewModel) {
        return new Observer<RefreshLoadModel<MutableLiveData<Result<PageBean<T>>>>>() {
            @Override
            public void onChanged(@Nullable RefreshLoadModel<MutableLiveData<Result<PageBean<T>>>> mutableLiveDataRefreshLoadModel) {

                if (mutableLiveDataRefreshLoadModel == null) return;
                if (mutableLiveDataRefreshLoadModel.isRefresh) {
                    adapter.setEnableLoadMore(false);
                } else {
                    refresh.setEnabled(false);
                }

                mutableLiveDataRefreshLoadModel.data.observe(owner, new Observer<Result<PageBean<T>>>() {
                    @Override
                    public void onChanged(@Nullable Result<PageBean<T>> pageBeanResult) {
                        refresh.setEnabled(true);
                        refresh.setRefreshing(false);

                        if (pageBeanResult == null && adapter.isLoading()) {
                            adapter.loadMoreFail();
                        } else {
                            adapter.setEnableLoadMore(true);
                            adapter.setLoaded(false);
                        }

                        if (pageBeanResult == null) {
                            EmptyViewHelper.setErrEmpty(recyclerView, null);
                            return;
                        }

                        if (pageBeanResult.getData().isOver()) {
                            adapter.setLoaded(true);
                        }

                        if (mutableLiveDataRefreshLoadModel.isRefresh) {
                            viewModel.getList().clear();
                        }
                        viewModel.getList().addAll(pageBeanResult.getData().getDatas());
                    }
                });
            }
        };
    }
}
