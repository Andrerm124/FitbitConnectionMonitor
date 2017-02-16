package com.ljmu.andre.fitbitconnectionmonitor.StreamWrappers;

import java.io.OutputStream;
import java.net.InetAddress;

import timber.log.Timber;

import static de.robv.android.xposed.XposedHelpers.callMethod;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class SSLOutputStreamWrapper extends OutputStreamWrapper {
    public SSLOutputStreamWrapper(OutputStream wrappedStream, InetAddress localAddress, InetAddress targetAddress) {
        super(wrappedStream, localAddress, targetAddress, "SSLOutputStreamWrapper");
    }

    public void awaitPendingOps() {
        logCall();
        callMethod(wrappedStream, "awaitPendingOps");
    }
}