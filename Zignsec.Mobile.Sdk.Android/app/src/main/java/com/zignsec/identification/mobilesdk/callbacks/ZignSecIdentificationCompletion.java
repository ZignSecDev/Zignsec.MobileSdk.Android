package com.zignsec.identification.mobilesdk.callbacks;

import androidx.annotation.Nullable;

import com.zignsec.identification.mobilesdk.exceptions.ZignSecIdentificationException;
import com.zignsec.identification.mobilesdk.results.ZignSecIdentificationResults;
import com.zignsec.identification.mobilesdk.results.ZignSecIdentificationTotalResult;

public interface ZignSecIdentificationCompletion {
    void onCompleted(ZignSecIdentificationTotalResult totalResult, @Nullable ZignSecIdentificationResults var2, @Nullable ZignSecIdentificationException var3);
}
