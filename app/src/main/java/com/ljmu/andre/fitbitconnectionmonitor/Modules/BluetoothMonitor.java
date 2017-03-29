package com.ljmu.andre.fitbitconnectionmonitor.Modules;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.ParcelUuid;

import com.ljmu.andre.fitbitconnectionmonitor.HookManager;
import com.ljmu.andre.fitbitconnectionmonitor.Packets.Packet;
import com.ljmu.andre.fitbitconnectionmonitor.Packets.Packet.PacketType;
import com.ljmu.andre.fitbitconnectionmonitor.Utils.FileLogger;
import com.ljmu.andre.fitbitconnectionmonitor.Utils.XposedUtils;

import junit.framework.Assert;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import timber.log.Timber;

import static de.robv.android.xposed.XposedHelpers.findAndHookConstructor;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.getObjectField;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class BluetoothMonitor extends Module {
    @Override public void init(ClassLoader fClassLoader, Context fContext) {
        XposedUtils.hookAllMethods("com.fitbit.galileo.ota.GalileoOtaMessages", fClassLoader, true, false);

        findAndHookMethod(
                "android.bluetooth.BluetoothGattService",
                fClassLoader,
                "getCharacteristic", UUID.class, int.class,
                new XC_MethodHook() {
                    @Override protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        BluetoothGattCharacteristic characteristic = (BluetoothGattCharacteristic) param.getResult();
                        if(characteristic.getValue() == null)
                            return;

                        Packet packet = new Packet(PacketType.BLUETOOTH_IN)
                                .setSize(characteristic.getValue().length)
                                .setSourceAddress("Watch")
                                .setTargetAddress("Smartphone")
                                .setTimestamp();

                        FileLogger.addPacket(packet);
                    }
                }
        );

        findAndHookMethod(
                "android.bluetooth.BluetoothGattService",
                fClassLoader,
                "sendNotification", UUID.class, int.class,
                new XC_MethodHook() {
                    @Override protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        BluetoothGattCharacteristic characteristic = (BluetoothGattCharacteristic) param.getResult();
                        if(characteristic.getValue() == null)
                            return;

                        Packet packet = new Packet(PacketType.BLUETOOTH_OUT)
                                .setSize(characteristic.getValue().length)
                                .setSourceAddress("Smartphone")
                                .setTargetAddress("Watch")
                                .setTimestamp();

                        FileLogger.addPacket(packet);
                    }
                }
        );
    }
}
