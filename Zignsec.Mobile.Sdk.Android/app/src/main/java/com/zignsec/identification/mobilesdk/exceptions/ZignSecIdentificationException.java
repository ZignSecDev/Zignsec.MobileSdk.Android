package com.zignsec.identification.mobilesdk.exceptions;

public class ZignSecIdentificationException extends Exception{
    Exception innerException;

    public ZignSecIdentificationException(Exception e){
        this.innerException = e;
    }
}
