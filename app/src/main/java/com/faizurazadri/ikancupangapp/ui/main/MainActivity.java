package com.faizurazadri.ikancupangapp.ui.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.faizurazadri.ikancupangapp.R;
import com.faizurazadri.ikancupangapp.adapter.AdapterFish;
import com.faizurazadri.ikancupangapp.api.ApiClient;
import com.faizurazadri.ikancupangapp.databinding.ActivityMainBinding;
import com.faizurazadri.ikancupangapp.model.Fish;
import com.faizurazadri.ikancupangapp.model.GetFish;
import com.faizurazadri.ikancupangapp.ui.fish.FishActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding mainBinding;
    private RecyclerView.LayoutManager layoutManager;
    private AdapterFish adapterFish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());

        if (getSupportActionBar()!=null){
            getSupportActionBar().setTitle(getResources().getString(R.string.home));
        }

        layoutManager = new LinearLayoutManager(this);
        mainBinding.rvFish.setLayoutManager(layoutManager);
        mainBinding.rvFish.setHasFixedSize(true);

        getDataFish();

        mainBinding.addFish.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, FishActivity.class));
        });

    }

    private void getDataFish(){
        Call<GetFish> getFishCall = ApiClient.getApiInterface().getDataFish();
        getFishCall.enqueue(new Callback<GetFish>() {
            @Override
            public void onResponse(Call<GetFish> call, Response<GetFish> response) {
                mainBinding.progressFish.setVisibility(View.GONE);
                if (response.isSuccessful()){
                    List<Fish> fishList = response.body().getFishList();
                    adapterFish  = new AdapterFish(fishList, MainActivity.this);
                    mainBinding.rvFish.setAdapter(adapterFish);
                    adapterFish.notifyDataSetChanged();
                }else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.not_found) , Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<GetFish> call, Throwable t) {
                mainBinding.progressFish.setVisibility(View.GONE);
            }
        });
    }
}