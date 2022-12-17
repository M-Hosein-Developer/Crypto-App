package com.example.myapplication;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.application.R;
import com.example.application.databinding.FragmentDetailBinding;
import com.example.myapplication.Models.cryptolistmodel.DataItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class DetailFragment extends Fragment {

    FragmentDetailBinding fragmentDetailBinding;
    ArrayList<String> bookmarksArray;
    boolean watchlistIsChecked = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

       fragmentDetailBinding  =  DataBindingUtil.inflate(inflater, R.layout.fragment_detail,container,false);

        DataItem dataItem = getArguments().getParcelable("model");

        initialize(dataItem);
        setLogo(dataItem);
        bookMarkButtom(dataItem);

        // Inflate the layout for this fragment
        return fragmentDetailBinding.getRoot();
    }

    private void bookMarkButtom(DataItem dataItem) {

        ReadDataStore();

        // show diffrent Icons when get data from shared Prefrence
        if (bookmarksArray.contains(dataItem.getSymbol())) {
            watchlistIsChecked = true;
            fragmentDetailBinding.bookmark.setImageResource(R.drawable.ic_baseline_star_24);
        } else {
            watchlistIsChecked = false;
            fragmentDetailBinding.bookmark.setImageResource(R.drawable.ic_baseline_star_border_24);
        }


        // write or delete data from Shared with click on bookmark Btn
        fragmentDetailBinding.bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (watchlistIsChecked == false) {
                    if (!bookmarksArray.contains(dataItem.getSymbol())){
                        bookmarksArray.add(dataItem.getSymbol());
                    }
                    writetoDataStore();
                    fragmentDetailBinding.bookmark.setImageResource(R.drawable.ic_baseline_star_24);
                    watchlistIsChecked = true;

                } else {
                    fragmentDetailBinding.bookmark.setImageResource(R.drawable.ic_baseline_star_border_24);
                    //clear bookmark
                    bookmarksArray.remove(dataItem.getSymbol());
                    writetoDataStore();
                    watchlistIsChecked = false;
                }
            }

        });
    }

    // read new BookMark on Shared Prefrence
    private void ReadDataStore() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(requireActivity());
        Gson gson = new Gson();
        String json = sharedPrefs.getString("bookmarks", String.valueOf(new ArrayList<String>()));
        Type type = new TypeToken<ArrayList<String>>(){}.getType();
        bookmarksArray = gson.fromJson(json, type);
        Log.e("TAG", "ReadDataStore: " + bookmarksArray);
    }

    // Write BookMarks ArrayList From Shared Prefrence
    private void writetoDataStore() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(requireActivity());
        SharedPreferences.Editor editor = sharedPrefs.edit();
        Gson gson = new Gson();

        String json = gson.toJson(bookmarksArray);

        editor.putString("bookmarks", json);
        editor.apply();
    }


    private void setLogo(DataItem dataItem) {
        Glide.with(fragmentDetailBinding.getRoot().getContext())
                .load("https://s2.coinmarketcap.com/static/img/coins/128x128/" + dataItem.getId() + ".png")
                .thumbnail(Glide.with(fragmentDetailBinding.getRoot().getContext()).load(R.drawable.loading))
                .into(fragmentDetailBinding.imageView2);
    }

    private void initialize(DataItem dataItem) {
        String Name = dataItem.getName();
        String volume24h = String.valueOf(dataItem.getListQuote().get(0).getVolume24h());
        String volume7d = String.valueOf(dataItem.getListQuote().get(0).getVolume7d());
        String volume30d = String.valueOf(dataItem.getListQuote().get(0).getVolume30d());
        String Persent = String.valueOf(dataItem.getListQuote().get(0).getPercentChange24h());

        fragmentDetailBinding.Name.setText(Name);
        fragmentDetailBinding.hours24.setText("$" + volume24h);
        fragmentDetailBinding.day7.setText("$" + volume7d);
        fragmentDetailBinding.day30.setText("$" + volume30d);



        loadChart(dataItem);
        SetDecimalsForPrice(dataItem);
        PersentColor(dataItem);
        TextColor(dataItem);

    }

    private void loadChart(DataItem dataItem) {

        Glide.with(fragmentDetailBinding.getRoot().getContext())
                .load("https://s3.coinmarketcap.com/generated/sparklines/web/7d/usd/" + dataItem.getId() + ".png")
                .into(fragmentDetailBinding.Charimg);
    }

    private void SetDecimalsForPrice(DataItem dataItem) {
        if (dataItem.getListQuote().get(0).getPrice() < 1){
            fragmentDetailBinding.Price.setText("$" + String.format("%.6f",dataItem.getListQuote().get(0).getPrice()));
        }else if (dataItem.getListQuote().get(0).getPrice() < 10){
            fragmentDetailBinding.Price.setText("$" + String.format("%.4f",dataItem.getListQuote().get(0).getPrice()));
        }else {
            fragmentDetailBinding.Price.setText("$" + String.format("%.2f",dataItem.getListQuote().get(0).getPrice()));
        }
    }

    private void TextColor(DataItem dataItem) {
        int greenColor = Color.parseColor("#FF00FF40");
        int redColor = Color.parseColor("#FFFF0000");
        int whiteColor = Color.parseColor("#FFFFFF");
        if (dataItem.getListQuote().get(0).getPercentChange24h() < 0){
            fragmentDetailBinding.Charimg.setColorFilter(redColor);
            fragmentDetailBinding.Percent24.setTextColor(Color.RED);
        }else if (dataItem.getListQuote().get(0).getPercentChange24h() > 0){
            fragmentDetailBinding.Charimg.setColorFilter(greenColor);
            fragmentDetailBinding.Percent24.setTextColor(Color.GREEN);
        }else {
            fragmentDetailBinding.Charimg.setColorFilter(whiteColor);
            fragmentDetailBinding.Percent24.setTextColor(Color.WHITE);
        }
    }

    private void PersentColor(DataItem dataItem) {
        if (dataItem.getListQuote().get(0).getPercentChange24h() > 0){
            fragmentDetailBinding.Percentimg.setBackgroundResource(R.drawable.ic_baseline_arrow_drop_up_24);
            fragmentDetailBinding.Percent24.setText(String.format("%.2f",dataItem.getListQuote().get(0).getPercentChange24h()) + "%");
        }else if (dataItem.getListQuote().get(0).getPercentChange24h() < 0){
            fragmentDetailBinding.Percentimg.setBackgroundResource(R.drawable.ic_baseline_arrow_drop_down_24);
            fragmentDetailBinding.Percent24.setText(String.format("%.2f",dataItem.getListQuote().get(0).getPercentChange24h()) + "%");
        }else {
            fragmentDetailBinding.Percent24.setText(String.format("%.2f",dataItem.getListQuote().get(0).getPercentChange24h()) + "%");
        }
    }
}