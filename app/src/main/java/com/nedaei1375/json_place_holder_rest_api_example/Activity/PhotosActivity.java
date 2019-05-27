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

import com.facebook.drawee.backends.pipeline.Fresco;
import com.nedaei1375.json_place_holder_rest_api_example.Adapter.PhotoAdapter;
import com.nedaei1375.json_place_holder_rest_api_example.Handler.CustomClickListener;
import com.nedaei1375.json_place_holder_rest_api_example.Model.PhotosData;
import com.nedaei1375.json_place_holder_rest_api_example.R;
import com.nedaei1375.json_place_holder_rest_api_example.Rest.ApiClient;
import com.nedaei1375.json_place_holder_rest_api_example.Rest.ApiInterface;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhotosActivity extends AppCompatActivity {
    private int id;
    private static final String TAG = "PhotosActivity";

    //Views
    RecyclerView recycler_photos;
    ProgressBar progress_load;
    TextView app_title;

    private ApiInterface apiInterface;
    private Call<List<PhotosData>> photoCall;
    private LinearLayoutManager linearLayoutManager;
    private boolean isLoading = false;
    private List<PhotosData> photoDataList;
    private PhotoAdapter photoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);

        setContentView(R.layout.activity_photos);


        Intent intent = getIntent();
        id = intent.getIntExtra("POST_ID", 0);

        recycler_photos = findViewById(R.id.recycler_photos);
        progress_load = findViewById(R.id.progress_load);
        app_title = findViewById(R.id.app_title);

        getPhotos(id);


        //recycler view setup
        recycler_photos.setLayoutManager(new LinearLayoutManager(this));
        photoAdapter = new PhotoAdapter(this, recycler_photos, photoDataList, new CustomClickListener() {
            @Override
            public void itemClick(View view, int position) {

            }
        });
        recycler_photos.setAdapter(photoAdapter);
        recycler_photos.setHasFixedSize(true);



    }



    //get Albums
    public void getPhotos(int id) {
        apiInterface = ApiClient.createApi();
        photoCall = apiInterface.getAlbumPhotos(id);

        photoCall.enqueue(new Callback<List<PhotosData>>() {
            @Override
            public void onResponse(Call<List<PhotosData>> call, Response<List<PhotosData>> response) {

                progress_load.setVisibility(View.VISIBLE);
                app_title.setVisibility(View.VISIBLE);

                photoDataList = response.body();


                photoAdapter = new PhotoAdapter(PhotosActivity.this, recycler_photos, photoDataList, new CustomClickListener() {
                    @Override
                    public void itemClick(View view, final int position) {
                    }
                });
                recycler_photos.setAdapter(photoAdapter);

                progress_load.setVisibility(View.GONE);
                app_title.setVisibility(View.GONE);


            }

            @Override
            public void onFailure(Call<List<PhotosData>> call, Throwable t) {
                Log.e(TAG, "onFailure: '" + t.getMessage());
                Toast.makeText(PhotosActivity.this, "اتصال به سرور برقرار نشد", Toast.LENGTH_SHORT).show();
                //Toast.makeText(MainActivity.this, "OnFailure: " + t.getMessage(), Toast.LENGTH_SHORT).show();

                progress_load.setVisibility(View.GONE);
                app_title.setVisibility(View.GONE);

                startActivity(new Intent(PhotosActivity.this, NetworkActivity.class));
                PhotosActivity.this.finish();
            }
        });

    }
}
