package com.zignsec.identification.callbacks;

import androidx.annotation.Nullable;

import com.zignsec.identification.dtos.ZignSecIdentificationSessionResult;
import com.zignsec.identification.exceptions.ZignSecIdentificationException;

public interface ZignSecIdentificationCompletion {
    void onCompleted(@Nullable ZignSecIdentificationSessionResult results, @Nullable ZignSecIdentificationException exception);
}
