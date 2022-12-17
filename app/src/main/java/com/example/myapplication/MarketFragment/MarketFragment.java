package com.example.myapplication.MarketFragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.RecyclerView;

import com.example.application.R;
import com.example.application.databinding.FragmentMarketBinding;
import com.example.myapplication.MainActivity;
import com.example.myapplication.Models.cryptolistmodel.AllMarketModel;
import com.example.myapplication.Models.cryptolistmodel.DataItem;
import com.example.myapplication.RoomDb.Entites.MarketListEntity;
import com.example.myapplication.viewmodels.AppViewmodel;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class MarketFragment extends Fragment {

    //Data Binding
    FragmentMarketBinding fragmentMarketBinding;

    //استفاده از بخش های مورد نیاز Main Activity
    MainActivity mainActivity;

    CollapsingToolbarLayout collapsingToolbarLayout;

    AppViewmodel appViewmodel;

    List<DataItem> dataItemList;
    marketRv_Adapter marketRvAdapter;
    CompositeDisposable compositeDisposable;

    ArrayList<DataItem> filteredList;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupToolbar(view);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentMarketBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_market,container,false);

        compositeDisposable = new CompositeDisposable();

        filteredList = new ArrayList<>();

        setupSearchBox();
        setupViewModel();
        getMarketListDataFromDb();



        return fragmentMarketBinding.getRoot();
    }

    private void setupSearchBox(){
        fragmentMarketBinding.searchEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
        });
    }

    private void filter(String name){
        filteredList.clear();

        if (dataItemList == null){

        }else{
            for (DataItem item : Objects.requireNonNull(dataItemList)){
                if (item.getSymbol().toLowerCase().contains(name.toLowerCase()) || item.getName().toLowerCase().contains(name.toLowerCase())){
                    filteredList.add(item);
                }
            }

            marketRvAdapter.updateData(filteredList);
            marketRvAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onChanged() {
                    super.onChanged();
                    checkEmpty();
                }

                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    super.onItemRangeInserted(positionStart, itemCount);
                    checkEmpty();
                }

                @Override
                public void onItemRangeRemoved(int positionStart, int itemCount) {
                    super.onItemRangeRemoved(positionStart, itemCount);
                    checkEmpty();
                }

                void checkEmpty(){
                    if (marketRvAdapter.getItemCount() == 0){
                        fragmentMarketBinding.itemnotFoundText.setVisibility(View.VISIBLE);
                    }else {
                        fragmentMarketBinding.itemnotFoundText.setVisibility(View.GONE);
                    }
                }
            });

        }
    }



    private void getMarketListDataFromDb() {
        Disposable disposable = appViewmodel.getAllMarketData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<MarketListEntity>() {
                    @Override
                    public void accept(MarketListEntity marketListEntity) throws Throwable {
                        AllMarketModel allMarketModel = marketListEntity.getAllMarketModel();
                        dataItemList = allMarketModel.getRootData().getCryptoCurrencyList();

                        if (fragmentMarketBinding.marketRv.getAdapter() == null){
                            marketRvAdapter = new marketRv_Adapter((ArrayList<DataItem>) dataItemList);
                            fragmentMarketBinding.marketRv.setAdapter(marketRvAdapter);
                        }else {
                            marketRvAdapter = (marketRv_Adapter) fragmentMarketBinding.marketRv.getAdapter();

                            if (filteredList.isEmpty() || filteredList.size() == 2000){
                                marketRvAdapter.updateData((ArrayList<DataItem>) dataItemList);
                            }else {
                                //get All new Data when user searching and filtering
                                marketRvAdapter.updateData(filteredList);
                            }
                        }


                    }
                });
        compositeDisposable.add(disposable);
    }


    private void setupViewModel() {
        appViewmodel = new ViewModelProvider(requireActivity()).get(AppViewmodel.class);
    }

    private void setupToolbar(View view){
        NavController navController = Navigation.findNavController(view);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.marketFragment)
                .setOpenableLayout(mainActivity.drawerLayout).build();

        Toolbar toolbar = view.findViewById(R.id.toolbar);

        collapsingToolbarLayout = view.findViewById(R.id.collapsing_market_tb);


        NavigationUI.setupWithNavController(collapsingToolbarLayout , toolbar , navController , appBarConfiguration);

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController navController, @NonNull NavDestination navDestination, @Nullable Bundle bundle) {
                if (navDestination.getId() == R.id.marketFragment){
                    collapsingToolbarLayout.setTitleEnabled(false);
                    toolbar.setNavigationIcon(R.drawable.ic_baseline_sort_24);
                    toolbar.setTitle("Market");
                    toolbar.setTitleTextColor(Color.WHITE);
                }
            }
        });
    }
}