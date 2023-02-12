package com.zignsec.identification.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import com.regula.documentreader.api.DocumentReader;
import com.regula.documentreader.api.enums.OnlineMode;
import com.regula.documentreader.api.enums.Scenario;
import com.regula.documentreader.api.params.OnlineProcessingConfig;
import com.regula.facesdk.FaceSDK;
import com.zignsec.identification.internal.ZignSecDocumentReaderCompletion;
import com.zignsec.identification.enums.ZignSecEnvironment;
import com.zignsec.identification.callbacks.ZignSecIdentificationCompletion;

import java.net.HttpURLConnection;

public class ZignSecIdentificationActivity extends Activity {
    private com.regula.documentreader.api.listener.NetworkInterceptorListener documentReaderInterceptorListener;
    private com.regula.facesdk.listener.NetworkInterceptorListener faceSdkInterceptorListener;
    private ZignSecEnvironment environment;

    private String sessionId;
    private String accessToken;

    public ZignSecIdentificationActivity(ZignSecEnvironment environment, String sessionId, String accessToken) {

        this.sessionId = sessionId;

        this.accessToken = accessToken;

        this.documentReaderInterceptorListener = new com.regula.documentreader.api.listener.NetworkInterceptorListener() {
            @Override
            public void onPrepareRequest(HttpURLConnection httpURLConnection) {
                httpURLConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
                httpURLConnection.setRequestProperty("x-zignsec-session-id", sessionId);
            }
        };

        this.faceSdkInterceptorListener =  new com.regula.facesdk.listener.NetworkInterceptorListener() {
            @Override
            public void onPrepareRequest(HttpURLConnection httpURLConnection) {
                httpURLConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
                httpURLConnection.setRequestProperty("x-zignsec-session-id", sessionId);
            }
        };

        this.environment = environment;

        this.setup();
    }

    @SuppressLint("MissingPermission")
    public void startIdentification(@NonNull Context context, @NonNull ZignSecIdentificationCompletion completion) {
        ZignSecDocumentReaderCompletion zsCompletion = new ZignSecDocumentReaderCompletion(context, this.sessionId, completion, this.environment, this.accessToken);

        DocumentReader.Instance().customization().edit().setShowBackgroundMask(true).apply();
        DocumentReader.Instance().showScanner(context, zsCompletion);
    }

    private void setup() {
        OnlineProcessingConfig onlineProcessingConfiguration = new OnlineProcessingConfig.Builder(OnlineMode.MANUAL)
                .setUrl(ZignSecEnvironment.getBaseUrl(this.environment) + "/proxy/docs")
                .setNetworkInterceptorListener(documentReaderInterceptorListener)
                .build();
        
        DocumentReader.Instance().functionality().edit().setManualMultipageMode(true).apply();
        DocumentReader.Instance().processParams().multipageProcessing = false;

        DocumentReader.Instance().startNewSession();

        onlineProcessingConfiguration.getProcessParam().setScenario(Scenario.SCENARIO_FULL_AUTH);

        DocumentReader.Instance().functionality().edit()
                .setOnlineProcessingConfiguration(onlineProcessingConfiguration)
                .apply();

        FaceSDK.Instance().setNetworkInterceptorListener(faceSdkInterceptorListener);

        FaceSDK.Instance().setServiceUrl(ZignSecEnvironment.getBaseUrl(this.environment) + "/proxy/faceapi");
    }
}
