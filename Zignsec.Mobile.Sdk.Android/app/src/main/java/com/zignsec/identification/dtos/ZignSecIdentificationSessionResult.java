package com.zignsec.identification.dtos;

public class ZignSecIdentificationSessionResult {
    private String id;
    private ZignSecIdentificationResult result;
    private ZignSecIdentificationSessionResultStatus status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ZignSecIdentificationResult getResult() {
        return result;
    }

    public void setResult(ZignSecIdentificationResult result) {
        this.result = result;
    }

    public ZignSecIdentificationSessionResultStatus getStatus() {
        return status;
    }

    public void setStatus(ZignSecIdentificationSessionResultStatus status) {
        this.status = status;
    }
}
