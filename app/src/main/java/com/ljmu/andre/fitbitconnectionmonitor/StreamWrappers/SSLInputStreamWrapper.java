package com.ljmu.andre.fitbitconnectionmonitor.StreamWrappers;

import java.io.InputStream;
import java.net.InetAddress;

import timber.log.Timber;

import static de.robv.android.xposed.XposedHelpers.callMethod;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class SSLInputStreamWrapper extends InputStreamWrapper {

    public SSLInputStreamWrapper(InputStream wrappedStream, InetAddress localAddress, InetAddress targetAddress) {
        super(wrappedStream, localAddress, targetAddress, "SSLInputStreamWrapper");
    }

    public void awaitPendingOps() {
        callMethod(wrappedStream, "awaitPendingOps");
    }
}
