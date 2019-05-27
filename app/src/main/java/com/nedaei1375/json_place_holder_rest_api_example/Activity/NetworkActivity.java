package com.nedaei1375.json_place_holder_rest_api_example.Activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;

import com.google.android.material.snackbar.Snackbar;
import com.nedaei1375.json_place_holder_rest_api_example.R;

public class NetworkActivity extends AppCompatActivity {


    Button btn_try_again;
    CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network);

        btn_try_again = findViewById(R.id.btn_try_again);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);

        btn_try_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryButton();
            }
        });

    }


        private void tryButton(){


        if (isNetworkAvailable()) {
            startActivity(new Intent(NetworkActivity.this, AlbumsActivity.class));
            this.finish();

        } else {

            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, "خطا در اتصال به اینترنت ", Snackbar.LENGTH_INDEFINITE)
                    .setAction("تنظیمات", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
                            startActivity(intent);
                            //startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                        }
                    });

            ViewCompat.setLayoutDirection(snackbar.getView(), ViewCompat.LAYOUT_DIRECTION_RTL);
            snackbar.show();
        }
        }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}


