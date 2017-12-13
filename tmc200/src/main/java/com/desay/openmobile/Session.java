package com.desay.openmobile;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created by uidq0655 on 2017/12/7.
 */

public class Session {
    private final static String TAG = "Session";
    private final Object mLock = new Object();
    private final Reader mReader;
    private final Tmc200 mSession;

    public int iCurrentChannel;

    public ArrayList<Channel> lChannelList = new ArrayList<>();

    private boolean isOpen;

    private static byte[] bSelectChannel = {0x0, 0x70, 0x0, 0x0, 0x1};
    private static byte[] bSelectAid = {0x0, (byte)0xa4, 0x04, 0x0, 0x00};
    private byte[] bResponseBackup;

    Session(Tmc200 session, Reader reader) {
        this.mReader = reader;
        this.mSession = session;
        isOpen = true;
    }

    public Reader getReader() {
        return this.mReader;
    }

    public byte[] getATR() {
        if(this.mReader.getSEService() != null) {
            if(this.mSession == null) {
                throw new IllegalStateException("service session is null");
            } else {
                lChannelList.clear();
                byte[] response = mSession.getATR();
                if (response != null) {
                    isOpen = true;
                    return response;
                }
                Log.e(TAG, "getATR reset fail");
                return null;
            }
        } else {
            throw new IllegalStateException("service not connected to system");
        }
    }

    public void close() {
        if(this.mReader.getSEService() != null) {
            if((this.mSession != null)){
                if(isOpen == false){
                    Log.d(TAG, "Session is already close");
                    return;
                }
                Object var1 = this.mLock;
                synchronized(this.mLock) {
                    closeChannels();
                    this.mSession.close();
                    isOpen = false;
                }
            }
        } else {
            throw new IllegalStateException("service not connected to system");
        }
    }

    public boolean isClosed() {
        return this.mSession == null || !isOpen;
    }

    public void closeChannels() {
        if(this.mReader.getSEService() != null) {
            if(this.mSession != null) {
                if(isOpen == false){
                    return;
                }
                Object var1 = this.mLock;
                synchronized(this.mLock) {
                    for(Channel c:lChannelList){
                        try {
                            c.close();
                        } catch (IOException e) {
                            Log.e(TAG, "channel" + c.channel_num + " close fail");
                            e.printStackTrace();
                        } catch (IllegalStateException e) {
                            Log.e(TAG, "IllegalStateException");
                            e.printStackTrace();
                        }
                    }
                    lChannelList.clear();
                }
            }
        } else {
            throw new IllegalStateException("service not connected to system");
        }
    }

    public Channel openBasicChannel(byte[] aid, byte p2) throws IOException, IllegalStateException, IllegalArgumentException, SecurityException, NoSuchElementException, UnsupportedOperationException {
        if(this.mReader.getSEService() != null) {
            if(this.mSession == null) {
                throw new IllegalStateException("service session is null");
            } else if(this.getReader() == null) {
                throw new IllegalStateException("reader must not be null");
            } else if(isOpen == false) {
                throw new IllegalStateException("session is being close");
            } else {
                Object var3 = this.mLock;
                synchronized(this.mLock) {
                    Channel var10000;
                    if (openChannelExtend(aid, p2, true)){
                        var10000 = new Channel(this, this.mSession);
                        var10000.bSelectResponse = bResponseBackup;
                        lChannelList.add(var10000);
                        return var10000;
                    }
                    return null;
                }
            }
        } else {
            throw new IllegalStateException("service not connected to system");
        }
    }

    public Channel openBasicChannel(byte[] aid) throws IOException, IllegalStateException, IllegalArgumentException, SecurityException, NoSuchElementException, UnsupportedOperationException {
        return this.openBasicChannel(aid, (byte)0);
    }

    public Channel openLogicalChannel(byte[] aid, byte p2) throws IOException, IllegalStateException, IllegalArgumentException, SecurityException, NoSuchElementException, UnsupportedOperationException {
        if(this.mReader.getSEService() != null) {
            if(this.mSession == null) {
                throw new IllegalStateException("service session is null");
            } else if(this.getReader() == null) {
                throw new IllegalStateException("reader must not be null");
            } else if(isOpen == false) {
                throw new IllegalStateException("session is being close");
            } else {
                Object var3 = this.mLock;
                synchronized(this.mLock) {
                    Channel var10000;
                    if (openChannelExtend(aid, p2, false)){
                        var10000 = new Channel(this, this.mSession);
                        var10000.bSelectResponse = bResponseBackup;
                        lChannelList.add(var10000);
                        return var10000;
                    }
                    return null;
                }
            }
        } else {
            throw new IllegalStateException("service not connected to system");
        }
    }

    public Channel openLogicalChannel(byte[] aid) throws IOException, IllegalStateException, IllegalArgumentException, SecurityException, NoSuchElementException, UnsupportedOperationException {
        return this.openLogicalChannel(aid, (byte)0);
    }

    private boolean openChannelExtend(byte[] var1, byte var2, boolean basic) throws IOException {
        byte[] response;

        if(basic == false) {
            response = mSession.transmit(bSelectChannel);
            if(response == null){
                throw new IOException("transmit select channel command fail");
            }
            /* check if response is valid */
            if (response.length != 0x03) {
                Log.e(TAG, Arrays.toString(response));
                throw new IllegalStateException("select channel command response length error, phase0");
            }
            if ((response[1] != (byte) 0x90) && (response[2] != (byte) 0x00)) {
                Log.e(TAG, Arrays.toString(response));
                throw new IllegalStateException("select channel command response error, phase0");
            }
            iCurrentChannel = response[0];
        } else {
            iCurrentChannel = 0;
        }

        byte[] cmd = new byte[bSelectAid.length+var1.length];
        for(int i=0; i<bSelectAid.length; i++){
            cmd[i] = bSelectAid[i];
        }
        for(int i=0; i<var1.length; i++){
            cmd[i+bSelectAid.length] = var1[i];
        }
        cmd[0] |= iCurrentChannel;
        cmd[3] = var2;
        if(var1.length >= 255){
            throw new IOException("aid length exceed 255->" + var1.length);
        }
        cmd[4] = (byte)var1.length;

        response = mSession.transmit(cmd);
        bResponseBackup = response;
        if ((response[0] != (byte)0x90) && (response[0] != (byte)0x00)) {
            Log.e(TAG, "select channel aid response error, phase1");
            Log.e(TAG, Arrays.toString(response));
        }
        return true;
    }
}
