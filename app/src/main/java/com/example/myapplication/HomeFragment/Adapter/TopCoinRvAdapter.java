package com.example.myapplication.HomeFragment.Adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.application.R;
import com.example.application.databinding.TopmarketItemBinding;
import com.example.myapplication.Models.cryptolistmodel.DataItem;

import java.util.ArrayList;

public class TopCoinRvAdapter extends RecyclerView.Adapter<TopCoinRvAdapter.TopCoinRvHolder> {

    LayoutInflater layoutInflater;
    ArrayList<DataItem> dataItems;

    public TopCoinRvAdapter(ArrayList<DataItem> dataItems) {
        this.dataItems = dataItems;
    }

    @NonNull
    @Override
    public TopCoinRvAdapter.TopCoinRvHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (layoutInflater == null){
            layoutInflater = LayoutInflater.from(parent.getContext());
        }

        TopmarketItemBinding topmarketItemBinding = DataBindingUtil.inflate(layoutInflater , R.layout.topmarket_item , parent , false);
        return new TopCoinRvHolder(topmarketItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull TopCoinRvAdapter.TopCoinRvHolder holder, int position) {
        holder.bind(dataItems.get(position));
        holder.topmarketItemBinding.TopCoinCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //اگر خواستیم کاری برامون کنن اینجا انجام میدیم.
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataItems.size();
    }

    public void updateData(ArrayList<DataItem> newdata) {
        dataItems.clear();
        dataItems.addAll(newdata);
        notifyDataSetChanged();
    }


    static class TopCoinRvHolder extends RecyclerView.ViewHolder{

        TopmarketItemBinding topmarketItemBinding;

        public TopCoinRvHolder(TopmarketItemBinding topmarketItemBinding) {
            super(topmarketItemBinding.getRoot());
            this.topmarketItemBinding = topmarketItemBinding;
        }

        public void bind(DataItem dataItem){
            loadCoinLogo(dataItem);
            SetColorText(dataItem);
            SetDecimalsForPrice(dataItem);
            topmarketItemBinding.TopCoinName.setText(String.format("%s/USD", dataItem.getSymbol()));
            if (dataItem.getListQuote().get(0).getPercentChange24h() > 0){
                topmarketItemBinding.TopCoinChange.setText("+" + String.format("%.2f",dataItem.getListQuote().get(0).getPercentChange24h()) + "%");
            }else {
                topmarketItemBinding.TopCoinChange.setText(String.format("%.2f",dataItem.getListQuote().get(0).getPercentChange24h()) + "%");
            }
            topmarketItemBinding.executePendingBindings();
        }

        private void loadCoinLogo(DataItem dataItem) {
            Glide.with(topmarketItemBinding.getRoot().getContext())
                    .load("https://s2.coinmarketcap.com/static/img/coins/64x64/" + dataItem.getId() + ".png")
                    .thumbnail(Glide.with(topmarketItemBinding.getRoot().getContext()).load(R.drawable.loading))
                    .into(topmarketItemBinding.coinlogo);
        }


        //set diffrent decimals for diffrent price
        private void SetDecimalsForPrice(DataItem dataItem) {
            if (dataItem.getListQuote().get(0).getPrice() < 1){
                topmarketItemBinding.TopCoinPrice.setText(String.format("%.6f",dataItem.getListQuote().get(0).getPrice()));
            }else if (dataItem.getListQuote().get(0).getPrice() < 10){
                topmarketItemBinding.TopCoinPrice.setText(String.format("%.4f",dataItem.getListQuote().get(0).getPrice()));
            }else {
                topmarketItemBinding.TopCoinPrice.setText(String.format("%.2f",dataItem.getListQuote().get(0).getPrice()));
            }
        }

        //set Color Green and Red for price
        private void SetColorText(DataItem dataItem){
            if (dataItem.getListQuote().get(0).getPercentChange24h() < 0){
                topmarketItemBinding.TopCoinChange.setTextColor(Color.RED);
                topmarketItemBinding.TopCoinPrice.setTextColor(Color.RED);
            }else {
                topmarketItemBinding.TopCoinChange.setTextColor(Color.GREEN);
                topmarketItemBinding.TopCoinPrice.setTextColor(Color.GREEN);
            }
        }

    }
}
