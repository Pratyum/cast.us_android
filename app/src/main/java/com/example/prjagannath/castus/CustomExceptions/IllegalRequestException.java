package com.example.prjagannath.castus.CustomExceptions;

/**
 * Created by prjagannath on 9/2/2016.
 */
public class IllegalRequestException extends RuntimeException {
    public IllegalRequestException(String api){
        super("Is this request require token? url: "+api);
    }
}
