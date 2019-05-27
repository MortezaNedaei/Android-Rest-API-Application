package com.nedaei1375.json_place_holder_rest_api_example.Rest;

import com.nedaei1375.json_place_holder_rest_api_example.Model.AlbumData;
import com.nedaei1375.json_place_holder_rest_api_example.Model.PhotosData;
import com.nedaei1375.json_place_holder_rest_api_example.Model.UserData;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface ApiInterface {


    @GET("albums")
    Call<List<AlbumData>> getAlbumData();

    @GET("users")
    Call<List<UserData>> getUserData(@Query("id")int id);

    @GET("photos")
    Call<List<PhotosData>> getAlbumPhotos(@Query("albumId")int id);




}
