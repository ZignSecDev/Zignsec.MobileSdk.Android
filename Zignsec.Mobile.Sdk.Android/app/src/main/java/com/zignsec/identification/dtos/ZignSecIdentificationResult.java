package com.zignsec.identification.dtos;

public class ZignSecIdentificationResult {
    private ZignSecIdentity identity;
    private ZignSecDocumentAnalysis documentAnalysis;
    private ZignSecLivenessAnalysis livenessAnalysis;
    private ZignSecFaceMatchAnalysis faceMatchAnalysis;

    public ZignSecIdentity getIdentity() {
        return identity;
    }

    public void setIdentity(ZignSecIdentity identity) {
        this.identity = identity;
    }

    public ZignSecDocumentAnalysis getDocumentAnalysis() {
        return documentAnalysis;
    }

    public void setDocumentAnalysis(ZignSecDocumentAnalysis documentAnalysis) {
        this.documentAnalysis = documentAnalysis;
    }

    public ZignSecLivenessAnalysis getLivenessAnalysis() {
        return livenessAnalysis;
    }

    public void setLivenessAnalysis(ZignSecLivenessAnalysis livenessAnalysis) {
        this.livenessAnalysis = livenessAnalysis;
    }

    public ZignSecFaceMatchAnalysis getFaceMatchAnalysis() {
        return faceMatchAnalysis;
    }

    public void setFaceMatchAnalysis(ZignSecFaceMatchAnalysis faceMatchAnalysis) {
        this.faceMatchAnalysis = faceMatchAnalysis;
    }
}
