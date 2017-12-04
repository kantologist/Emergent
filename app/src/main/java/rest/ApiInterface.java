package rest;

import models.GetNews;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


/**
 * Created by user on 5/14/17.
 */

public interface ApiInterface {

    @GET("articles")
    Call<GetNews> getNews(
            @Query("source") String source,
            @Query("apiKey") String api_key);
}
