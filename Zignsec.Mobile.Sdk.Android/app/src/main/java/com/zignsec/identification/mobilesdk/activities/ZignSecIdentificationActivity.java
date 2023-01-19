package com.zignsec.identification.mobilesdk.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import com.regula.documentreader.api.DocumentReader;
import com.regula.documentreader.api.enums.OnlineMode;
import com.regula.documentreader.api.enums.Scenario;
import com.regula.documentreader.api.params.OnlineProcessingConfig;
import com.regula.facesdk.FaceSDK;
import com.zignsec.identification.mobilesdk.callbacks.ZignSecIdentificationCompletion;
import com.zignsec.identification.mobilesdk.enums.ZignSecEnvironment;
import com.zignsec.identification.mobilesdk.enums.ZignSecIdentificationOptions;
import com.zignsec.identification.mobilesdk.internal.ZignSecDocumentReaderCompletion;

import java.net.HttpURLConnection;

public class ZignSecIdentificationActivity extends Activity {
    private com.regula.documentreader.api.listener.NetworkInterceptorListener documentReaderInterceptorListener;
    private com.regula.facesdk.listener.NetworkInterceptorListener faceSdkInterceptorListener;
    private ZignSecIdentificationOptions option;

    private ZignSecEnvironment environment;

    public ZignSecIdentificationActivity(ZignSecEnvironment environment, ZignSecIdentificationOptions option, String accessToken) {
        this.documentReaderInterceptorListener = new com.regula.documentreader.api.listener.NetworkInterceptorListener() {
            @Override
            public void onPrepareRequest(HttpURLConnection httpURLConnection) {
                httpURLConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
            }
        };

        this.faceSdkInterceptorListener =  new com.regula.facesdk.listener.NetworkInterceptorListener() {
            @Override
            public void onPrepareRequest(HttpURLConnection httpURLConnection) {
                httpURLConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
            }
        };

        this.environment = environment;
        this.option = option;

        this.setup();
    }

    public String getBaseUrl() {
        if (environment == ZignSecEnvironment.DEV) {
            return "https://dev-gateway.zignsec.com";
        }
        else if (environment == ZignSecEnvironment.TEST) {
            return "https://test-gateway.zignsec.com";
        } else {
            return "https://gateway.zignsec.com";
        }
    }

    @SuppressLint("MissingPermission")
    public void startIdentification(@NonNull Context context, @NonNull ZignSecIdentificationCompletion completion) {
        ZignSecDocumentReaderCompletion zsCompletion = new ZignSecDocumentReaderCompletion(context, option, completion);

        DocumentReader.Instance().showScanner(context, zsCompletion);
    }

    private void setup() {
        OnlineProcessingConfig onlineProcessingConfiguration = new OnlineProcessingConfig.Builder(OnlineMode.MANUAL)
                .setUrl(getBaseUrl() + "/proxy/docs")
                .setNetworkInterceptorListener(documentReaderInterceptorListener)
                .build();

        onlineProcessingConfiguration.getProcessParam().setScenario(Scenario.SCENARIO_FULL_PROCESS);

        DocumentReader.Instance().functionality().edit()
                .setOnlineProcessingConfiguration(onlineProcessingConfiguration)
                .apply();

        FaceSDK.Instance().setNetworkInterceptorListener(faceSdkInterceptorListener);

        FaceSDK.Instance().setServiceUrl(getBaseUrl() + "/regula/faceapi");
    }
}
