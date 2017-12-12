package com.desay.uidq0655.setest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;

import com.desay.openmobile.Channel;
import com.desay.openmobile.Reader;
import com.desay.openmobile.SEService;
import com.desay.openmobile.Session;
import com.desay.openmobile.Tmc200;

import java.io.IOException;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "MainActivity";
    private SEService seService;
    private Reader[] reader;
    private Session session;
    private Channel channel;
    private Tmc200 tmc200;

    @BindView(R.id.open)
    Button open;

    @BindView(R.id.reset)
    Button reset;

    @BindView(R.id.getatr)
    Button getatr;

    @BindView(R.id.close)
    Button close;

    @BindView(R.id.openReader)
    Button openReader;

    @BindView(R.id.closeReader)
    Button closeReader;

    @BindView(R.id.openSession)
    Button openSession;

    @BindView(R.id.closeSession)
    Button closeSession;

    @BindView(R.id.openChannel)
    Button openChannel;

    @BindView(R.id.closeChannel)
    Button closeChannel;

    @OnClick(R.id.open)
    void open() {
        if (tmc200 == null) {
            tmc200 = new Tmc200();
        }
        boolean status = tmc200.open();
        if (status == false) {
            Log.d(TAG, "open tmc200 fail");
        }
    }

    @OnClick(R.id.close)
    void close() {
        if (tmc200 == null) {
            tmc200 = new Tmc200();
        }
        boolean status = tmc200.close();
        if (status == false) {
            Log.d(TAG, "open tmc200 fail");
        }
    }

    @OnClick(R.id.reset)
    void reset() {
        if (tmc200 == null) {
            tmc200 = new Tmc200();
        }
        byte[] response = tmc200.reset();
        if (response == null) {
            Log.d(TAG, "tmc200 reset fail");
        } else {
            Log.d(TAG, "response" + Arrays.toString(response));
        }
    }

    @OnClick(R.id.getatr)
    void getatr() {
        if (tmc200 == null) {
            tmc200 = new Tmc200();
        }
        byte[] response = tmc200.getATR();
        if (response == null) {
            Log.d(TAG, "tmc200 getart fail");
        } else {
            Log.d(TAG, "response" + Arrays.toString(response));
        }
    }

    @OnClick(R.id.openReader)
    void openReader() {
        if (reader != null) {
            Log.d(TAG, "reader is being open");
        }
        reader = seService.getReaders();
    }

    @OnClick(R.id.closeReader)
    void closeReader() {
        if (reader == null) {
            Log.d(TAG, "reader is being close");
        } else {
            reader[0].closeSessions();
            reader = null;
        }
    }

    @OnClick(R.id.openSession)
    void openSession() {
        if (reader == null) {
            Log.e(TAG, "reader is null");
            return;
        }
        try {
            session = reader[0].openSession();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.getAtrSession)
    void getAtrSession() {
        if (reader == null) {
            Log.e(TAG, "reader is null");
            return;
        }
        byte[] response = session.getATR();
        Log.d(TAG, "Session getatr: " + Arrays.toString(response));
    }

    @OnClick(R.id.closeSession)
    void closeSession() {
        if (reader == null) {
            Log.e(TAG, "reader is null");
            return;
        }
        session.close();
    }

    @OnClick(R.id.openChannel)
    void openChannel() {
        if (session == null) {
            Log.d(TAG, "session is being close");
            return;
        }
        try {
            channel = session.openLogicalChannel(new byte[]{
                    0x01, (byte) 0xa4, 0x04, 0x0, 0x10, (byte) 0xa0, 0x0, 0x0,
                    0x06, 0x28, 0x0, 0x0, 0x1, 0x0, 0x0, 0x0, 0x0, 0x1, (byte) 0xe2,
                    0x0, 0x01});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.openBasicChannel)
    void openBasicChannel() {
        if (session == null) {
            Log.d(TAG, "session is being close");
            return;
        }
        try {
            channel = session.openBasicChannel(new byte[]{
                    0x01, (byte) 0xa4, 0x04, 0x0, 0x10, (byte) 0xa0, 0x0, 0x0,
                    0x06, 0x28, 0x0, 0x0, 0x1, 0x0, 0x0, 0x0, 0x0, 0x1, (byte) 0xe2,
                    0x0, 0x01});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.transmit)
    void transmit(){
        int iTestCount = 0;
        if (reader == null) {
            Log.e(TAG, "reader is null");
            return;
        }
        if (session == null) {
            Log.d(TAG, "session is being close");
            return;
        }
        while (iTestCount < 100000) {
            try {
                byte[] response = channel.transmit(new byte[]{0x00, (byte) 0x84, 0x00, 0x00, 0x08});
                if (response != null) {
                    Log.d(TAG, iTestCount + ": transmit response: " + Arrays.toString(response));
                    if((response[8] != 0x90) && (response[9] != 0x0)) {
                        break;
                    }
                } else {
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
            iTestCount++;
        }
    }

    @OnClick(R.id.closeChannel)
    void closeChannel() {
        if (session == null) {
            Log.d(TAG, "session is being close");
            return;
        }
        try {
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.getSelectResponse)
    void getSelectResponse() {
        if (session == null) {
            Log.d(TAG, "session is being close");
            return;
        }
        byte[] response = channel.getSelectResponse();
        Log.d(TAG, "response: " + Arrays.toString(response));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        seService = new SEService(this, null);
    }
}
