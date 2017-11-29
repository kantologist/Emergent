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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Adapters.NewsAdapter;
import Models.GetNews;
import Models.News;
import Rest.Api_Client;
import Rest.Api_Interface;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by femi on 5/14/17.
 */

public class NewsFragment extends Fragment{

    @BindView(R.id.news_rv) RecyclerView recyclerView;
    NewsAdapter newsAdapter;
    @BindView(R.id.news_swipe) SwipeRefreshLayout swipeRefreshLayout;
    LinearLayoutManager linearLayoutManager;
    List<News> newses;
    FloatingActionButton fab;
    private String source = "techcrunch";
    private Unbinder unbinder;
    private DatabaseReference mDatabaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstancestate){
        View rootView = inflater.inflate(R.layout.fragment_news, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        Timber.plant(new Timber.DebugTree());
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("news");
        mDatabaseReference.keepSynced(true);
        newses  = new ArrayList<>();
        newsAdapter = new NewsAdapter(getActivity(), newses);
        recyclerView.setAdapter(newsAdapter);
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
                getNewsFromFirebase();
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
    private void getNews() {
        newsAdapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);

    }

//    private List<News> getNews() {
//        Api_Interface api_interface = Api_Client.newsRequestBySource().create(Api_Interface.class);
//        Call<GetNews> call = api_interface.getNews(source,BuildConfig.API_KEY);
//        call.enqueue(new Callback<GetNews>() {
//            @Override
//            public void onResponse(Call<GetNews> call, Response<GetNews> response) {
//                newses = response.body().getArticles();
//                newsAdapter = new NewsAdapter(getActivity(), newses);
//                recyclerView.setAdapter(newsAdapter);
//                swipeRefreshLayout.setRefreshing(false);
//            }
//
//            @Override
//            public void onFailure(Call<GetNews> call, Throwable t) {
//                swipeRefreshLayout.setRefreshing(false);
//                swipeRefreshLayout.setVisibility(View.GONE);
//                call.cancel();
//                Toast.makeText(getActivity(), "Please retry. No internet access", Toast.LENGTH_SHORT).show();
//                Timber.d("Network error: " + t.toString());
//
//            }
//        });
//        return newses;
//    }

    private void getNewsFromFirebase(){

         mDatabaseReference.orderByKey().addChildEventListener(new ChildEventListener() {
             @Override
             public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                 GenericTypeIndicator<List<News>> typeIndicator = new GenericTypeIndicator<List<News>>() {};
                 News news = dataSnapshot.getValue(News.class);
                 newses.add(news);
                 getNews()  ;
             }

             @Override
             public void onChildChanged(DataSnapshot dataSnapshot, String s) {

             }

             @Override
             public void onChildRemoved(DataSnapshot dataSnapshot) {

             }

             @Override
             public void onChildMoved(DataSnapshot dataSnapshot, String s) {

             }

             @Override
             public void onCancelled(DatabaseError databaseError) {
                 Timber.e(databaseError.toException());
                 swipeRefreshLayout.setRefreshing(false);
                 swipeRefreshLayout.setVisibility(View.GONE);
             }
         });
//        mDatabaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                GenericTypeIndicator<List<News>> typeIndicator = new GenericTypeIndicator<List<News>>();
//                List<News> newsList = dataSnapshot.getValue(typeIndicator);
//                getNews(newsList);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Timber.e(databaseError.toException());
//            swipeRefreshLayout.setRefreshing(false);
//            swipeRefreshLayout.setVisibility(View.GONE);
//
//            }
//        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
