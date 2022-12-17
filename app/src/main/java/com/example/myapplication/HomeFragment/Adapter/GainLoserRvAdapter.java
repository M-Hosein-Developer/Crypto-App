package com.example.myapplication.HomeFragment.Adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.application.R;
import com.example.application.databinding.GainloseRvItemBinding;
import com.example.myapplication.Models.cryptolistmodel.DataItem;

import java.util.ArrayList;

public class GainLoserRvAdapter extends RecyclerView.Adapter<GainLoserRvAdapter.GainLoseRvHolder> {

    ArrayList<DataItem> dataItems;
    LayoutInflater layoutInflater;

    public GainLoserRvAdapter(ArrayList<DataItem> dataItems) {
        this.dataItems = dataItems;
    }

    @NonNull
    @Override
    public GainLoseRvHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (layoutInflater == null){
            layoutInflater = LayoutInflater.from(parent.getContext());
        }
        GainloseRvItemBinding gainloseRvItemBinding = DataBindingUtil.inflate(layoutInflater , R.layout.gainlose_rv_item , parent , false);
        return new GainLoseRvHolder(gainloseRvItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull GainLoserRvAdapter.GainLoseRvHolder holder, int position) {
        holder.bind(dataItems.get(position));

        holder.gainloseRvItemBinding.GainLoseRVCon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return dataItems.size();
    }

    public void updateData(ArrayList<DataItem> newdata) {
        dataItems = newdata;
        notifyDataSetChanged();

    }

    static class GainLoseRvHolder extends RecyclerView.ViewHolder {

        GainloseRvItemBinding gainloseRvItemBinding;

        public GainLoseRvHolder(GainloseRvItemBinding gainloseRvItemBinding) {
            super(gainloseRvItemBinding.getRoot());
            this.gainloseRvItemBinding = gainloseRvItemBinding;
        }

        public void bind(DataItem dataItem){
            loadCoinlogo(dataItem);
            loadChar(dataItem);
            setColorText(dataItem);
            gainloseRvItemBinding.GLCoinName.setText(dataItem.getName());
            gainloseRvItemBinding.GLcoinSymbol.setText(dataItem.getName());
            setDecimalsForPrice(dataItem);
            if (dataItem.getListQuote().get(0).getPercentChange24h() > 0){
                gainloseRvItemBinding.UpDownIcon.setBackgroundResource(R.drawable.ic_baseline_arrow_drop_up_24);
                gainloseRvItemBinding.GLcoinChange.setText(String.format("%2f" , dataItem.getListQuote().get(0).getPercentChange24h()) + "%");
            }else if (dataItem.getListQuote().get(0).getPercentChange24h() < 0){
                gainloseRvItemBinding.UpDownIcon.setBackgroundResource(R.drawable.ic_baseline_arrow_drop_down_24);
                gainloseRvItemBinding.GLcoinChange.setText(String.format("%2f" , dataItem.getListQuote().get(0).getPercentChange24h()) + "%");
            }else{
                gainloseRvItemBinding.GLcoinChange.setText(String.format("%.2f",dataItem.getListQuote().get(0).getPercentChange24h()) + "%");
            }
            gainloseRvItemBinding.executePendingBindings();
        }

        private void loadCoinlogo(DataItem dataItem) {
            Glide.with(gainloseRvItemBinding.getRoot().getContext())
                    .load("https://s2.coinmarketcap.com/static/img/coins/64x64/" + dataItem.getId() + ".png")
                    .thumbnail(Glide.with(gainloseRvItemBinding.getRoot().getContext()).load(R.drawable.loading))
                    .into(gainloseRvItemBinding.gainLoseCoinlogo);
        }

        private void loadChar(DataItem dataItem) {
            Glide.with(gainloseRvItemBinding.getRoot().getContext())
                    .load("https://s3.coinmarketcap.com/generated/sparklines/web/7d/usd/" + dataItem.getId() + ".png")
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(gainloseRvItemBinding.imageView);
        }

        private void setDecimalsForPrice(DataItem dataItem) {
            if (dataItem.getListQuote().get(0).getPrice() < 1){
                gainloseRvItemBinding.GLcoinPrice.setText("$" + String.format("%.6f",dataItem.getListQuote().get(0).getPrice()));
            }else if (dataItem.getListQuote().get(0).getPrice() < 10){
                gainloseRvItemBinding.GLcoinPrice.setText("$" + String.format("%.4f",dataItem.getListQuote().get(0).getPrice()));
            }else {
                gainloseRvItemBinding.GLcoinPrice.setText("$" + String.format("%.2f",dataItem.getListQuote().get(0).getPrice()));
            }
        }

        private void setColorText(DataItem dataItem) {
            int greenColor = Color.parseColor("#FF00FF40");
            int redColor = Color.parseColor("#FFFF0000");
            int WhiteColor = Color.parseColor("#FFFFFF");

            if (dataItem.getListQuote().get(0).getPercentChange24h() < 0){
                gainloseRvItemBinding.imageView.setColorFilter(redColor);
                gainloseRvItemBinding.GLcoinChange.setTextColor(Color.RED);
            }else if (dataItem.getListQuote().get(0).getPercentChange24h() > 0){
                gainloseRvItemBinding.imageView.setColorFilter(greenColor);
                gainloseRvItemBinding.GLcoinChange.setTextColor(Color.GREEN);
            }else{
                gainloseRvItemBinding.imageView.setColorFilter(WhiteColor);
                gainloseRvItemBinding.GLcoinChange.setTextColor(Color.WHITE);
            }
        }
    }
}
