package com.ljmu.andre.fitbitconnectionmonitor.StreamWrappers;

import android.nfc.Tag;
import android.os.Parcel;
import android.support.annotation.NonNull;

import com.ljmu.andre.fitbitconnectionmonitor.Packets.Packet;
import com.ljmu.andre.fitbitconnectionmonitor.Packets.Packet.PacketType;
import com.ljmu.andre.fitbitconnectionmonitor.Utils.FileLogger;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;

import timber.log.Timber;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class OutputStreamWrapper extends OutputStream {
    private String localAddress;
    private String targetAddress;

    String TAG = "OutputStreamWrapper";
    OutputStream wrappedStream;

    public OutputStreamWrapper(OutputStream wrappedStream, InetAddress localAddress, InetAddress targetAddress) {
        this.wrappedStream = wrappedStream;
        this.localAddress = localAddress.toString();
        this.targetAddress = targetAddress.toString();
    }

    OutputStreamWrapper(OutputStream wrappedStream, InetAddress localAddress, InetAddress targetAddress, String TAG) {
        this(wrappedStream, localAddress, targetAddress);
        this.TAG = TAG;
    }


    @Override public void write(int b) throws IOException {
        Packet packet = new Packet(PacketType.NETWORK_OUT)
                .setSize(32)
                .setSourceAddress(localAddress)
                .setTargetAddress(targetAddress)
                .setTimestamp();
        FileLogger.addPacket(packet);

        wrappedStream.write(b);
    }

    public void write(@NonNull byte b[]) throws IOException {
        Packet packet = new Packet(PacketType.NETWORK_OUT)
                .setSize(b.length)
                .setSourceAddress(localAddress)
                .setTargetAddress(targetAddress)
                .setTimestamp();
        FileLogger.addPacket(packet);

        wrappedStream.write(b);
    }

    public void write(byte b[], int off, int len) throws IOException {
        Packet packet = new Packet(PacketType.NETWORK_OUT)
                .setSize(len)
                .setSourceAddress(localAddress)
                .setTargetAddress(targetAddress)
                .setTimestamp();
        FileLogger.addPacket(packet);

        wrappedStream.write(b, off, len);
    }

    public void flush() throws IOException {
        wrappedStream.flush();
    }

    public void close() throws IOException {
        wrappedStream.close();
    }
}
