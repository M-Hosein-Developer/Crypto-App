package com.example.myapplication.HomeFragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.application.R;
import com.example.application.databinding.FragmentTopGainLoseBinding;
import com.example.myapplication.HomeFragment.Adapter.GainLoserRvAdapter;
import com.example.myapplication.Models.cryptolistmodel.AllMarketModel;
import com.example.myapplication.Models.cryptolistmodel.DataItem;
import com.example.myapplication.viewmodels.AppViewmodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class TopGainLoseFrag extends Fragment {

    FragmentTopGainLoseBinding fragmentTopGainLoseBinding;
    GainLoserRvAdapter gainLoserRvAdapter;
    AppViewmodel appViewmodel;
    List<DataItem> data;
    CompositeDisposable compositeDisposable;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        fragmentTopGainLoseBinding = DataBindingUtil.inflate(inflater , R.layout.fragment_top_gain_lose , container , false);
        compositeDisposable = new CompositeDisposable();

        Bundle args = getArguments();
        int pos = args.getInt("pos");

        appViewmodel = new ViewModelProvider(requireActivity()).get(AppViewmodel.class);

        setupRecyclerView(pos);

        return fragmentTopGainLoseBinding.getRoot();
    }

    private void setupRecyclerView(int pos) {

        Disposable disposable = appViewmodel.getAllMarketData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(roomMarketEntity -> {
                    AllMarketModel allMarketModel = roomMarketEntity.getAllMarketModel();
                    data = allMarketModel.getRootData().getCryptoCurrencyList();

                    Collections.sort(data, new Comparator<DataItem>() {
                        @Override
                        public int compare(DataItem o1, DataItem o2) {
                            return Integer.valueOf((int) o1.getListQuote().get(0).getPercentChange24h()).compareTo((int) o2.getListQuote().get(0).getPercentChange24h());
                        }
                    });

                    try {
                        ArrayList<DataItem> dataItems = new ArrayList<>();
                        //if page was top Gainers
                        if (pos == 0){
                            //get 10 last Item
                            for (int i = 0; i < 10; i++){
                                dataItems.add(data.get(data.size() -1 - i));
                            }

                            //if page was top Loser
                        }else  if (pos == 1){
                            //get 10 first Item
                            for (int i = 0; i < 10; i++){
                                dataItems.add(data.get(i));
                            }
                        }

                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                        fragmentTopGainLoseBinding.gainLoseRv.setLayoutManager(linearLayoutManager);

                        if (fragmentTopGainLoseBinding.gainLoseRv.getAdapter() == null){
                            gainLoserRvAdapter = new GainLoserRvAdapter(dataItems);
                            fragmentTopGainLoseBinding.gainLoseRv.setAdapter(gainLoserRvAdapter);
                        }else{
                            gainLoserRvAdapter = (GainLoserRvAdapter) fragmentTopGainLoseBinding.gainLoseRv.getAdapter();
                            gainLoserRvAdapter.updateData(dataItems);
                        }
                        fragmentTopGainLoseBinding.progressBar.setVisibility(View.GONE);
                    }catch (Exception e){
                        Log.e("exeption" , "setupRecyclerView: " + e.getMessage());
                    }
                });
        compositeDisposable.add(disposable);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}