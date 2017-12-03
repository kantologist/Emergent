package Rest;

import android.os.Build;
import android.support.compat.BuildConfig;

import java.util.List;

import Models.GetNews;
import Models.News;
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
