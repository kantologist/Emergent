package Rest;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by femi on 5/14/17.
 */

public class ApiClient {

    private static final String BASE_URL = "https://newsapi.org/v1/";
    private static Retrofit retrofit = null;

    public static Retrofit newsRequestBySource(){
        if(retrofit == null){
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            OkHttpClient client = httpClient.build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }

        return retrofit;
    }
}
