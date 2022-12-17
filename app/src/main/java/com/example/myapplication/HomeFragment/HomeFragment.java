package com.example.myapplication.HomeFragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.viewpager2.widget.ViewPager2;

import com.example.application.R;
import com.example.application.databinding.FragmentHomeBinding;
import com.example.myapplication.HomeFragment.Adapter.TopCoinRvAdapter;
import com.example.myapplication.HomeFragment.Adapter.TopGainLoseresAdapter;
import com.example.myapplication.HomeFragment.Adapter.sliderImageAdapter;
import com.example.myapplication.MainActivity;
import com.example.myapplication.Models.cryptolistmodel.AllMarketModel;
import com.example.myapplication.Models.cryptolistmodel.DataItem;
import com.example.myapplication.RoomDb.Entites.MarketListEntity;
import com.example.myapplication.viewmodels.AppViewmodel;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

@AndroidEntryPoint

public class HomeFragment extends Fragment {

    //Data Binding
    FragmentHomeBinding fragmentHomeBinding;

    //استفاده از بخش های مورد نیاز Main Activity
    MainActivity mainActivity;

    //شاخت شی از app view model
    AppViewmodel appViewmodel;

    @Inject
    @Named("hosein")
    String name;


    public List<String> top_wants = Arrays.asList("BTC","ETH","BNB","ADA","XRP","DOGE","DOT","UNI","LTC","LINK");
    TopCoinRvAdapter topCoinRvAdapter;
    CompositeDisposable compositeDisposable;

    TopGainLoseresAdapter topGainLoseresAdapter;

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
        fragmentHomeBinding = DataBindingUtil.inflate(inflater , R.layout.fragment_home , container ,false);
        //مقدار دهی app view model
        appViewmodel = new ViewModelProvider(requireActivity()).get(AppViewmodel.class);
        //متد تنظیمات ویو پیجر
        setupViewPager2();

        compositeDisposable = new CompositeDisposable();

        getAllMarketDataFromDb();

        setupTablayouTopGainLose(fragmentHomeBinding.topGainindicator , fragmentHomeBinding.topLoseindicator);

        // Inflate the layout for this fragment
        //fragmentHomeBinding.getRoot() رو به جای بخشی که inflate میشد قرار میدیم
        return fragmentHomeBinding.getRoot();
    }

    private void setupTablayouTopGainLose(View topGainindicator, View topLoseindicator) {
        topGainLoseresAdapter = new TopGainLoseresAdapter(this);
        fragmentHomeBinding.viewPager2.setAdapter(topGainLoseresAdapter);


        Animation gainAnimIn = AnimationUtils.loadAnimation(getActivity().getApplicationContext(),R.anim.slide_from_left);
        Animation gainAnimOut = AnimationUtils.loadAnimation(getActivity().getApplicationContext(),R.anim.slide_out_left);
        Animation loseAnimIn = AnimationUtils.loadAnimation(getActivity().getApplicationContext(),R.anim.slide_from_right);
        Animation loseAnimOut = AnimationUtils.loadAnimation(getActivity().getApplicationContext(),R.anim.slide_out_right);

        fragmentHomeBinding.viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                if (position == 0){
                    topLoseindicator.startAnimation(loseAnimOut);
                    topLoseindicator.setVisibility(View.GONE);
                    topGainindicator.setVisibility(View.VISIBLE);
                    topGainindicator.startAnimation(gainAnimIn);

                }else {
                    topGainindicator.startAnimation(gainAnimOut);
                    topGainindicator.setVisibility(View.GONE);
                    topLoseindicator.setVisibility(View.VISIBLE);
                    topLoseindicator.startAnimation(loseAnimIn);
                }
            }
        });

        new TabLayoutMediator(fragmentHomeBinding.tablayout, fragmentHomeBinding.viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                if (position == 0){
                    tab.setText("Top Gainers");
                }else {
                    tab.setText("Top Losers");
                }
            }
        }).attach();

    }

    private void setupViewPager2(){
        appViewmodel.getMutableLiveData().observe((LifecycleOwner) getActivity(), new Observer<ArrayList<Integer>>() {
            @Override
            public void onChanged(ArrayList<Integer> pics) {
                fragmentHomeBinding.viewPagerimageSlider.setAdapter(new sliderImageAdapter(pics));
                fragmentHomeBinding.viewPagerimageSlider.setOffscreenPageLimit(3);
                //fragmentHomeBinding.viewPagerimageSlider.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setupToolbar(View view){
        NavController navController = Navigation.findNavController(view);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.homeFragment)
                .setOpenableLayout(mainActivity.drawerLayout).build();

        Toolbar toolbar = view.findViewById(R.id.toolbar);

        NavigationUI.setupWithNavController(toolbar , navController , appBarConfiguration);

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController navController, @NonNull NavDestination navDestination, @Nullable Bundle bundle) {
                if (navDestination.getId() == R.id.homeFragment){
                    toolbar.setNavigationIcon(R.drawable.ic_baseline_sort_24);
                    toolbar.setTitle("Crypto");
                }
            }
        });
    }

    private void getAllMarketDataFromDb() {
        Disposable disposable = appViewmodel.getAllMarketData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<MarketListEntity>() {
                    @Override
                    public void accept(MarketListEntity marketListEntity) throws Throwable {
                        AllMarketModel allMarketModel = marketListEntity.getAllMarketModel();

                        ArrayList<DataItem> top10 = new ArrayList<>();
                        for (int i = 0; i < allMarketModel.getRootData().getCryptoCurrencyList().size(); i++) {
                            for (int j = 0; j < top_wants.size(); j++) {
                                String coin_name = top_wants.get(j);
                                if (allMarketModel.getRootData().getCryptoCurrencyList().get(i).getSymbol().equals(coin_name)) {
                                    DataItem dataItem = allMarketModel.getRootData().getCryptoCurrencyList().get(i);
                                    top10.add(dataItem);
                                }
                            }
                        }

                        if (fragmentHomeBinding.TopCoinRv.getAdapter() != null){
                            topCoinRvAdapter = (TopCoinRvAdapter) fragmentHomeBinding.TopCoinRv.getAdapter();
                            topCoinRvAdapter.updateData(top10);
                        }else{
                            topCoinRvAdapter = new TopCoinRvAdapter(top10);
                            fragmentHomeBinding.TopCoinRv.setAdapter(topCoinRvAdapter);
                        }
                    }
                });
        compositeDisposable.add(disposable);
    }
}

