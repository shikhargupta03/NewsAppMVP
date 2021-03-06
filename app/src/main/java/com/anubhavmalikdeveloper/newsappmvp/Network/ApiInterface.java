package com.anubhavmalikdeveloper.newsappmvp.Network;

import com.anubhavmalikdeveloper.newsappmvp.Data.Models.NewsModel;
import com.anubhavmalikdeveloper.newsappmvp.Data.Models.SourceWrapper;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface ApiInterface {
    @GET("{type}")
    Call<NewsModel> getGeneralNewsByType(@Header("Authorization") String token
            , @Path("type") String type
            , @QueryMap Map<String, String> options);

    @GET("/sources")
    Call<SourceWrapper> getSources(@Header("Authorization") String token);
    // apparently it doesn't work with country , @Query("country") String country); and returns html.
}
