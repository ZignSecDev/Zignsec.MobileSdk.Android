package com.zignsec.identification.results;

import com.regula.documentreader.api.results.DocumentReaderResults;
import com.regula.facesdk.model.results.LivenessResponse;
import com.regula.facesdk.model.results.matchfaces.MatchFacesResponse;

public class ZignSecIdentificationResults {

    private DocumentReaderResults documentReaderResults;
    private MatchFacesResponse matchFacesResponse;
    private LivenessResponse livenessResponse;

    public ZignSecIdentificationResults(DocumentReaderResults documentReaderResults){
        this.setDocumentReaderResults(documentReaderResults);
    }

    public ZignSecIdentificationResults(DocumentReaderResults documentReaderResults, LivenessResponse livenessResponse, MatchFacesResponse matchFacesResponse){
        this.setDocumentReaderResults(documentReaderResults);
        this.setLivenessResponse(livenessResponse);
        this.setMatchFacesResponse(matchFacesResponse);
    }

    public DocumentReaderResults getDocumentReaderResults() {
        return documentReaderResults;
    }

    public void setDocumentReaderResults(DocumentReaderResults documentReaderResults) {
        this.documentReaderResults = documentReaderResults;
    }

    public MatchFacesResponse getMatchFacesResponse() {
        return matchFacesResponse;
    }

    public void setMatchFacesResponse(MatchFacesResponse matchFacesResponse) {
        this.matchFacesResponse = matchFacesResponse;
    }

    public LivenessResponse getLivenessResponse() {
        return livenessResponse;
    }

    public void setLivenessResponse(LivenessResponse livenessResponse) {
        this.livenessResponse = livenessResponse;
    }
}
