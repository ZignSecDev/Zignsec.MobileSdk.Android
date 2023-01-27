package com.zignsec.identification.internal;

import com.zignsec.identification.dtos.ZignSecIdentificationSessionResult;

import retrofit2.Call;

import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ZignSecApi {

    @POST("/proxy/faceapi/api/liveness/complete")
    Call<ZignSecIdentificationSessionResult> finaliseLiveness(@Header ("Authorization") String accessToken, @Header ("x-zignsec-session-id") String sessionId, @Body ZignSecApiLivenessCompletionRequest request);
}
