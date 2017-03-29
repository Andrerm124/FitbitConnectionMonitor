package com.ljmu.andre.fitbitconnectionmonitor.Utils;

import com.ljmu.andre.fitbitconnectionmonitor.FCMApplication;
import com.ljmu.andre.fitbitconnectionmonitor.Packets.Packet;
import com.ljmu.andre.fitbitconnectionmonitor.Packets.Packet.OutputType;
import com.ljmu.andre.fitbitconnectionmonitor.Packets.Packet.PacketType;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class PacketProcessor implements Runnable {
    private final long WRITE_DELAY;
    private final Object LOCK = new Object();
    private List<List<Packet>> packetFamilies = new ArrayList<>();
    private boolean isAlive = false;
    private long lastWriteTime;
    private PacketType packetType;
    private String procName;
    private File outputFile;

    PacketProcessor(PacketType packetType, long WRITE_DELAY) {
        this.packetType = packetType;
        this.procName = packetType.name();
        this.WRITE_DELAY = WRITE_DELAY;
        isAlive = true;
    }

    void addPacket(Packet packet) {
        if (packet.getType() != packetType) {
            throw new IllegalArgumentException(
                    String.format("Tried to add %s packet to a %s processor",
                            packet.getType().name(),
                            packetType.name()));
        }

        synchronized (LOCK) {
            Timber.d(getTag() + "Aquired Lock");

            boolean hasAdded = false;
            for (List<Packet> familyPackets : packetFamilies) {
                if (familyPackets.isEmpty())
                    continue;

                Packet firstPacket = familyPackets.get(0);

                if (packet.compareFamily(firstPacket)) {
                    familyPackets.add(packet);
                    Timber.d(getTag() + "Added to family: " + packet.toString());
                    hasAdded = true;
                    break;
                }
            }

            if (!hasAdded) {
                Timber.d(getTag() + "Family non existent... Creating new");
                Timber.d(getTag() + "Added: " + packet.toString());
                List<Packet> newFamily = new ArrayList<>();
                newFamily.add(packet);
                packetFamilies.add(newFamily);
            }

            LOCK.notifyAll();
            Timber.d(getTag() + "Thread notified");
        }

        Timber.d(getTag() + "Lock Released");
    }

    public void start() {
        isAlive = true;
    }

    @Override public void run() {
        Timber.d(getTag() + "Running on " + Thread.currentThread().getName());

        while (isAlive) {
            try {
                synchronized (LOCK) {
                    while (packetFamilies.isEmpty()) {
                        Timber.d(getTag() + "Waiting for entries");
                        LOCK.wait();
                        Timber.d(getTag() + "Resuming after wait");
                    }
                }

                long writeTimeDifference = System.currentTimeMillis() - lastWriteTime;

                if (writeTimeDifference < WRITE_DELAY) {
                    long adjustedWriteDelay = WRITE_DELAY - writeTimeDifference;
                    Timber.d(getTag() + "Sleeping for %sms", adjustedWriteDelay);
                    Thread.sleep(adjustedWriteDelay);
                    Timber.d(getTag() + "Awoken from sleep");
                }

                writeCollectedPackets();

                Timber.d(getTag() + "Completed Write Loop");
            } catch (InterruptedException | IOException e) {
                Timber.e(e);
            }
        }
    }

    public void writeCollectedPackets() throws IOException {
        BufferedWriter writer = getWriter();

        if (writer == null) {
            stop();
            return;
        }

        synchronized (LOCK) {
            // Disgustingly inefficient timestamp sorting \\
            List<Packet> sortedMasters = new ArrayList<>();
            Packet summaryPacket = new Packet(packetType, OutputType.SUMMARY);

            for (List<Packet> packetFamily : packetFamilies) {
                if (packetFamily.isEmpty())
                    continue;

                Packet familyReference = packetFamily.get(0);
                Packet breakdownPacket = new Packet(familyReference.getType(), OutputType.BREAKDOWN)
                        .setSourceAddress(familyReference.getSourceAddress())
                        .setTargetAddress(familyReference.getTargetAddress())
                        .setTimestamp(0);

                for (Packet packet : packetFamily) {
                    breakdownPacket.incrementTimestamp(packet.getTimestamp() / packetFamily.size())
                            .incrementSize(packet.getSize());
                    summaryPacket.incrementSize(packet.getSize());
                }

                if(sortedMasters.isEmpty())
                    sortedMasters.add(breakdownPacket);
                else {
                    boolean hasAdded = false;

                    for(int i = 0; i < sortedMasters.size(); i++) {
                        if(breakdownPacket.getTimestamp() < sortedMasters.get(i).getTimestamp()) {
                            sortedMasters.add(0, breakdownPacket);
                            hasAdded = true;
                            break;
                        }
                    }

                    if(!hasAdded)
                        sortedMasters.add(breakdownPacket);
                }
            }

            for(Packet compiledPacket : sortedMasters)
                writePacket(writer, compiledPacket);

            summaryPacket.setTimestamp();
            writePacket(writer, summaryPacket);
        }

        writer.flush();
        writer.close();

        packetFamilies.clear();
        lastWriteTime = System.currentTimeMillis();
    }

    private BufferedWriter getWriter() {
        try {
            BufferedWriter writer;

            if (outputFile == null || !outputFile.exists()) {

                File directory = new File(FileUtils.getExternalPath() + "/" + FCMApplication.MODULE_TAG);

                if (!directory.exists() && !directory.mkdirs()) {
                    Timber.e(getTag() + "Couldn't create directory");
                    return null;
                }

                outputFile = new File(directory, packetType.name().toLowerCase() + "_logs.log");
                boolean existed = outputFile.exists();

                Timber.d(getTag() + "File: " + outputFile.getAbsolutePath());

                if (!outputFile.exists() && !outputFile.createNewFile()) {
                    Timber.e(getTag() + "Error creating new file");
                    return null;
                }

                writer = new BufferedWriter(new FileWriter(outputFile, true));

                if(!existed) {
                    writer.append("# File outputs are as follows! #");
                    writer.newLine();
                    writer.append("# OutputType, SourceAddress, TargetAddress, PacketSize, Timestamp, Write_Frequency#");
                    writer.newLine();
                }
            } else

                writer = new BufferedWriter(new FileWriter(outputFile, true));

            return writer;
        } catch (Throwable t) {
            Timber.e(t);
        }

        return null;
    }

    public void stop() {
        Timber.w(getTag() + "Killing");
        isAlive = false;
        synchronized (LOCK) {
            LOCK.notifyAll();
        }
    }

    private void writePacket(BufferedWriter writer, Packet packet) throws IOException {
        Timber.d(getTag() + "Writing: " + packet.toString());
        writer.append(packet.getWritable()).append(",").append(Long.toString(WRITE_DELAY));
        writer.newLine();
    }

    public boolean isAlive() {
        return isAlive;
    }


    private String getTag() {
        return "[" + procName + "] ";
    }
    public interface Writable {
        String getWritable();
    }
}
