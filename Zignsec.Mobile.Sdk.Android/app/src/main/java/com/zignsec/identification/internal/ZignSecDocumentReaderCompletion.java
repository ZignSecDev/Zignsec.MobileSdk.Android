package com.zignsec.identification.internal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.regula.documentreader.api.DocumentReader;
import com.regula.documentreader.api.completions.IDocumentReaderCompletion;
import com.regula.documentreader.api.enums.DocReaderAction;
import com.regula.documentreader.api.errors.DocumentReaderException;
import com.regula.documentreader.api.results.DocumentReaderResults;
import com.regula.facesdk.FaceSDK;
import com.regula.facesdk.configuration.LivenessConfiguration;
import com.regula.facesdk.enums.LivenessStatus;
import com.zignsec.identification.callbacks.ZignSecIdentificationCompletion;
import com.zignsec.identification.dtos.ZignSecDocumentAnalysisStatus;
import com.zignsec.identification.dtos.ZignSecIdentificationSessionResponse;
import com.zignsec.identification.dtos.ZignSecIdentificationSessionResult;
import com.zignsec.identification.dtos.ZignSecLivenessAnalysisStatus;
import com.zignsec.identification.enums.ZignSecEnvironment;
import com.zignsec.identification.exceptions.ZignSecIdentificationException;
import java.io.ByteArrayOutputStream;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ZignSecDocumentReaderCompletion implements IDocumentReaderCompletion {

    ZignSecIdentificationCompletion completion;
    Context context;
    ZignSecEnvironment environment;
    String accessToken;
    String sessionId;

    public ZignSecDocumentReaderCompletion(@NonNull Context context, String sessionId, ZignSecIdentificationCompletion completion, ZignSecEnvironment environment, String accessToken){
        this.completion = completion;
        this.context = context;
        this.environment = environment;
        this.sessionId = sessionId;
        this.accessToken = accessToken;
    }

    private void startFaceFlow() {
        LivenessConfiguration configuration = new LivenessConfiguration.Builder()
                    .setCameraId(0)
                    .setCameraSwitchEnabled(true)
                    .build();

            FaceSDK.Instance().startLiveness(context, configuration, livenessResponse -> {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(ZignSecEnvironment.getBaseUrl(this.environment) + "/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                ZignSecApi apiService =
                        retrofit.create(ZignSecApi.class);

                ZignSecApiLivenessCompletionRequest request;

                if (livenessResponse.getLiveness() == LivenessStatus.PASSED) {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    livenessResponse.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                    byte[] imageByteArray = byteArrayOutputStream .toByteArray();
                    String encodedImageByteArray = Base64.encodeToString(imageByteArray, Base64.DEFAULT);
                    request = new ZignSecApiLivenessCompletionRequest("0", encodedImageByteArray);
                } else {
                    request = new ZignSecApiLivenessCompletionRequest("1", null);
                }

                Call<ZignSecIdentificationSessionResult> call =  apiService.finaliseLiveness("Bearer " + this.accessToken, this.sessionId, request);

                call.enqueue(new Callback<ZignSecIdentificationSessionResult>() {
                    @Override
                    public void onResponse(Call<ZignSecIdentificationSessionResult> call, Response<ZignSecIdentificationSessionResult> response) {
                        if (response.code() >= 200 && response.code() <= 299){
                            completion.onCompleted(response.body(), null);
                        }
                        else {
                            completion.onCompleted(null, new ZignSecIdentificationException(new Exception("Network Error when calling Liveness.")));
                        }
                    }

                    @Override
                    public void onFailure(Call<ZignSecIdentificationSessionResult> call, Throwable t) {
                        completion.onCompleted(null, new ZignSecIdentificationException(new Exception(t.getMessage())));
                    }
                });

                return;
            });
    }

    private ZignSecIdentificationSessionResponse getZignSecIdentificationSessionResponseFromJson(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return objectMapper.readValue(json, ZignSecIdentificationSessionResponse.class);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCompleted(int action, @Nullable DocumentReaderResults documentReaderResults, @Nullable DocumentReaderException e)
        {
            if (e != null) {
                completion.onCompleted(null, new ZignSecIdentificationException(e));
            }

            if (action == DocReaderAction.COMPLETE) {

           if (documentReaderResults.morePagesAvailable != 0) {
               DocumentReader.Instance().startNewPage();
               DocumentReader.Instance().customization().edit().setResultStatus("A Two-Sided document has been detected. Please take a photo of the other side.").apply();
               DocumentReader.Instance().customization().edit().setStatusTextSize(20).apply();


               DocumentReader.Instance().showScanner(this.context, this);
                    return;
                }

                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                ZignSecIdentificationSessionResponse response = new ZignSecIdentificationSessionResponse();

                try {
                    response = getZignSecIdentificationSessionResponseFromJson(documentReaderResults.rawResult);
                } catch (JsonProcessingException ex) {
                    completion.onCompleted(null, new ZignSecIdentificationException(e));
                    return;
                }

                if (response.getSession().getResult().getDocumentAnalysis().getStatus() == ZignSecDocumentAnalysisStatus.Accepted &&
                    response.getSession().getResult().getLivenessAnalysis().getStatus() == ZignSecLivenessAnalysisStatus.Requested){
                    startFaceFlow();
                } else {
                    completion.onCompleted(response.getSession(), null);
                }

            } else if (action == DocReaderAction.CANCEL) {
                completion.onCompleted(null, new ZignSecIdentificationException(new Exception("User Cancelled Flow.")));
            } else if (action == DocReaderAction.TIMEOUT) {
                completion.onCompleted(null, new ZignSecIdentificationException(new Exception("Flow Timed Out.")));
            }
            else if (action == DocReaderAction.PROCESSING_ON_SERVICE || action == DocReaderAction.PROCESS || action == DocReaderAction.NOTIFICATION || action == DocReaderAction.MORE_PAGES_AVAILABLE || action == DocReaderAction.PROCESS_IR_FRAME || action == DocReaderAction.PROCESS_WHITE_FLASHLIGHT || action == DocReaderAction.PROCESS_WHITE_UV_IMAGES){
                /* Ignore These Events */
            }
            else {
                /* Treat all other Events as failures*/
                completion.onCompleted(null, new ZignSecIdentificationException(new Exception("Something went wrong during the Document Scan.")));
            }
        }
}