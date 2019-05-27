package com.nedaei1375.json_place_holder_rest_api_example.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nedaei1375.json_place_holder_rest_api_example.Adapter.AlbumAdapter;
import com.nedaei1375.json_place_holder_rest_api_example.Handler.CustomClickListener;
import com.nedaei1375.json_place_holder_rest_api_example.Model.AlbumData;
import com.nedaei1375.json_place_holder_rest_api_example.Model.UserData;
import com.nedaei1375.json_place_holder_rest_api_example.R;
import com.nedaei1375.json_place_holder_rest_api_example.Rest.ApiClient;
import com.nedaei1375.json_place_holder_rest_api_example.Rest.ApiInterface;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlbumsActivity extends AppCompatActivity {

    private static final String TAG = "AlbumsActivity";
    //Views
    RecyclerView recycler_albums;
    ProgressBar progress_load;
    TextView app_title;

    private ApiInterface apiInterface;
    private Call<List<AlbumData>> albumCall;
    private LinearLayoutManager linearLayoutManager;
    private boolean isLoading = false;
    private List<AlbumData> albumDataList;
    private AlbumAdapter albumAdapter;

    private Call<List<UserData>> userCall;
    private List<UserData> userDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_albums);

        recycler_albums = findViewById(R.id.recycler_albums);
        progress_load = findViewById(R.id.progress_load);
        app_title = findViewById(R.id.app_title);


        getAlbums();


        //recycler view setup
        recycler_albums.setLayoutManager(new LinearLayoutManager(this));
        albumAdapter = new AlbumAdapter(this, recycler_albums, albumDataList, new CustomClickListener() {
            @Override
            public void itemClick(View view, int position) {

            }
        });
        recycler_albums.setAdapter(albumAdapter);
        recycler_albums.setHasFixedSize(true);



    }



    //get Albums
    public void getAlbums() {
        apiInterface = ApiClient.createApi();
        albumCall = apiInterface.getAlbumData();

        albumCall.enqueue(new Callback<List<AlbumData>>() {
            @Override
            public void onResponse(Call<List<AlbumData>> call, Response<List<AlbumData>> response) {

                progress_load.setVisibility(View.VISIBLE);
                app_title.setVisibility(View.VISIBLE);

                albumDataList = response.body();

                getUsers(albumDataList.get(0).getId());

                albumAdapter = new AlbumAdapter(AlbumsActivity.this, recycler_albums, albumDataList, new CustomClickListener() {
                    @Override
                    public void itemClick(View view, final int position) {
                    }
                });
                recycler_albums.setAdapter(albumAdapter);

                progress_load.setVisibility(View.GONE);
                app_title.setVisibility(View.GONE);


            }

            @Override
            public void onFailure(Call<List<AlbumData>> call, Throwable t) {
                Log.e(TAG, "onFailure: '" + t.getMessage());
                Toast.makeText(AlbumsActivity.this, "اتصال به سرور برقرار نشد", Toast.LENGTH_SHORT).show();
                //Toast.makeText(MainActivity.this, "OnFailure: " + t.getMessage(), Toast.LENGTH_SHORT).show();

                progress_load.setVisibility(View.GONE);
                app_title.setVisibility(View.GONE);

                startActivity(new Intent(AlbumsActivity.this, NetworkActivity.class));
                AlbumsActivity.this.finish();
            }
        });

    }


    //get Users
    public void getUsers(int id) {
        apiInterface = ApiClient.createApi();
        userCall = apiInterface.getUserData(id);

        userCall.enqueue(new Callback<List<UserData>>() {
            @Override
            public void onResponse(Call<List<UserData>> call, Response<List<UserData>> response) {

                userDataList = response.body();

            }

            @Override
            public void onFailure(Call<List<UserData>> call, Throwable t) {
                Log.e(TAG, "onFailure: '" + t.getMessage());
                Toast.makeText(AlbumsActivity.this, "اتصال به سرور برقرار نشد", Toast.LENGTH_SHORT).show();
                //Toast.makeText(MainActivity.this, "OnFailure: " + t.getMessage(), Toast.LENGTH_SHORT).show();

                startActivity(new Intent(AlbumsActivity.this, NetworkActivity.class));
                AlbumsActivity.this.finish();
            }
        });

    }




}
