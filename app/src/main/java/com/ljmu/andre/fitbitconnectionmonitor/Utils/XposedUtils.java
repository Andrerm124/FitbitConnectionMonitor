package com.ljmu.andre.fitbitconnectionmonitor.Utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import timber.log.Timber;

import static de.robv.android.xposed.XposedBridge.hookAllConstructors;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class XposedUtils {
    public static void hookAllMethods(String className, ClassLoader cl, boolean hookSubClasses, boolean hookSuperClasses) {
        try {
            Timber.d("Starting allhook");

            final Class targetClass = findClass(className, cl);
            Method[] allMethods = targetClass.getDeclaredMethods();

            hookAllConstructors(targetClass, new XC_MethodHook() {
                @Override protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    Timber.d("HookTrigger: " + targetClass.getName() + ".<init>");
                }
            });

            Timber.d("Methods to hook: " + allMethods.length);
            for (final Method baseMethod : allMethods) {
                final Class<?>[] paramList = baseMethod.getParameterTypes();
                final String fullMethodString = targetClass.getName() + "." + baseMethod.getName() + "(" + Arrays.toString(paramList) + ") -> " + baseMethod.getReturnType();

                if (Modifier.isAbstract(baseMethod.getModifiers())) {
                    Timber.w("Abstract method: " + fullMethodString);
                    continue;
                }

                Object[] finalParam = new Object[paramList.length + 1];

                System.arraycopy(paramList, 0, finalParam, 0, paramList.length);

                XC_MethodHook hook = new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Object result = param.getResult();
                        Timber.d("HookTrigger: " + fullMethodString + (result != null ? " RET: " + result.toString() : ""));
                    }
                };

                finalParam[paramList.length] = hook;

                findAndHookMethod(targetClass, baseMethod.getName(), finalParam);
                Timber.d("Hooked method: " + fullMethodString);
            }

            if (hookSubClasses) {
                Class[] subClasses = targetClass.getClasses();

                Timber.d("Hooking Subclasses: " + subClasses.length);

                for (Class subClass : subClasses)
                    hookAllMethods(subClass.getName(), cl, true, hookSuperClasses);
            }

            if (hookSuperClasses) {
                Class superClass = targetClass.getSuperclass();
                if (superClass == null || superClass.getSimpleName().equals("Object"))
                    return;

                Timber.d("Found Superclass: " + superClass.getSimpleName());
                hookAllMethods(superClass.getName(), cl, false, true);
            }
        } catch(Throwable t) {
            Timber.e(t);
        }
    }



    /**
     * Logs the current stack trace(ie. the chain of calls to get where you are now)
     */
    public static void logStackTrace() {
       logStackTrace(Integer.MAX_VALUE);
    }

    public static void logStackTrace(int length) {
        StackTraceElement[] stackTraceElements = new Throwable().getStackTrace();

        for (StackTraceElement traceElement : stackTraceElements) {
            if(length-- <= 0)
                return;

            Timber.d("Stack trace: [Class: %s] [Method: %s] [Line: %s]", traceElement.getClassName(), traceElement.getMethodName(), traceElement.getLineNumber());
        }
    }

    public static void logEntireClass(Object obj, int classCap) {
        logEntireClass(obj.getClass(), obj, classCap);
    }

    public static void logEntireClass(Class objClass, Object obj, int classCap) {
        for(int i = 0; i <= classCap; i++) {
            logEntireClass(objClass, obj);
            objClass = objClass.getSuperclass();

            if(objClass == null)
                break;
        }
    }

    public static void logEntireClass(Class objClass, Object obj) {
        Field[] arrFields = objClass.getFields();

        StringBuilder toStringBuilder = new StringBuilder(objClass.getSimpleName() + " {");

        for (Field field : arrFields) {
            toStringBuilder.append("\n\t")
                    .append(field.getName())
                    .append(": ");

            try {
                Object value = field.get(obj);
                if(value != null)
                    toStringBuilder.append(value.toString());
                else
                    toStringBuilder.append("NULL");
            } catch (IllegalAccessException e) {
                toStringBuilder.append("IAE");
            }
        }

        toStringBuilder.append("}");

        Timber.d(toStringBuilder.toString());
    }
}
