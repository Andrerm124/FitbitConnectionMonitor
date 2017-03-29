package com.ljmu.andre.fitbitconnectionmonitor.Packets;

import android.support.annotation.Nullable;

import com.ljmu.andre.fitbitconnectionmonitor.Utils.FileUtils;
import com.ljmu.andre.fitbitconnectionmonitor.Utils.PacketProcessor.Writable;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class Packet implements Writable {
    private final PacketType type;
    private final OutputType outputType;

    @Nullable private String sourceAddress;
    @Nullable private String targetAddress;

    private long size = 0;
    private long timestamp;

    public Packet(PacketType type, OutputType outputType) {
        this.type = type;
        this.outputType = outputType;
    }

    public Packet(PacketType type) {
        this.type = type;
        this.outputType = OutputType.BREAKDOWN;
    }

    public Packet incrementSize(long incrementation) {
        size += incrementation;
        return this;
    }

    public Packet incrementTimestamp(long incrementation) {
        timestamp += incrementation;
        return this;
    }

    public boolean compareFamily(Packet packet) {
        return getType() == packet.getType() &&
                (getSourceAddress() != null ?
                        getSourceAddress().equals(packet.getSourceAddress()) :
                        packet.getSourceAddress() == null &&
                                (getTargetAddress() != null ?
                                        getTargetAddress().equals(packet.getTargetAddress()) :
                                        packet.getTargetAddress() == null));

    }

    @Nullable public String getSourceAddress() {
        return sourceAddress;
    }

    public PacketType getType() {
        return type;
    }

    @Nullable public String getTargetAddress() {
        return targetAddress;
    }

    public Packet setTargetAddress(@Nullable String targetAddress) {
        this.targetAddress = targetAddress;
        return this;
    }

    public Packet setSourceAddress(@Nullable String sourceAddress) {
        this.sourceAddress = sourceAddress;
        return this;
    }

    @Override public int hashCode() {
        int result = getType() != null ? getType().hashCode() : 0;
        result = 31 * result + (getSourceAddress() != null ? getSourceAddress().hashCode() : 0);
        result = 31 * result + (getTargetAddress() != null ? getTargetAddress().hashCode() : 0);
        result = 31 * result + (int) (getSize() ^ (getSize() >>> 32));
        result = 31 * result + (int) (getTimestamp() ^ (getTimestamp() >>> 32));
        return result;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Packet)) return false;

        Packet packet = (Packet) o;

        if (getSize() != packet.getSize()) return false;
        if (getTimestamp() != packet.getTimestamp()) return false;
        if (getType() != packet.getType()) return false;
        if (getSourceAddress() != null ? !getSourceAddress().equals(packet.getSourceAddress()) : packet.getSourceAddress() != null)
            return false;
        return getTargetAddress() != null ? getTargetAddress().equals(packet.getTargetAddress()) : packet.getTargetAddress() == null;

    }

    public long getSize() {
        return size;
    }

    public Packet setSize(long size) {
        this.size = size;
        return this;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Packet setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public Packet setTimestamp() {
        timestamp = System.currentTimeMillis();
        return this;
    }

    public OutputType getOutputType() {
        return outputType;
    }

    @Override public String toString() {
        return "Packet{" +
                "type=" + type +
                ", outputType=" + outputType +
                ", sourceAddress=" + sourceAddress +
                ", targetAddress=" + targetAddress +
                ", size=" + size +
                ", timestamp=" + timestamp +
                '}';
    }

    @Override public String getWritable() {
        return FileUtils.createCSV(outputType.getName(), sourceAddress, targetAddress, size, timestamp);
    }

    public enum PacketType {
        BLUETOOTH_IN, BLUETOOTH_OUT, NETWORK_IN, NETWORK_OUT
    }

    public enum OutputType {
        SUMMARY("summary"),
        BREAKDOWN("breakdown");

        private String name;
        OutputType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
