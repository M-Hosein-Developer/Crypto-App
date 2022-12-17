package com.example.myapplication.WatchListFragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.application.R;
import com.example.application.databinding.WatchlistItemBinding;
import com.example.myapplication.Models.cryptolistmodel.DataItem;

import java.util.ArrayList;

public class WatchListRvAdapter extends RecyclerView.Adapter<WatchListRvAdapter.WatchListRvHolder>{

    LayoutInflater layoutInflater;
    ArrayList<DataItem> dataItems;

    public WatchListRvAdapter(ArrayList<DataItem> dataItems) {
        this.dataItems = dataItems;
    }

    @NonNull
    @Override
    public WatchListRvAdapter.WatchListRvHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (layoutInflater == null){
            layoutInflater = LayoutInflater.from(parent.getContext());
        }

        WatchlistItemBinding watchlistItemBinding = DataBindingUtil.inflate(layoutInflater, R.layout.watchlist_item,parent,false);


        return new WatchListRvHolder(watchlistItemBinding);
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull WatchListRvAdapter.WatchListRvHolder holder, int position) {

        holder.bind(dataItems.get(position), position);

        holder.watchlistItemBinding.WatchLisetcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("model",dataItems.get(position));

                Navigation.findNavController(v).navigate(R.id.action_watchListFragment_to_detailFragment , bundle);

            }
        });

    }

    @Override
    public int getItemCount() {
        return dataItems.size();
    }

    public void updateData(ArrayList<DataItem> newdata){
        dataItems.addAll(newdata);
        notifyDataSetChanged();
    }

    static class WatchListRvHolder extends RecyclerView.ViewHolder{
        WatchlistItemBinding watchlistItemBinding;

        public WatchListRvHolder(WatchlistItemBinding watchlistItemBinding){
            super(watchlistItemBinding.getRoot());
            this.watchlistItemBinding = watchlistItemBinding;
        }

        public void bind(DataItem dataItem, int position){

            loadCoinlogo(dataItem);
            loadChart(dataItem);
            SetColorText(dataItem);
            watchlistItemBinding.GLCoinName.setText(dataItem.getName());
            watchlistItemBinding.GLcoinSymbol.setText(dataItem.getSymbol());
            SetDecimalsForPrice(dataItem);
            //set + or - before precent change
            if (dataItem.getListQuote().get(0).getPercentChange24h() > 0){
                watchlistItemBinding.UpDownIcon.setBackgroundResource(R.drawable.ic_baseline_arrow_drop_up_24);
                watchlistItemBinding.GLcoinChange.setText(String.format("%.2f",dataItem.getListQuote().get(0).getPercentChange24h()) + "%");
            }else if (dataItem.getListQuote().get(0).getPercentChange24h() < 0){
                watchlistItemBinding.UpDownIcon.setBackgroundResource(R.drawable.ic_baseline_arrow_drop_down_24);
                watchlistItemBinding.GLcoinChange.setText(String.format("%.2f",dataItem.getListQuote().get(0).getPercentChange24h()) + "%");
            }else {
                watchlistItemBinding.GLcoinChange.setText(String.format("%.2f",dataItem.getListQuote().get(0).getPercentChange24h()) + "%");
            }
            watchlistItemBinding.executePendingBindings();
        }

        private void loadCoinlogo(DataItem dataItem) {
            Glide.with(watchlistItemBinding.getRoot().getContext())
                    .load("https://s2.coinmarketcap.com/static/img/coins/32x32/" + dataItem.getId() + ".png")
                    .thumbnail(Glide.with(watchlistItemBinding.getRoot().getContext()).load(R.drawable.loading))
                    .into(watchlistItemBinding.gainLoseCoinlogo);
        }

        private void loadChart(DataItem dataItem) {

            Glide.with(watchlistItemBinding.getRoot().getContext())
                    .load("https://s3.coinmarketcap.com/generated/sparklines/web/7d/usd/" + dataItem.getId() + ".png")
                    .into(watchlistItemBinding.imageView);
        }

        //set diffrent decimals for diffrent price
        private void SetDecimalsForPrice(DataItem dataItem) {
            if (dataItem.getListQuote().get(0).getPrice() < 1){
                watchlistItemBinding.GLcoinPrice .setText("$" + String.format("%.6f",dataItem.getListQuote().get(0).getPrice()));
            }else if (dataItem.getListQuote().get(0).getPrice() < 10){
                watchlistItemBinding.GLcoinPrice.setText("$" + String.format("%.4f",dataItem.getListQuote().get(0).getPrice()));
            }else {
                watchlistItemBinding.GLcoinPrice.setText("$" + String.format("%.2f",dataItem.getListQuote().get(0).getPrice()));
            }
        }

        //set Color Green and Red for price and chart
        private void SetColorText(DataItem dataItem){
            int greenColor = Color.parseColor("#FF00FF40");
            int redColor = Color.parseColor("#FFFF0000");
            int whiteColor = Color.parseColor("#FFFFFF");
            if (dataItem.getListQuote().get(0).getPercentChange24h() < 0){
                watchlistItemBinding.imageView.setColorFilter(redColor);
                watchlistItemBinding.GLcoinChange.setTextColor(Color.RED);
            }else if (dataItem.getListQuote().get(0).getPercentChange24h() > 0){
                watchlistItemBinding.imageView.setColorFilter(greenColor);
                watchlistItemBinding.GLcoinChange.setTextColor(Color.GREEN);
            }else {
                watchlistItemBinding.imageView.setColorFilter(whiteColor);
                watchlistItemBinding.GLcoinChange.setTextColor(Color.WHITE);
            }
        }
    }
}
