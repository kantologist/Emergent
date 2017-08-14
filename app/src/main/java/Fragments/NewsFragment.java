package Fragments;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.femi.emergent.AddNewsActivity;
import com.example.femi.emergent.BuildConfig;
import com.example.femi.emergent.NewsActivity;
import com.example.femi.emergent.R;

import java.util.ArrayList;
import java.util.List;

import Adapters.NewsAdapter;
import Models.GetNews;
import Models.News;
import Rest.Api_Client;
import Rest.Api_Interface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by femi on 5/14/17.
 */

public class NewsFragment extends Fragment{

    RecyclerView recyclerView;
    NewsAdapter newsAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    LinearLayoutManager linearLayoutManager;
    List<News> newses;
    FloatingActionButton fab;
    private String source = "techcrunch";
    private static final String LOG_TAG = NewsFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstancestate){
        View rootView = inflater.inflate(R.layout.fragment_news, container, false);
        newses  = new ArrayList<>();
        recyclerView = (RecyclerView) rootView.findViewById(R.id.news_rv);
        newsAdapter = new NewsAdapter(getActivity(), newses);
        recyclerView.setAdapter(newsAdapter);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.news_swipe);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                getNews();
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), AddNewsActivity.class);
                startActivity(i);
            }
        });

        recyclerView.addOnItemTouchListener(new NewsAdapter.RecycleTouchListener(getContext(), recyclerView, new NewsAdapter.ClickListener(){

            @Override
            public void onClick(View view, int position) throws ClassNotFoundException {
                Intent intent = new Intent(getContext(), NewsActivity.class);
                intent.putExtra("urlToImage", newses.get(position).getUrlToImage());
                intent.putExtra("title", newses.get(position).getTitle());
                intent.putExtra("description", newses.get(position).getDescription());
                intent.putExtra("author", newses.get(position).getAuthor());
                intent.putExtra("url", newses.get(position).getUrl());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        return rootView;
    }

    private List<News> getNews() {
        Api_Interface api_interface = Api_Client.newsRequestBySource().create(Api_Interface.class);
        Call<GetNews> call = api_interface.getNews(source,BuildConfig.API_KEY);
        call.enqueue(new Callback<GetNews>() {
            @Override
            public void onResponse(Call<GetNews> call, Response<GetNews> response) {
                newses = response.body().getArticles();
                newsAdapter = new NewsAdapter(getActivity(), newses);
                recyclerView.setAdapter(newsAdapter);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<GetNews> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                swipeRefreshLayout.setVisibility(View.GONE);
                call.cancel();
                Toast.makeText(getActivity(), "Please retry. No internet access", Toast.LENGTH_SHORT).show();
                Log.i(LOG_TAG, "Network error: " + t.toString());

            }
        });
        return newses;
    }
}
