package com.ljmu.andre.fitbitconnectionmonitor.StreamWrappers;

import android.support.annotation.NonNull;

import com.ljmu.andre.fitbitconnectionmonitor.Packets.Packet;
import com.ljmu.andre.fitbitconnectionmonitor.Packets.Packet.PacketType;
import com.ljmu.andre.fitbitconnectionmonitor.Utils.FileLogger;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;

import timber.log.Timber;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class InputStreamWrapper extends InputStream {
    private String localAddress;
    private String targetAddress;

    String TAG = "InputStreamWrapper";
    InputStream wrappedStream;

    public InputStreamWrapper(InputStream wrappedStream, InetAddress localAddress, InetAddress targetAddress) {
        this.wrappedStream = wrappedStream;
        this.localAddress = localAddress.toString();
        this.targetAddress = targetAddress.toString();
    }

    InputStreamWrapper(InputStream wrappedStream, InetAddress localAddress, InetAddress targetAddress, String TAG) {
        this(wrappedStream, localAddress, targetAddress);
        this.TAG = TAG;
    }

    @Override public int read() throws IOException {
        int size = wrappedStream.read();

        if(size != -1) {
            Packet packet = new Packet(PacketType.NETWORK_IN)
                    .setSize(size)
                    .setSourceAddress(localAddress)
                    .setTargetAddress(targetAddress)
                    .setTimestamp();

            FileLogger.addPacket(packet);
        }

        return size;
    }

    public int read(@NonNull byte b[]) throws IOException {
        int size = wrappedStream.read(b);

        if(size != -1) {
            Packet packet = new Packet(PacketType.NETWORK_IN)
                    .setSize(size)
                    .setSourceAddress(localAddress)
                    .setTargetAddress(targetAddress)
                    .setTimestamp();

            FileLogger.addPacket(packet);
        }

        return size;
    }

    public int read(@NonNull byte b[], int off, int len) throws IOException {
        int size = wrappedStream.read(b, off, len);

        if(size != -1) {
            Packet packet = new Packet(PacketType.NETWORK_IN)
                    .setSize(size)
                    .setSourceAddress(localAddress)
                    .setTargetAddress(targetAddress)
                    .setTimestamp();

            FileLogger.addPacket(packet);
        }

        return size;
    }

    public long skip(long n) throws IOException {
        return wrappedStream.skip(n);
    }

    public int available() throws IOException {
        return wrappedStream.available();
    }

    public void close() throws IOException {
        wrappedStream.close();
    }

    public synchronized void mark(int readlimit) {
        wrappedStream.mark(readlimit);
    }

    public synchronized void reset() throws IOException {
        wrappedStream.reset();
    }

    public boolean markSupported() {
        return wrappedStream.markSupported();
    }
}
