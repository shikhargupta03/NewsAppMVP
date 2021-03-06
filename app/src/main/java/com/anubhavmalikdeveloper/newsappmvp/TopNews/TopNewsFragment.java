package com.anubhavmalikdeveloper.newsappmvp.TopNews;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.airbnb.lottie.LottieAnimationView;
import com.anubhavmalikdeveloper.newsappmvp.AllNews.AllNewsAdapter;
import com.anubhavmalikdeveloper.newsappmvp.Base.BaseFragment;
import com.anubhavmalikdeveloper.newsappmvp.Callbacks.GeneralNewsInterface;
import com.anubhavmalikdeveloper.newsappmvp.Data.Models.Article;
import com.anubhavmalikdeveloper.newsappmvp.Data.Models.NewsModel;
import com.anubhavmalikdeveloper.newsappmvp.Network.ApiClient;
import com.anubhavmalikdeveloper.newsappmvp.Network.ApiInterface;
import com.anubhavmalikdeveloper.newsappmvp.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TopNewsFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener
        , CompoundButton.OnCheckedChangeListener
        , TopNewsContract.View
        , GeneralNewsInterface {

    @BindView(R.id.rv_main)
    RecyclerView rvMain;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.toggle_trending)
    SwitchCompat toggleSwitch;

    @BindView(R.id.lottie_view)
    LottieAnimationView lottieAnimationView;

    private Context mContext;
    private TopNewsPresenter presenter;
    private boolean isAccordingToPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_trending, null, false);
        ButterKnife.bind(this, view);

        initViewClicks();
        initDataHelpers();

        setAdapter(provideEmptyNewsModel());

        presenter.loadData(isAccordingToPreferences, false, true);

        return view;
    }

    private NewsModel provideEmptyNewsModel() {
        NewsModel newsModel = new NewsModel();
        newsModel.setTotalResults(0);
        newsModel.setArticles(new ArrayList<Article>());
        newsModel.setStatus("ok");

        return newsModel;
    }

    private void initDataHelpers() {
        ApiClient apiClient = ApiClient.getInstance();
        initPresenter(apiClient.createService(ApiInterface.class));
    }

    private void initPresenter(ApiInterface apiInterface) {
        presenter = new TopNewsPresenter(this, apiInterface);
    }

    private void initViewClicks() {
        swipeRefreshLayout.setOnRefreshListener(this);
        toggleSwitch.setOnCheckedChangeListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onRefresh() {
        presenter.loadData(isAccordingToPreferences, false, true);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        isAccordingToPreferences = b;
        setAdapter(provideEmptyNewsModel());
        presenter.loadData(isAccordingToPreferences, false, true);
    }

    @Override
    public void setRefreshing(boolean refreshing) {
        swipeRefreshLayout.setRefreshing(refreshing);
    }

    @Override
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void showData(@NonNull NewsModel newsModel) {
        rvMain.setVisibility(View.VISIBLE);
        setAdapter(newsModel);
    }

    private void setAdapter(NewsModel newsModel) {
        AllNewsAdapter allNewsAdapter = new AllNewsAdapter(mContext, newsModel, this);
        rvMain.setAdapter(allNewsAdapter);
        rvMain.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
    }

    @Override
    public void showProgress(boolean status) {
        if (status) {
            setRefreshing(false);
            lottieAnimationView.setAnimation("newspaperAnimation.json");
            lottieAnimationView.setVisibility(View.VISIBLE);
            lottieAnimationView.playAnimation();
            lottieAnimationView.loop(true);
        } else {
            setRefreshing(false);
            lottieAnimationView.setVisibility(View.GONE);
            lottieAnimationView.cancelAnimation();
        }
    }

    @Override
    public void showSnackBar(String message, int duration) {
        Snackbar.make(rvMain, message, duration).show();
    }

    @Override
    public void showEmptyPlaceholder() {
        rvMain.setVisibility(View.GONE);
        lottieAnimationView.setAnimation("error.json");
        lottieAnimationView.setVisibility(View.VISIBLE);
        lottieAnimationView.playAnimation();
        lottieAnimationView.loop(true);
    }

    @Override
    public void redirectToNewsSource(String url) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    @Override
    public void loadMore() {
        presenter.loadData(isAccordingToPreferences, true, false);
    }
}


