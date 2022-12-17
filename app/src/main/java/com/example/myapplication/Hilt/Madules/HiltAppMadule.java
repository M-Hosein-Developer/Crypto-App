package com.example.myapplication.Hilt.Madules;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;
import dagger.hilt.android.qualifiers.ActivityContext;

@Module

@InstallIn(ActivityComponent.class)
public class HiltAppMadule {

    @Provides
    @Named("hosein")
    String ProvideName(){
        return "hosein";
    }

    @Provides
    @Named("lastname")
    String ProvidelastName(){
        return "ali";
    }

    @Provides
    ConnectivityManager ProvideConnectivityManager(@ActivityContext Context context){
        return (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    @Provides
    NetworkRequest ProvideNetworkRequest(){
        return new NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build();
    }
}



