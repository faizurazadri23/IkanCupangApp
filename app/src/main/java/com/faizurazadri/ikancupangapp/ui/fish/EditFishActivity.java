package com.faizurazadri.ikancupangapp.ui.fish;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.faizurazadri.ikancupangapp.R;
import com.faizurazadri.ikancupangapp.api.ApiClient;
import com.faizurazadri.ikancupangapp.databinding.ActivityEditFishBinding;
import com.faizurazadri.ikancupangapp.model.Fish;
import com.faizurazadri.ikancupangapp.model.FishResponse;
import com.faizurazadri.ikancupangapp.ui.main.MainActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditFishActivity extends AppCompatActivity {

    ActivityEditFishBinding editFishBinding;
    Fish fish;
    private DatePickerDialog.OnDateSetListener onDateSetListener;
    public static final int IMAGE_CAPTURE_CODE = 102,SELECT_FILE  = 101;
    Bitmap bitmap;
    Uri uri;
    private String ConvertImage = "", jenis="", harga ="", penjual="", tanggalBeli="",id="";
    private SimpleDateFormat simpleDateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        editFishBinding = ActivityEditFishBinding.inflate(getLayoutInflater());
        setContentView(editFishBinding.getRoot());

        fish = getIntent().getParcelableExtra("DATA");
        id = String.valueOf(fish.getId_ikan());

        check_permission();

        if (getSupportActionBar()!=null){
            getSupportActionBar().setTitle("Edit "+ fish.getJenis_ikan());
        }

        editFishBinding.jenisFish.setText(fish.getJenis_ikan());
        editFishBinding.priceFish.setText(String.valueOf(fish.getHarga()));
        editFishBinding.sellerFish.setText(fish.getPenjual());



        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = sdf.parse(fish.getTanggal_beli());
            sdf = new SimpleDateFormat("dd-MM-yyyy");
            editFishBinding.purchaeDate.setText(sdf.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }


        Glide.with(this)
                .load("https://ikancupang.urangcoding.com/uploads/"+fish.getImage_ikan())
                .error(R.drawable.icon_image)
                .into(editFishBinding.imgFish);


        onDateSetListener = (view, year, month, dayOfMonth) -> {

            month = month + 1;

            String dateIn = dayOfMonth + "-" + month + "-"+ year;

            SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);

            try {
                editFishBinding.purchaeDate.setText(dateFormat1.format(dateFormat1.parse(dateIn)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        };

        editFishBinding.imgSelectedDate.setOnClickListener(new proses_btn());
        editFishBinding.imageSearchfish.setOnClickListener(new proses_btn());
        editFishBinding.btnUpdatefish.setOnClickListener(new proses_btn());
    }

    class proses_btn implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.img_selectedDate:
                    setOnDateSetListener();
                    break;

                case R.id.btn_updatefish:
                    updateData();
                    break;

                case R.id.image_searchfish:
                    selectImage();
                    break;
            }
        }
    }

    private void updateData() {

        jenis = editFishBinding.jenisFish.getText().toString();
        harga = editFishBinding.priceFish.getText().toString();
        penjual = editFishBinding.sellerFish.getText().toString();
        tanggalBeli = editFishBinding.purchaeDate.getText().toString();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date date = simpleDateFormat.parse(editFishBinding.purchaeDate.getText().toString());
            simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            tanggalBeli = simpleDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        editFishBinding.progressFish.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        Call<FishResponse> fishResponseCall = ApiClient.getApiInterface().updateFish(id, jenis, Integer.parseInt(harga), penjual, tanggalBeli, ConvertImage);
        fishResponseCall.enqueue(new Callback<FishResponse>() {
            @Override
            public void onResponse(Call<FishResponse> call, Response<FishResponse> response) {
                editFishBinding.progressFish.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                if (response.isSuccessful()){
                    Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                    startActivity(new Intent(EditFishActivity.this, MainActivity.class));
                    finish();
                }else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.failed_permintaan) , Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<FishResponse> call, Throwable t) {
                editFishBinding.progressFish.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.failed_permintaan) , Toast.LENGTH_LONG).show();
            }
        });
    }


    private void selectImage(){
        final CharSequence[] items = {"Camera", "Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(EditFishActivity.this);
        builder.setTitle("Add Image");
        builder.setItems(items, ((dialog, which) -> {
            if (items[which].equals("Camera")){
                Intent openCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(openCamera, IMAGE_CAPTURE_CODE);
            }else if (items[which].equals("Gallery")){
                Intent openFileImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                openFileImage.setType("image/");
                startActivityForResult(openFileImage.createChooser(openFileImage, "Select File"), SELECT_FILE);
            }else if (items[which].equals("Cancel")){
                dialog.dismiss();
            }
        }));

        builder.show();
    }

    private void setOnDateSetListener(){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int mont = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog dialog= new DatePickerDialog(EditFishActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, onDateSetListener,
                year, mont, day);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void check_permission(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_CAPTURE_CODE){
            bitmap = (Bitmap) data.getExtras().get("data");
            editFishBinding.imgFish.setImageBitmap(bitmap);

            ByteArrayOutputStream byteArrayOutputStream;
            byteArrayOutputStream = new ByteArrayOutputStream();

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] bytesArray = byteArrayOutputStream.toByteArray();
            ConvertImage = Base64.encodeToString(bytesArray, Base64.DEFAULT);
        }else if (requestCode==SELECT_FILE){
            uri = data.getData();
            editFishBinding.imgFish.setImageURI(uri);


            try {
                InputStream inputStream = EditFishActivity.this.getContentResolver().openInputStream(uri);
                byte[] bytes = new byte[inputStream.available()];
                inputStream.read(bytes);
                ConvertImage = Base64.encodeToString(bytes, Base64.DEFAULT);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}