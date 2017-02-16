package com.ljmu.andre.fitbitconnectionmonitor;

import android.content.Context;
import android.util.Log;

import com.ljmu.andre.fitbitconnectionmonitor.Modules.BluetoothMonitor;
import com.ljmu.andre.fitbitconnectionmonitor.Modules.NetworkMonitor;
import com.ljmu.andre.fitbitconnectionmonitor.Utils.TimberUtils;

import junit.framework.Assert;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import timber.log.Timber;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class HookManager implements IXposedHookLoadPackage {
    private boolean hasHooked = false;
    private Context fContext;
    private ClassLoader fClassLoader;

    @Override public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
        try {
            if (!lpparam.packageName.equalsIgnoreCase("com.fitbit.FitbitMobile")) {
                return;
            }

            hasHooked = true;

            TimberUtils.plantAppropriateTree();

            Timber.d("Fitbit is loading!");

            fClassLoader = lpparam.classLoader;

            findAndHookMethod(
                    "android.app.Application",
                    lpparam.classLoader,
                    "attach",
                    Context.class,
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            try {
                                Timber.d("Application attach called");
                                fContext = (Context) param.args[0];
                                Assert.assertNotNull(fContext);

                                findAndHookMethod(
                                        "com.fitbit.FitBitApplication",
                                        lpparam.classLoader,
                                        "onCreate",
                                        new XC_MethodHook() {
                                            @Override protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                                Timber.d("Created fitbit application");
                                                try {
                                                    //new NetworkMonitor().init(fClassLoader, fContext);
                                                    new BluetoothMonitor().init(fClassLoader, fContext);
                                                } catch(Throwable t) {
                                                    Timber.e(t);
                                                }
                                            }
                                        });
                            } catch (Throwable t) {
                                Timber.e(t);
                            }
                        }
                    });
        } catch(Throwable t) {
            Log.e("FCM", t.getMessage());
            t.printStackTrace();
        }
    }
}
