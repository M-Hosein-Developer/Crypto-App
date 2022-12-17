package com.example.myapplication.WatchListFragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.application.R;
import com.example.application.databinding.FragmentWatchListBinding;
import com.example.myapplication.MainActivity;
import com.example.myapplication.Models.cryptolistmodel.AllMarketModel;
import com.example.myapplication.Models.cryptolistmodel.DataItem;
import com.example.myapplication.RoomDb.Entites.MarketListEntity;
import com.example.myapplication.viewmodels.AppViewmodel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

@AndroidEntryPoint
public class WatchListFragment extends Fragment {

    //Data Binding
    FragmentWatchListBinding fragmentWatchListBinding;

    //استفاده از بخش های مورد نیاز Main Activity
    MainActivity mainActivity;

    AppViewmodel appViewmodel;

    WatchListRvAdapter watchListRvAdapter;
    CompositeDisposable compositeDisposable;

    ArrayList<String> bookmarksArray;
    ArrayList<DataItem> finalData;
    List<DataItem> dataItemList;


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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Data Binding fragment
        fragmentWatchListBinding = DataBindingUtil.inflate(inflater , R.layout.fragment_watch_list , container ,false);
        finalData = new ArrayList<>();
        compositeDisposable = new CompositeDisposable();

        setupViewModel();
        ReadDataStore();
        getMarketListDataFromDb();

        return fragmentWatchListBinding.getRoot();
    }

    private void getMarketListDataFromDb() {
        Disposable disposable = appViewmodel.getAllMarketData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<MarketListEntity>() {
                    @Override
                    public void accept(MarketListEntity roomMarketEntity) throws Throwable {
                        AllMarketModel allMarketModel = roomMarketEntity.getAllMarketModel();
                        dataItemList = allMarketModel.getRootData().getCryptoCurrencyList();

                        finalData.clear();
                        for (int i = 0;i < bookmarksArray.size();i++){
                            for (int j = 0;j < dataItemList.size();j++){
                                if (bookmarksArray.get(i).equals(dataItemList.get(j).getSymbol())){
                                    finalData.add(dataItemList.get(j));
                                }
                            }
                        }

                        Log.e("Tag" , "DataItem" + dataItemList);
                        Log.e("TAg" , "BookmarkArray "  + bookmarksArray);
                        Log.e("TAg" , "final "  + finalData);


                        LinearLayoutManager llm = new LinearLayoutManager(getContext());
                        llm.setOrientation(LinearLayoutManager.VERTICAL);
                        fragmentWatchListBinding.WatchLisetRv.setLayoutManager(llm);

                        if (fragmentWatchListBinding.WatchLisetRv.getAdapter() != null) {
                            watchListRvAdapter = (WatchListRvAdapter) fragmentWatchListBinding.WatchLisetRv.getAdapter();

                        } else {
                            watchListRvAdapter = new WatchListRvAdapter(finalData);
                            fragmentWatchListBinding.WatchLisetRv.setAdapter(watchListRvAdapter);
                        }
                    }
                });
    }

    private void ReadDataStore() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(requireActivity());
        Gson gson = new Gson();
        String json = sharedPrefs.getString("bookmarks", String.valueOf(new ArrayList<String>()));
        Type type = new TypeToken<ArrayList<String>>(){}.getType();
        bookmarksArray = gson.fromJson(json, type);
        Log.e("TAG", "ReadDataStore: " + bookmarksArray);
    }

    private void setupViewModel() {
        appViewmodel = new ViewModelProvider(requireActivity()).get(AppViewmodel.class);
    }


    private void setupToolbar(View view){
        NavController navController = Navigation.findNavController(view);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.watchListFragment)
                .setOpenableLayout(mainActivity.drawerLayout).build();

        Toolbar toolbar = view.findViewById(R.id.toolbar);

        NavigationUI.setupWithNavController(toolbar , navController , appBarConfiguration);

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController navController, @NonNull NavDestination navDestination, @Nullable Bundle bundle) {
                if (navDestination.getId() == R.id.watchListFragment){
                    toolbar.setNavigationIcon(R.drawable.ic_baseline_sort_24);
                    toolbar.setTitle("Watch Lists");
                }
            }
        });
    }
}