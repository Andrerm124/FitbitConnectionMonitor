package com.ljmu.andre.fitbitconnectionmonitor.Modules;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.net.LocalSocket;

import com.ljmu.andre.fitbitconnectionmonitor.DataModels.Packet;
import com.ljmu.andre.fitbitconnectionmonitor.StreamWrappers.InputStreamWrapper;
import com.ljmu.andre.fitbitconnectionmonitor.StreamWrappers.OutputStreamWrapper;
import com.ljmu.andre.fitbitconnectionmonitor.StreamWrappers.SSLInputStreamWrapper;
import com.ljmu.andre.fitbitconnectionmonitor.StreamWrappers.SSLOutputStreamWrapper;

import junit.framework.Assert;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import de.robv.android.xposed.XC_MethodHook;
import timber.log.Timber;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClassIfExists;
import static de.robv.android.xposed.XposedHelpers.newInstance;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class NetworkMonitor extends Module {
    private static HashMap<Class, Class> wrapperMap = new HashMap<>();
    private static boolean wrapperInitialized = false;

    static {
        wrapperMap.put(SSLOutputStreamWrapper.class, Object.class);
        wrapperMap.put(SSLInputStreamWrapper.class, Object.class);
        wrapperMap.put(OutputStreamWrapper.class, Object.class);
        wrapperMap.put(InputStreamWrapper.class, Object.class);
    }

    private ArrayList<Packet> packetList = new ArrayList<>();

    @Override public void init(final ClassLoader fClassLoader, Context fContext) {
        Timber.d("Loading NetworkMonitor");

        if (!wrapperInitialized)
            initWrapperClasses(fClassLoader);

        XC_MethodHook outputWrappingHook = new XC_MethodHook() {
            @Override protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                try {
                    OutputStream unwrappedStream = (OutputStream) param.getResult();
                    Assert.assertNotNull(unwrappedStream);

                    Class wrappedStreamClass = wrapperMap.get(unwrappedStream.getClass());

                    if (wrappedStreamClass == null) {
                        Timber.e("Wrapper not found for stream: " + unwrappedStream.getClass());
                        return;
                    } else if (wrappedStreamClass.equals(Object.class)) {
                        Timber.w("Stream already wrapped: " + wrappedStreamClass);
                        return;
                    }

                    InetAddress sourceAddress = (InetAddress) callMethod(param.thisObject, "getLocalAddress");
                    InetAddress targetAddress = (InetAddress) callMethod(param.thisObject, "getInetAddress");
                    Object wrappedStream = newInstance(wrappedStreamClass, unwrappedStream, sourceAddress, targetAddress);
                    Assert.assertNotNull(wrappedStream);

                    param.setResult(wrappedStream);
                } catch (Throwable t) {
                    Timber.e(t);
                }
            }
        };

        XC_MethodHook inputWrappingHook = new XC_MethodHook() {
            @Override protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                try {
                    InputStream unwrappedStream = (InputStream) param.getResult();
                    Assert.assertNotNull(unwrappedStream);

                    Class wrappedStreamClass = wrapperMap.get(unwrappedStream.getClass());

                    if (wrappedStreamClass == null) {
                        Timber.e("Wrapper not found for stream: " + unwrappedStream.getClass());
                        return;
                    } else if (wrappedStreamClass.equals(Object.class)) {
                        Timber.w("Stream already wrapped: " + wrappedStreamClass);
                        return;
                    }

                    InetAddress targetAddress = (InetAddress) callMethod(param.thisObject, "getLocalAddress");
                    InetAddress sourceAddress = (InetAddress) callMethod(param.thisObject, "getInetAddress");
                    Object wrappedStream = newInstance(wrappedStreamClass, unwrappedStream, sourceAddress, targetAddress);
                    Assert.assertNotNull(wrappedStream);

                    param.setResult(wrappedStream);
                } catch (Throwable t) {
                    Timber.e(t);
                }
            }
        };

        findAndHookMethod(
                Socket.class,
                "getOutputStream",
                outputWrappingHook
        );
        findAndHookMethod(
                Socket.class,
                "getInputStream",
                inputWrappingHook
        );

        findAndHookMethod("com.android.org.conscrypt.OpenSSLSocketImpl",
                fClassLoader,
                "getOutputStream",
                outputWrappingHook
        );
        findAndHookMethod("com.android.org.conscrypt.OpenSSLSocketImpl",
                fClassLoader,
                "getInputStream",
                inputWrappingHook
        );
    }

    private static void initWrapperClasses(ClassLoader fClassLoader) {
        Class SSLOututStream = findClassIfExists("com.android.org.conscrypt.OpenSSLSocketImpl$SSLOutputStream", fClassLoader);
        if (SSLOututStream != null) wrapperMap.put(SSLOututStream, SSLOutputStreamWrapper.class);

        Class SSLInputStream = findClassIfExists("com.android.org.conscrypt.OpenSSLSocketImpl$SSLInputStream", fClassLoader);
        if (SSLInputStream != null) wrapperMap.put(SSLInputStream, SSLInputStreamWrapper.class);

        Class PlainSocketOutputStream = findClassIfExists("java.net.PlainSocketImpl$PlainSocketOutputStream", fClassLoader);
        if (PlainSocketOutputStream != null)
            wrapperMap.put(PlainSocketOutputStream, OutputStreamWrapper.class);

        Class PlainSocketInputStream = findClassIfExists("java.net.PlainSocketImpl$PlainSocketInputStream", fClassLoader);
        if (PlainSocketInputStream != null)
            wrapperMap.put(PlainSocketInputStream, InputStreamWrapper.class);

        wrapperInitialized = true;
    }
}
