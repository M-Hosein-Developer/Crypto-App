package com.example.myapplication.Hilt.Madules;

import android.content.Context;

import com.example.myapplication.RoomDb.AppDataBase;
import com.example.myapplication.RoomDb.RoomDao;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class HiltRoomModule {

    @Provides
    @Singleton
    AppDataBase ProvideAppDatabase(@ApplicationContext Context context){
        return AppDataBase.getInstance(context);
    }

    @Provides
    @Singleton
    RoomDao ProvideRoomDao(AppDataBase appDataBase){
        return appDataBase.roomDao();
    }
}

