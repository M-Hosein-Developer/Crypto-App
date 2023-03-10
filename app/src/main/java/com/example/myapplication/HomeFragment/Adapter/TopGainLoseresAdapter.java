package com.example.myapplication.HomeFragment.Adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.myapplication.HomeFragment.TopGainLoseFrag;

public class TopGainLoseresAdapter extends FragmentStateAdapter {

    public TopGainLoseresAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        Fragment fragment = new TopGainLoseFrag();

        Bundle args = new Bundle();
        args.putInt("pos" , position);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
