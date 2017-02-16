package com.ljmu.andre.fitbitconnectionmonitor.StreamWrappers;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;

import timber.log.Timber;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class InputStreamWrapper extends InputStream {
    private InetAddress localAddress;
    private InetAddress targetAddress;

    static int total = 0;

    String TAG = "InputStreamWrapper";
    InputStream wrappedStream;

    public InputStreamWrapper(InputStream wrappedStream, InetAddress localAddress, InetAddress targetAddress) {
        this.wrappedStream = wrappedStream;
        this.localAddress = localAddress;
        this.targetAddress = targetAddress;
    }

    InputStreamWrapper(InputStream wrappedStream, InetAddress localAddress, InetAddress targetAddress, String TAG) {
        this(wrappedStream, localAddress, targetAddress);
        this.TAG = TAG;
    }

    @Override public int read() throws IOException {
        logCall();
        return incrementTotal(wrappedStream.read());
    }

    public int read(@NonNull byte b[]) throws IOException {
        logCall();
        return incrementTotal(wrappedStream.read(b));
    }

    public int read(@NonNull byte b[], int off, int len) throws IOException {
        logCall();
        return incrementTotal(wrappedStream.read(b, off, len));
    }

    public long skip(long n) throws IOException {
        logCall();
        return wrappedStream.skip(n);
    }

    public int available() throws IOException {
        logCall();
        return wrappedStream.available();
    }

    public void close() throws IOException {
        logCall();
        wrappedStream.close();
    }

    public synchronized void mark(int readlimit) {
        logCall();
        wrappedStream.mark(readlimit);
    }

    public synchronized void reset() throws IOException {
        logCall();
        wrappedStream.reset();
    }

    public boolean markSupported() {
        logCall();
        return wrappedStream.markSupported();
    }

    public void logCall() {
        Timber.d("[Call:%s] [From:%s][To:%s]", TAG, localAddress.getHostAddress(), targetAddress.getHostAddress());
    }

    public static int incrementTotal(int increase) {
        total += increase;

        Timber.d("Inc: " + increase + " Total: " + (total + OutputStreamWrapper.total) + " Input: " + total + " Output: " + OutputStreamWrapper.total);

        return increase;
    }
}
