package com.faizurazadri.ikancupangapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.faizurazadri.ikancupangapp.R;
import com.faizurazadri.ikancupangapp.model.Fish;
import com.faizurazadri.ikancupangapp.ui.fish.DetailFishActivity;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterFish extends RecyclerView.Adapter<AdapterFish.ViewholderFish> {

    List<Fish> fishList;
    Context context;

    Locale localeID = new Locale("in", "ID");
    NumberFormat numberFormat = NumberFormat.getCurrencyInstance(localeID);

    public AdapterFish(List<Fish> fishList, Context context) {
        this.fishList = fishList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewholderFish onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_fish, parent, false);
        return new ViewholderFish(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewholderFish holder, int position) {
        Fish fish = fishList.get(position);

        Glide.with(context)
                .load("https://ikancupang.urangcoding.com/uploads/"+fish.getImage_ikan())
                .error(R.drawable.icon_image)
                .into(holder.img_fish);

        holder.jnsfish.setText(fish.getJenis_ikan());
        holder.price.setText(numberFormat.format(fish.getHarga()));

        holder.itemView.setOnClickListener(v -> {
            Intent detail = new Intent(v.getContext(), DetailFishActivity.class);
            detail.putExtra("DATA", fish);
            v.getContext().startActivity(detail);
        });
    }

    @Override
    public int getItemCount() {
        return fishList.size();
    }

    public class ViewholderFish extends RecyclerView.ViewHolder {

        private CircleImageView img_fish;
        private TextView jnsfish,price;

        public ViewholderFish(@NonNull View itemView) {
            super(itemView);

            img_fish = itemView.findViewById(R.id.img_fish);
            jnsfish = itemView.findViewById(R.id.jenis_fish);
            price = itemView.findViewById(R.id.price_fish);
        }
    }
}
