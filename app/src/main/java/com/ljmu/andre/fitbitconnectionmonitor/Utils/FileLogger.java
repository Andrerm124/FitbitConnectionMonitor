package com.ljmu.andre.fitbitconnectionmonitor.Utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ljmu.andre.fitbitconnectionmonitor.Packets.Packet;
import com.ljmu.andre.fitbitconnectionmonitor.Packets.Packet.PacketType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class FileLogger {
    private static final long WRITE_FREQ = TimeUnit.SECONDS.toMillis(15);
    private static final Map<String, ProcContainer> containers = new ConcurrentHashMap<>();
    private static int createdThreadCount = 0;

    public static void addPacket(Packet packet) {
        if(packet.getSize() < 0) {
            Timber.w("Tried to save packet of negative size: " + packet.toString());
            return;
        }

        PacketProcessor processor = getOrBuildProcessor(packet.getType());
        processor.addPacket(packet);
    }

    @Nullable private static ProcContainer getProcessorContainer(@NonNull PacketType packetType) {
        return containers.get(packetType.name());
    }

    @NonNull private static PacketProcessor getOrBuildProcessor(@NonNull PacketType packetType) {
        ProcContainer procContainer = getProcessorContainer(packetType);

        if(procContainer == null || !procContainer.thread.isAlive()) {
            procContainer = ProcContainer.build(packetType);
            containers.put(packetType.name(), procContainer);
            Timber.w("Created new processor: " + packetType.name());
        }

        return procContainer.processor;
    }

    public static void stopLogging() {
        for(ProcContainer container : containers.values())
            container.processor.stop();
    }

    private static class ProcContainer {
        private static int containerCount = 0;
        Thread thread;
        PacketProcessor processor;

        private ProcContainer(Thread thread, PacketProcessor processor) {
            this.thread = thread;
            this.processor = processor;
        }

        static ProcContainer build(PacketType packetType) {
            PacketProcessor processor = new PacketProcessor(packetType, WRITE_FREQ);

            Thread thread = new Thread(processor);
            thread.start();
            thread.setName(packetType.name() + (containerCount++));

            return new ProcContainer(thread, processor);
        }
    }
}
