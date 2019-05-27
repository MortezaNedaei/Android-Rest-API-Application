package com.nedaei1375.json_place_holder_rest_api_example.Adapter;

import android.app.Activity;
import android.content.Context;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;

import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nedaei1375.json_place_holder_rest_api_example.Activity.NetworkActivity;
import com.nedaei1375.json_place_holder_rest_api_example.Activity.PhotosActivity;
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


public class AlbumAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "AlbumAdapter";
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private int mLastPosition = -1;
    private OnLoadMoreListener onLoadMoreListener;
    private boolean isLoading;

    private boolean isMoreDataAvailable = true;
    private Activity activity;
    private int visibleThreshold = 5; // Important
    private int lastVisibleItem, totalItemCount;
    private Context context;
    private List<AlbumData> albumDataList;
    private AlbumData albumData;
    private CustomClickListener listener;

    private ApiInterface apiInterface;
    private Call<List<UserData>> userCall;
    private List<UserData> userDataList;
    private String userName = "" ;




    public AlbumAdapter(Context context, RecyclerView recyclerView, List<AlbumData> albumDataList, CustomClickListener listener) {
        this.context = context;
        this.albumDataList = albumDataList;
        this.listener = listener;

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (!isLoading && isMoreDataAvailable && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    if (onLoadMoreListener != null) {
                        onLoadMoreListener.onLoadMore();
                    }
                    isLoading = true;
                }
            }
        });
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.onLoadMoreListener = mOnLoadMoreListener;
    }

    @Override
    public int getItemViewType(int position) {
        return albumDataList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {

            final View view = LayoutInflater.from(context).inflate(R.layout.recycler_item_vertical_albums, parent, false);
            final AlbumsViewHolder albumsViewHolder = new AlbumsViewHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.itemClick(view, albumsViewHolder.getLayoutPosition());
                }
            });
            return albumsViewHolder;

        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }


        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof AlbumsViewHolder) {
            final AlbumsViewHolder albumsViewHolder = (AlbumsViewHolder) holder;

            albumData = albumDataList.get(position);

            albumsViewHolder.txt_title.setText(albumData.getTitle());
            //albumsViewHolder.txt_user.setText(getUsers(albumData.getUserId()));
            //getUsers(albumData.getUserId() , position);

            apiInterface = ApiClient.createApi();
            userCall = apiInterface.getUserData(albumData.getUserId());

            userCall.enqueue(new Callback<List<UserData>>() {
                @Override
                public void onResponse(Call<List<UserData>> call, Response<List<UserData>> response) {
                    userDataList = response.body();

                    albumsViewHolder.txt_user.setText(userDataList.get(0).getUsername());
                    albumsViewHolder.txt_name.setText(userDataList.get(0).getName());
                    albumsViewHolder.txt_email.setText(userDataList.get(0).getEmail());
                    albumsViewHolder.txt_phone.setText(userDataList.get(0).getPhone());

                }

                @Override
                public void onFailure(Call<List<UserData>> call, Throwable t) {
                    Log.e(TAG, "onFailure: '" + t.getMessage());
                    Toast.makeText(context, "اتصال به سرور برقرار نشد", Toast.LENGTH_SHORT).show();
                    //Toast.makeText(MainActivity.this, "OnFailure: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    context.startActivity(new Intent(context, NetworkActivity.class));
                    activity.finish();
                }
            });



            albumsViewHolder.btn_download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    albumData = albumDataList.get(position);  // Important

                    Intent intent = new Intent(context, PhotosActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("POST_ID", albumData.getId());
                    context.startActivity(intent);
                }
            });


        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }


    }


    @Override
    public int getItemCount() {
        return albumDataList == null ? 0 : albumDataList.size();
    }



    public interface AlbumsAdapterListener {
        void onPostSelected(AlbumData albumData);
    }


    public void setLoaded() {
        isLoading = false;
    }

    public void setMoreDataAvailable(boolean moreDataAvailable) {
        isMoreDataAvailable = moreDataAvailable;
    }



    //get Users
    private String getUsers(int id) {
        apiInterface = ApiClient.createApi();
        userCall = apiInterface.getUserData(id);

        userCall.enqueue(new Callback<List<UserData>>() {
            @Override
            public void onResponse(Call<List<UserData>> call, Response<List<UserData>> response) {
                userDataList = response.body();
                //Toast.makeText(context, userDataList.get(0).getUsername() + "", Toast.LENGTH_SHORT).show();
                userName = userDataList.get(0).getUsername() + "";

            }

            @Override
            public void onFailure(Call<List<UserData>> call, Throwable t) {
                Log.e(TAG, "onFailure: '" + t.getMessage());
                Toast.makeText(context, "اتصال به سرور برقرار نشد", Toast.LENGTH_SHORT).show();
                //Toast.makeText(MainActivity.this, "OnFailure: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                context.startActivity(new Intent(context, NetworkActivity.class));
                activity.finish();
            }
        });
        Toast.makeText(context, userName, Toast.LENGTH_SHORT).show();

        return userName;

    }


}

class AlbumsViewHolder extends RecyclerView.ViewHolder {
    TextView txt_title;
    TextView txt_user;
    TextView txt_name;
    TextView txt_email;
    TextView txt_phone;
    Button btn_download;



    public AlbumsViewHolder(View itemView) {
        super(itemView);
        txt_title = itemView.findViewById(R.id.txt_title);
        txt_name = itemView.findViewById(R.id.txt_name);
        txt_user = itemView.findViewById(R.id.txt_user);
        txt_email = itemView.findViewById(R.id.txt_email);
        txt_phone = itemView.findViewById(R.id.txt_phone);
        btn_download = itemView.findViewById(R.id.btn_download);
    }

}

class LoadingViewHolder extends RecyclerView.ViewHolder {
    public ProgressBar progressBar;

    public LoadingViewHolder(View view) {
        super(view);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar1);
    }
}