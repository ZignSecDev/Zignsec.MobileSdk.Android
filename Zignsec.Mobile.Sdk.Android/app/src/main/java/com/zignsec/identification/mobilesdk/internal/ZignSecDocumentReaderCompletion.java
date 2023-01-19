package com.zignsec.identification.mobilesdk.internal;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.regula.documentreader.api.completions.IDocumentReaderCompletion;
import com.regula.documentreader.api.enums.DocReaderAction;
import com.regula.documentreader.api.enums.eGraphicFieldType;
import com.regula.documentreader.api.errors.DocumentReaderException;
import com.regula.documentreader.api.results.DocumentReaderResults;
import com.regula.facesdk.FaceSDK;
import com.regula.facesdk.configuration.LivenessConfiguration;
import com.regula.facesdk.enums.ImageType;
import com.regula.facesdk.enums.LivenessStatus;
import com.regula.facesdk.model.MatchFacesImage;
import com.regula.facesdk.model.results.LivenessResponse;
import com.regula.facesdk.request.MatchFacesRequest;
import com.zignsec.identification.mobilesdk.callbacks.ZignSecIdentificationCompletion;
import com.zignsec.identification.mobilesdk.enums.ZignSecIdentificationOptions;
import com.zignsec.identification.mobilesdk.exceptions.ZignSecIdentificationException;
import com.zignsec.identification.mobilesdk.results.ZignSecIdentificationResults;
import com.zignsec.identification.mobilesdk.results.ZignSecIdentificationTotalResult;

import java.util.Arrays;
import java.util.List;

public class ZignSecDocumentReaderCompletion implements IDocumentReaderCompletion {

    ZignSecIdentificationCompletion completion;
    ZignSecIdentificationOptions option;
    Context context;

    public ZignSecDocumentReaderCompletion(@NonNull Context context, ZignSecIdentificationOptions option, ZignSecIdentificationCompletion completion){
        this.completion = completion;
        this.option = option;
        this.context = context;
    }

    private Bitmap getImage(DocumentReaderResults results) {
        if (results.getGraphicFieldImageByType(eGraphicFieldType.GF_PORTRAIT) != null) {
            return results.getGraphicFieldImageByType(eGraphicFieldType.GF_PORTRAIT);
        } else {
            return null;
        }
    }

    private void startFaceFlow(DocumentReaderResults documentReaderResults) {
        LivenessConfiguration configuration = new LivenessConfiguration.Builder()
                    .setCameraId(0)
                    .setCameraSwitchEnabled(true)
                    .build();


            FaceSDK.Instance().startLiveness(context, configuration, livenessResponse -> {
                if (livenessResponse.getLiveness() == LivenessStatus.PASSED) {
                    matchFaces(documentReaderResults, livenessResponse);
                } else if (livenessResponse.getException() == null) {
                    this.completion.onCompleted(ZignSecIdentificationTotalResult.DECLINED, new ZignSecIdentificationResults(documentReaderResults, livenessResponse, null), null);
                } else {
                    this.completion.onCompleted(ZignSecIdentificationTotalResult.FAILED, new ZignSecIdentificationResults(documentReaderResults, livenessResponse, null), new ZignSecIdentificationException(livenessResponse.getException()));
                }
            });
    }

    private void matchFaces(DocumentReaderResults documentReaderResults, LivenessResponse livenessResponse){

        List<MatchFacesImage> images = Arrays.asList(
                new MatchFacesImage(getImage(documentReaderResults), ImageType.PRINTED),
                new MatchFacesImage(livenessResponse.getBitmap(), ImageType.LIVE)
        );

        MatchFacesRequest request = new MatchFacesRequest(images);

        FaceSDK.Instance().matchFaces(request, faceMatchresponse -> {
            if (faceMatchresponse.getException() != null){
                this.completion.onCompleted(ZignSecIdentificationTotalResult.FAILED, new ZignSecIdentificationResults(documentReaderResults, livenessResponse, faceMatchresponse), new ZignSecIdentificationException(faceMatchresponse.getException()));
            } else {
                this.completion.onCompleted(ZignSecIdentificationTotalResult.ACCEPTED, new ZignSecIdentificationResults(documentReaderResults, livenessResponse, faceMatchresponse), null);
            }
        });
    }

    @Override
    public void onCompleted(int action, @Nullable DocumentReaderResults documentReaderResults, @Nullable DocumentReaderException e)
        {
            if (e != null) {
                completion.onCompleted(ZignSecIdentificationTotalResult.FAILED, new ZignSecIdentificationResults(documentReaderResults), new ZignSecIdentificationException(e));
            }

            if (action == DocReaderAction.COMPLETE) {
                if (option == ZignSecIdentificationOptions.DOCUMENT_SCAN_ONLY) {
                    if (documentReaderResults.documentType.size() == 1) {
                        this.completion.onCompleted(ZignSecIdentificationTotalResult.ACCEPTED, new ZignSecIdentificationResults(documentReaderResults), null);
                    } else {
                        this.completion.onCompleted(ZignSecIdentificationTotalResult.DECLINED, new ZignSecIdentificationResults(documentReaderResults), null);
                    }
                } else {
                    if (documentReaderResults.documentType.size() == 1) {
                        startFaceFlow(documentReaderResults);
                    } else {
                        this.completion.onCompleted(ZignSecIdentificationTotalResult.DECLINED, new ZignSecIdentificationResults(documentReaderResults), null);
                    }
                }
            } else if (action == DocReaderAction.CANCEL) {
                completion.onCompleted(ZignSecIdentificationTotalResult.CANCELLED, new ZignSecIdentificationResults(documentReaderResults), null);
            } else if (action == DocReaderAction.TIMEOUT) {
                completion.onCompleted(ZignSecIdentificationTotalResult.TIMEOUT, new ZignSecIdentificationResults(documentReaderResults), null);
            }
            else if (action == DocReaderAction.PROCESSING_ON_SERVICE || action == DocReaderAction.PROCESS || action == DocReaderAction.NOTIFICATION || action == DocReaderAction.MORE_PAGES_AVAILABLE || action == DocReaderAction.PROCESS_IR_FRAME || action == DocReaderAction.PROCESS_WHITE_FLASHLIGHT || action == DocReaderAction.PROCESS_WHITE_UV_IMAGES){
                /* Ignore These Events */
            }
            else {
                /* Treat all other Events as failures*/
                completion.onCompleted(ZignSecIdentificationTotalResult.FAILED, new ZignSecIdentificationResults(documentReaderResults), null);
            }
        }
}