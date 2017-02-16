package com.ljmu.andre.fitbitconnectionmonitor.StreamWrappers;

import android.nfc.Tag;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;

import timber.log.Timber;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class OutputStreamWrapper extends OutputStream {
    private InetAddress localAddress;
    private InetAddress targetAddress;

    static int total = 0;

    String TAG = "OutputStreamWrapper";
    OutputStream wrappedStream;

    public OutputStreamWrapper(OutputStream wrappedStream, InetAddress localAddress, InetAddress targetAddress) {
        this.wrappedStream = wrappedStream;
        this.localAddress = localAddress;
        this.targetAddress = targetAddress;
    }

    OutputStreamWrapper(OutputStream wrappedStream, InetAddress localAddress, InetAddress targetAddress, String TAG) {
        this(wrappedStream, localAddress, targetAddress);
        this.TAG = TAG;
    }


    @Override public void write(int b) throws IOException {
        logCall();
        incrementTotal(b);
        wrappedStream.write(b);
    }

    public void write(byte b[]) throws IOException {
        logCall();
        incrementTotal(b.length);
        wrappedStream.write(b);
    }

    public void write(byte b[], int off, int len) throws IOException {
        logCall();
        incrementTotal(len);
        wrappedStream.write(b, off, len);
    }

    public void flush() throws IOException {
        logCall();
        wrappedStream.flush();
    }

    public void close() throws IOException {
        logCall();
        wrappedStream.close();
    }

    public void logCall() {
        Timber.d("[Call:%s] [From:%s][To:%s]", TAG, localAddress.getHostAddress(), targetAddress.getHostAddress());
    }

    public static int incrementTotal(int increase) {
        total += increase;

        Timber.d("Inc: " + increase + " Total: " + (total + InputStreamWrapper.total) + " Output: " + total + " Input: " + InputStreamWrapper.total);

        return increase;
    }
}
