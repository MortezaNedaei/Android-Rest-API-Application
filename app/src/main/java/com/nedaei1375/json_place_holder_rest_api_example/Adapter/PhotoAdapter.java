package com.nedaei1375.json_place_holder_rest_api_example.Adapter;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;

import android.net.Uri;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;

import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import androidx.annotation.NonNull;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.nedaei1375.json_place_holder_rest_api_example.Handler.CustomClickListener;
import com.nedaei1375.json_place_holder_rest_api_example.Model.AlbumData;
import com.nedaei1375.json_place_holder_rest_api_example.Model.PhotosData;
import com.nedaei1375.json_place_holder_rest_api_example.R;
import com.nedaei1375.json_place_holder_rest_api_example.Rest.ApiInterface;

import java.util.List;


public class PhotoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "PhotoAdapter";
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
    private List<PhotosData> photosDataList;
    private PhotosData photosData;
    private CustomClickListener listener;
    private ApiInterface apiInterface;



    public PhotoAdapter(Context context, RecyclerView recyclerView, List<PhotosData> photosDataList, CustomClickListener listener) {
        this.context = context;
        this.photosDataList = photosDataList;
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
        return photosDataList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {

            final View view = LayoutInflater.from(context).inflate(R.layout.recycler_item_vertical_photos, parent, false);
            final PhotosViewHolder photosViewHolder = new PhotosViewHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.itemClick(view, photosViewHolder.getLayoutPosition());
                }
            });
            return photosViewHolder;

        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }


        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof PhotosViewHolder) {
            final PhotosViewHolder photosViewHolder = (PhotosViewHolder) holder;

            photosData = photosDataList.get(position);

            photosViewHolder.txt_title.setText(photosData.getTitle());
            photosViewHolder.img_post.setImageURI(Uri.parse(photosData.getThumbnailUrl()));




            photosViewHolder.img_post.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    photosData = photosDataList.get(position);

                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.layout_dialog_image_larger);
                    ImageView img_post_larger = (ImageView) dialog.findViewById(R.id.img_post_larger);
                    img_post_larger.setImageURI(Uri.parse(photosData.getUrl()));
                    dialog.show();

                }
            });



        } else if (holder instanceof LoadingViewHolder_Photos) {
            LoadingViewHolder_Photos loadingViewHolder_photos = (LoadingViewHolder_Photos) holder;
            loadingViewHolder_photos.progressBar.setIndeterminate(true);
        }


    }


    @Override
    public int getItemCount() {
        return photosDataList == null ? 0 : photosDataList.size();
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




}

class PhotosViewHolder extends RecyclerView.ViewHolder {
    TextView txt_title;
    SimpleDraweeView img_post;
    Button btn_download;


    public PhotosViewHolder(View itemView) {
        super(itemView);
        txt_title = itemView.findViewById(R.id.txt_title);
        img_post = itemView.findViewById(R.id.img_post);
        btn_download = itemView.findViewById(R.id.btn_download);
    }

}

class LoadingViewHolder_Photos extends RecyclerView.ViewHolder {
    public ProgressBar progressBar;

    public LoadingViewHolder_Photos(View view) {
        super(view);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar1);
    }
}