package com.faizurazadri.ikancupangapp.ui.fish;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.LabeledIntent;
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

import com.faizurazadri.ikancupangapp.R;
import com.faizurazadri.ikancupangapp.api.ApiClient;
import com.faizurazadri.ikancupangapp.databinding.ActivityFishBinding;
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

public class FishActivity extends AppCompatActivity {

    ActivityFishBinding fishBinding;
    private DatePickerDialog.OnDateSetListener onDateSetListener;
    public static final int IMAGE_CAPTURE_CODE = 102,SELECT_FILE  = 101;
    Bitmap bitmap;
    Uri uri;
    private String ConvertImage = "", jenis="", harga ="", penjual="", tanggalBeli="";
    private SimpleDateFormat simpleDateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fishBinding = ActivityFishBinding.inflate(getLayoutInflater());
        setContentView(fishBinding.getRoot());

        Date dateNow = new Date();

        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        String tanggal = simpleDateFormat.format(dateNow);
        fishBinding.purchaeDate.setText(tanggal);

        check_permission();

        onDateSetListener = (view, year, month, dayOfMonth) -> {
            month = month + 1;
            String dateIn = dayOfMonth + "-" + month + "-"+ year;
            SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);

            try {
                fishBinding.purchaeDate.setText(dateFormat1.format(dateFormat1.parse(dateIn)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        };

        fishBinding.imgSelectedDate.setOnClickListener(new proses_btn());
        fishBinding.btnInsertfish.setOnClickListener(new proses_btn());
        fishBinding.imageSearchfish.setOnClickListener(new proses_btn());
    }

    class proses_btn implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.img_selectedDate:
                    setOnDateSetListener();
                    break;

                case R.id.btn_insertfish:
                    sendData();
                    break;

                case R.id.image_searchfish:
                    selectImage();
                    break;
            }
        }
    }

    private void sendData() {
        jenis = fishBinding.jenisFish.getText().toString();
        harga = fishBinding.priceFish.getText().toString();
        penjual =fishBinding.sellerFish.getText().toString();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date newDate = simpleDateFormat.parse(fishBinding.purchaeDate.getText().toString());
            simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            tanggalBeli = simpleDateFormat.format(newDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (jenis.isEmpty()){
            fishBinding.jenisFish.setError(getResources().getString(R.string.validation_error));
            fishBinding.jenisFish.requestFocus();
            return;
        }

        if (harga.isEmpty()){
            fishBinding.priceFish.setError(getResources().getString(R.string.validation_error));
            fishBinding.priceFish.requestFocus();
            return;
        }

        if (penjual.isEmpty()){
            fishBinding.sellerFish.setError(getResources().getString(R.string.validation_error));
            fishBinding.sellerFish.requestFocus();
            return;
        }

        if (ConvertImage.isEmpty()){
            Toast.makeText(getApplicationContext(), "Gambar Ikan " + getResources().getString(R.string.validation_error) , Toast.LENGTH_LONG).show();
            return;
        }

        fishBinding.progressFish.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        Call<FishResponse> fishResponseCall = ApiClient.getApiInterface().insertFish(jenis, Integer.parseInt(harga), penjual, tanggalBeli, ConvertImage);
        fishResponseCall.enqueue(new Callback<FishResponse>() {
            @Override
            public void onResponse(Call<FishResponse> call, Response<FishResponse> response) {
                if (response.isSuccessful()){
                    Toast.makeText(getApplicationContext(), response.body().getMessage() , Toast.LENGTH_LONG).show();
                    startActivity(new Intent(FishActivity.this, MainActivity.class));
                    finish();
                }else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.failed_permintaan) , Toast.LENGTH_LONG).show();
                }

                fishBinding.progressFish.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }

            @Override
            public void onFailure(Call<FishResponse> call, Throwable t) {
                fishBinding.progressFish.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.failed_permintaan) , Toast.LENGTH_LONG).show();
            }
        });
    }

    private void selectImage(){
        final CharSequence[] items = {"Camera", "Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(FishActivity.this);
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


        DatePickerDialog dialog= new DatePickerDialog(FishActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, onDateSetListener,
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
            fishBinding.imgFish.setImageBitmap(bitmap);

            ByteArrayOutputStream byteArrayOutputStream;
            byteArrayOutputStream = new ByteArrayOutputStream();

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] bytesArray = byteArrayOutputStream.toByteArray();
            ConvertImage = Base64.encodeToString(bytesArray, Base64.DEFAULT);
        }else if (requestCode==SELECT_FILE){
            uri = data.getData();
            fishBinding.imgFish.setImageURI(uri);


            try {
                InputStream inputStream = FishActivity.this.getContentResolver().openInputStream(uri);
                byte[] bytes = new byte[inputStream.available()];
                inputStream.read(bytes);
                ConvertImage = Base64.encodeToString(bytes, Base64.DEFAULT);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}