package com.desay.openmobile;

import android.util.Log;

import java.io.IOException;
import java.util.Arrays;

/**
 * Created by uidq0655 on 2017/12/7.
 */

public class Channel {
    private static final String TAG = "Channel";
    private Session mSession;
    private final Tmc200 mChannel;
    private final Object mLock = new Object();

    public int channel_num;
    private boolean isClose;

    private byte[] bDisChannelAPDU = {0x01, 0x70, (byte) 0x80, 0x01, 0x00};
    public byte[] bSelectResponse;

    Channel(Session session, Tmc200 channel) {
        this.mSession = session;
        this.mChannel = channel;
        channel_num = session.iCurrentChannel;
        isClose = false;
        bSelectResponse = null;
    }

    public boolean closeChannel() throws IOException {
        byte[] response;
        bDisChannelAPDU[0] = (byte)channel_num;
        response = transmit(bDisChannelAPDU);

        /* check if response is valid */
        if (response.length != 0x02) {
            Log.e(TAG, Arrays.toString(response));
            throw new IllegalStateException("close channel command response length error");
        }
        if ((response[0] != (byte) 0x90) && (response[1] != (byte) 0x00)) {
            Log.e(TAG, Arrays.toString(response));
            throw new IllegalStateException("close channel command response error->" + channel_num);
        }
        isClose = true;
        return true;
    }

    public boolean close() throws IOException {
        if (this.mSession.getReader().getSEService() != null) {
            if (this.mChannel == null) {
                throw new IllegalStateException("channel must not be null");
            } else {
                if (!this.isClosed()) {
                    Object var1 = this.mLock;
                    closeChannel();
                    isClose = true;
                    return true;
                }
                return false;
            }
        } else {
            throw new IllegalStateException("service not connected to system");
        }
    }

    public boolean isClosed() {
        if (this.mSession.getReader().getSEService() != null) {
            if (this.mChannel == null) {
                throw new IllegalStateException("channel must not be null");
            } else {
                return isClose;
            }
        } else {
            throw new IllegalStateException("service not connected to system");
        }
    }

    public boolean isBasicChannel() {
        if (this.mSession.getReader().getSEService() != null) {
            if (this.mChannel == null) {
                throw new IllegalStateException("channel must not be null");
            } else {
                return (channel_num == 0);
            }
        } else {
            throw new IllegalStateException("service not connected to system");
        }
    }

    public byte[] transmit(byte[] command) throws IOException, IllegalStateException, IllegalArgumentException, SecurityException, NullPointerException {
        if (this.mSession.getReader().getSEService() != null) {
            if (this.mChannel == null) {
                throw new IllegalStateException("channel must not be null");
            } else {
                Object var2 = this.mLock;
                synchronized (this.mLock) {
                    byte[] response = this.mChannel.transmit(command);
                    if(response == null){
                        throw new IOException("channel transmit fail");
                    }
                    return response;
                }
            }
        } else {
            throw new IllegalStateException("service not connected to system");
        }
    }

    public Session getSession() {
        return this.mSession;
    }

    public byte[] getSelectResponse() {
        if (this.mSession.getReader().getSEService() != null) {
            if (this.mChannel == null) {
                throw new IllegalStateException("channel must not be null");
            } else {
                if (isClose == false) {
                    return bSelectResponse;
                }
                throw new IllegalStateException("channel is close");
            }
        } else {
            throw new IllegalStateException("service not connected to system");
        }
    }

    public boolean selectNext() throws IOException, IllegalStateException, UnsupportedOperationException {
        if (this.mSession.getReader().getSEService() != null) {
            if (this.mChannel == null) {
                throw new IllegalStateException("channel must not be null");
            } else {
                Log.e(TAG, "not support function[selectNext]");
                return false;
            }
        } else {
            throw new IllegalStateException("service not connected to system");
        }
    }
}
