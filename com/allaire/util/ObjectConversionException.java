package com.allaire.util;

public class ObjectConversionException extends Exception{
    private Throwable _rootCause;
    public ObjectConversionException(String message){
        super(message);
    }

    public ObjectConversionException(String message, Throwable th){
        super(message);
        _rootCause = th;
    }

    public Throwable getRootCause(){
        return _rootCause;
    }
}
