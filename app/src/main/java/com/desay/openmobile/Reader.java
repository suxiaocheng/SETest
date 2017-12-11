package com.desay.openmobile;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by uidq0655 on 2017/12/7.
 */

public class Reader {
    private final String mName;
    private Object mService;
    private Tmc200 mReader;
    private final Object mLock = new Object();
    private ArrayList<Session> lSession = new ArrayList<>();

    public Reader(Object service, Tmc200 reader, String name) {
        this.mName = name;
        this.mService = service;
        this.mReader = reader;
        lSession.clear();
    }

    public String getName() {
        return this.mName;
    }

    public Session openSession() throws IOException {
        if(this.mService != null) {
            Object var1 = this.mLock;
            synchronized(this.mLock) {
                Session var10000;

                if (openReader() == true) {
                    var10000 = new Session(mReader, this);
                    lSession.add(var10000);
                    return var10000;
                } else {
                    throw new IOException("openSession fail");
                }
            }
        } else {
            throw new IllegalStateException("service is not connected");
        }
    }

    public boolean isSecureElementPresent() {
        if(this.mService != null) {
            return true;
        } else {
            return false;
        }
    }

    public Object getSEService() {
        return this.mService;
    }

    public void closeSessions() {
        if(this.mService != null) {
            Object var1 = this.mLock;
            synchronized(this.mLock) {
                for(Session s: lSession){
                    s.closeChannels();
                }
                lSession.clear();
            }
        } else {
            throw new IllegalStateException("service is not connected");
        }
    }

    public boolean openReader() {
        boolean status = mReader.open();
        if (status == false) {
            return false;
        }
        byte[] response = mReader.reset();
        if (response == null) {
            return false;
        }
        lSession.clear();
        return true;
    }
}
