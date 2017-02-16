package com.ljmu.andre.fitbitconnectionmonitor.Modules;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;

import com.ljmu.andre.fitbitconnectionmonitor.Utils.XposedUtils;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import timber.log.Timber;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.getObjectField;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class BluetoothMonitor extends Module {
    String[] strLst = new String[]{ "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z" };
    @Override public void init(ClassLoader fClassLoader, Context fContext) {
        /*XposedUtils.hookAllMethods(BluetoothAdapter.class.getName(), fClassLoader, true, false);
        XposedUtils.hookAllMethods(BluetoothDevice.class.getName(), fClassLoader, true, false);
        XposedUtils.hookAllMethods(BluetoothGatt.class.getName(), fClassLoader, true, false);
        XposedUtils.hookAllMethods(BluetoothGattServer.class.getName(), fClassLoader, true, false);
        XposedUtils.hookAllMethods(BluetoothGattService.class.getName(), fClassLoader, true, false);*/

        /*XposedUtils.hookAllMethods("com.fitbit.bluetooth.galileo.a", fClassLoader, true, false);
        XposedUtils.hookAllMethods("com.fitbit.bluetooth.AirlinkSession", fClassLoader, true, false);
        XposedUtils.hookAllMethods("com.fitbit.bluetooth.BluetoothService", fClassLoader, true, false);
        XposedUtils.hookAllMethods("com.fitbit.bluetooth.BluetoothService$a", fClassLoader, true, false);
        XposedUtils.hookAllMethods("com.fitbit.bluetooth.BluetoothService$b", fClassLoader, true, false);*/

        /*XposedUtils.hookAllMethods(BluetoothSocket.class.getName(), fClassLoader, true, false);
        XposedUtils.hookAllMethods("bluetooth.le.a.b", fClassLoader, true, false);
        XposedUtils.hookAllMethods("android.bluetooth.BluetoothOutputStream", fClassLoader, true, false);
        XposedUtils.hookAllMethods("android.bluetooth.BluetoothInputStream", fClassLoader, true, false);
        XposedUtils.hookAllMethods("com.fitbit.heartrate.HeartRate", fClassLoader, true, false);
        XposedUtils.hookAllMethods("com.fitbit.bluetooth.d", fClassLoader, true, false);
        XposedUtils.hookAllMethods("com.fitbit.bluetooth.LiveDataTask", fClassLoader, true, false);*/

        XposedUtils.hookAllMethods("bluetooth.le.a.c", fClassLoader, true, false);

        /*findAndHookMethod(
                Handler.class,
                "sendMessage", Message.class,
                new XC_MethodHook() {
                    @Override protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        Handler.Callback callback = (Callback) getObjectField(param.thisObject, "mCallback");

                        if(callback != null)
                            Timber.d("Callback: " + callback.toString());

                        XposedUtils.logStackTrace();
                    }
                });*/



        findAndHookMethod(
                "bluetooth.le.a.b",
                fClassLoader,
                "handleMessage", Message.class,
                new XC_MethodHook() {
                    @Override protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        XposedUtils.logStackTrace();
                    }
                });

        findAndHookMethod(
                BluetoothDevice.class,
                "getService",
                new XC_MethodHook() {
                    @Override protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Timber.d("BluetoothService: " + param.getResult().getClass());
                    }
                }
        );

        findAndHookMethod(
                "bluetooth.le.a.b",
                fClassLoader,
                "handleMessage", Message.class,
                new XC_MethodHook() {
                    @Override protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        try {
                            Message message = (Message) param.args[0];
                            Timber.d("Bluetooth message: " + message.what + " Obj: " + message.obj.toString());
                        } catch (Throwable t) {
                            Timber.e(t);
                        }
                    }
                });

        findAndHookMethod(
                "com.fitbit.bluetooth.BluetoothService",
                fClassLoader,
                "onStartCommand", Intent.class, int.class, int.class,
                new XC_MethodHook() {
                    @Override protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        Timber.d("StartCommand: " + param.args[0].toString() + " 2: " + param.args[1] + " 3: " + param.args[2]);
                    }
                });
    }
}
