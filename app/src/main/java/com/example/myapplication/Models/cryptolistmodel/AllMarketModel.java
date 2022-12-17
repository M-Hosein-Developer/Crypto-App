package com.example.myapplication.Models.cryptolistmodel;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AllMarketModel {

    @SerializedName("data")
    private RootData data;

    @SerializedName("status")
    private ListStatus listStatus;

    public RootData getRootData(){
        return data;
    }

    public ListStatus getStatus(){
        return listStatus;
    }


    public static class ListStatus {

        @SerializedName("timestamp")
        private String timestamp;

        @SerializedName("error_code")
        private String errorcode;

        @SerializedName("error_message")
        private String errormessage;

        @SerializedName("elapsed")
        private String elapsed;

        @SerializedName("credit_count")
        private int creditcount;

        public String getTimestamp(){
            return timestamp;
        }

        public String getErrorcode(){
            return errorcode;
        }

        public String getErrormessage(){
            return errormessage;
        }

        public String getElapsed(){
            return elapsed;
        }

        public int getCreditcount(){
            return creditcount;
        }
    }

    public static class RootData{

        @SerializedName("totalCount")
        private String totaCount;

        @SerializedName("cryptoCurrencyList")
        private List<DataItem> cryptoCurrencyList;

        public List<DataItem> getCryptoCurrencyList(){return cryptoCurrencyList;}

        public String getTotaCount(){return totaCount;}
    }
}

