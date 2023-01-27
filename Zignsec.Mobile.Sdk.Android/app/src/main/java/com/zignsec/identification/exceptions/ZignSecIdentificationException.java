package com.zignsec.identification.exceptions;

public class ZignSecIdentificationException extends Exception{
    Exception innerException;

    public ZignSecIdentificationException(Exception e){
        this.innerException = e;
    }
}
