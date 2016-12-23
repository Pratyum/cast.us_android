package com.example.prjagannath.castus;

import java.util.GregorianCalendar;

/**
 * Created by prjagannath on 9/2/2016.
 */
public class Session {
    private boolean isActive = false;
    private long lastActiveTime = 0;
    private long timeoutInMillis = 0;

    /**
     * Initiate a session with desire timeout length
     * @param timeoutInSec define in seconds
     */
    public Session(long timeoutInSec){
        timeoutInMillis = timeoutInSec * 1000;
    }

    public void startSession(){
        lastActiveTime = new GregorianCalendar().getTimeInMillis();
        isActive = true;
    }

    public void closeSession(){
        lastActiveTime = 0;
        isActive = false;
    }

    public boolean isSessionActive(){
        return (isActive && (new GregorianCalendar().getTimeInMillis() - lastActiveTime < timeoutInMillis));
    }
}
