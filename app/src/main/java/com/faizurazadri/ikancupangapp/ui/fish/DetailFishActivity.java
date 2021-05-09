package com.faizurazadri.ikancupangapp.ui.fish;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.faizurazadri.ikancupangapp.R;
import com.faizurazadri.ikancupangapp.api.ApiClient;
import com.faizurazadri.ikancupangapp.databinding.ActivityDetailFishBinding;
import com.faizurazadri.ikancupangapp.model.Fish;
import com.faizurazadri.ikancupangapp.model.FishResponse;
import com.faizurazadri.ikancupangapp.ui.main.MainActivity;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailFishActivity extends AppCompatActivity {

    ActivityDetailFishBinding detailFishBinding;
    Fish fish;
    ProgressDialog progressDialog;
    private int id=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        detailFishBinding = ActivityDetailFishBinding.inflate(getLayoutInflater());
        setContentView(detailFishBinding.getRoot());

        fish = getIntent().getParcelableExtra("DATA");

        if (getSupportActionBar()!=null){
            getSupportActionBar().setTitle("Detail " + fish.getJenis_ikan());
        }

        id = fish.getId_ikan();

        progressDialog = new ProgressDialog(this);

        Locale locale = new Locale("in", "ID");
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);

        Glide.with(this)
                .load("https://ikancupang.urangcoding.com/uploads/"+fish.getImage_ikan())
                .error(R.drawable.icon_image)
                .into(detailFishBinding.imageFish);

        detailFishBinding.sellerFish.setText(fish.getPenjual());
        detailFishBinding.jenisFish.setText(fish.getJenis_ikan());
        detailFishBinding.priceFish.setText(numberFormat.format(fish.getHarga()));

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date newDate = simpleDateFormat.parse(fish.getTanggal_beli());
            simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
            detailFishBinding.purchaeDate.setText(simpleDateFormat.format(newDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        detailFishBinding.editFish.setOnClickListener(new proses_btn());
        detailFishBinding.btnDelete.setOnClickListener(new proses_btn());

    }

    class proses_btn implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){

                case R.id.edit_fish:
                    editData();
                    break;

                case R.id.btn_delete:
                    deleteData();
                    break;
            }
        }
    }

    private void editData(){
        Intent edit = new Intent(DetailFishActivity.this, EditFishActivity.class);
        edit.putExtra("DATA", fish);
        startActivity(edit);
    }

    private void deleteData(){
        progressDialog.setTitle(getResources().getString(R.string.deleted));
        progressDialog.setMessage(getResources().getString(R.string.please_Wait));
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        Call<FishResponse> fishResponseCall = ApiClient.getApiInterface().deleteFish(String.valueOf(id));
        fishResponseCall.enqueue(new Callback<FishResponse>() {
            @Override
            public void onResponse(Call<FishResponse> call, Response<FishResponse> response) {
                if (response.isSuccessful()){
                    Toast.makeText(getApplicationContext(), response.body().getMessage() , Toast.LENGTH_LONG).show();
                    startActivity(new Intent(DetailFishActivity.this, MainActivity.class));
                    finish();
                }else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.failed_permintaan) , Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<FishResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.failed_permintaan) , Toast.LENGTH_LONG).show();
            }
        });
    }
}