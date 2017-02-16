package com.ljmu.andre.fitbitconnectionmonitor.DataModels;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class Packet {
    int packetSize;

    String tHostName;
    String tHostAddress;
    int tHostPort;

    String sHostName;
    String sHostAddress;
    int sHostPort;

    public int getPacketSize() {
        return packetSize;
    }

    public void setPacketSize(int packetSize) {
        this.packetSize = packetSize;
    }

    public String gettHostName() {
        return tHostName;
    }

    public void settHostName(String tHostName) {
        this.tHostName = tHostName;
    }

    public String gettHostAddress() {
        return tHostAddress;
    }

    public void settHostAddress(String tHostAddress) {
        this.tHostAddress = tHostAddress;
    }

    public int gettHostPort() {
        return tHostPort;
    }

    public void settHostPort(int tHostPort) {
        this.tHostPort = tHostPort;
    }

    public String getsHostName() {
        return sHostName;
    }

    public void setsHostName(String sHostName) {
        this.sHostName = sHostName;
    }

    public String getsHostAddress() {
        return sHostAddress;
    }

    public void setsHostAddress(String sHostAddress) {
        this.sHostAddress = sHostAddress;
    }

    public int getsHostPort() {
        return sHostPort;
    }

    public void setsHostPort(int sHostPort) {
        this.sHostPort = sHostPort;
    }
}
