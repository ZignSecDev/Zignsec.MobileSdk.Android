package com.zignsec.identification.internal;

import com.google.gson.annotations.SerializedName;

public class ZignSecApiLivenessCompletionRequest {

    @SerializedName("result")
    String result;
    @SerializedName("image")
    String image;

    public ZignSecApiLivenessCompletionRequest(String result, String image){
        this.image = image;
        this.result = result;
    }
}
