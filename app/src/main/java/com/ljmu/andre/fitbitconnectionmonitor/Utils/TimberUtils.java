package com.ljmu.andre.fitbitconnectionmonitor.Utils;

import android.support.annotation.NonNull;
import android.util.Log;

import com.ljmu.andre.fitbitconnectionmonitor.FCMApplication;

import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Locale;

import de.robv.android.xposed.XposedBridge;
import timber.log.Timber;
import timber.log.Timber.DebugTree;
import timber.log.Timber.Tree;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class TimberUtils {
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-M-yyyy_hh:mm:ss", Locale.ENGLISH);

    public static void plantAppropriateXposedTree() {
        if (FCMApplication.DEBUG)
            plantCheck(new XposedDebugTree(), "XposeDebug");
        else
            plantCheck(new XposedReleaseTree(), "XposeRelease");
    }

    public static void plantAppropriateTree() {
        if (FCMApplication.DEBUG) {
            plantCheck(new DebugTree() {
                @Override
                protected String createStackElementTag(@NonNull StackTraceElement element) {
                    return String.format(
                            "%s-[%s ⇢ %s:%s]",
                            FCMApplication.MODULE_TAG,
                            super.createStackElementTag(element),
                            element.getMethodName(),
                            element.getLineNumber());
                }
            }, "Debug");
        } else
            plantCheck(new ReleaseTree(), "Release");
    }

    private static void plantCheck(Tree tree, String treeName) {
        Timber.plant(tree);
        Timber.d("Planted [Tree:%s]", treeName);
    }

    private static class ReleaseTree extends DebugTree {
        static final HashSet<Integer> priorityWhitelist;

        static {
            priorityWhitelist = new HashSet<>();
            priorityWhitelist.add(Log.ERROR);
            priorityWhitelist.add(Log.ASSERT);
            priorityWhitelist.add(Log.WARN);
        }

        @Override
        protected boolean isLoggable(String tag, int priority) {
            return priorityWhitelist.contains(priority);
        }

        @Override protected void log(int priority, String tag, String message, Throwable t) {
            if (isLoggable(tag, priority))
                super.log(priority, tag, message, t);
        }

        @Override
        protected String createStackElementTag(StackTraceElement element) {
            return String.format(
                    "%s-[%s ⇢ %s:%s]",
                    FCMApplication.MODULE_TAG,
                    super.createStackElementTag(element),
                    element.getMethodName(),
                    element.getLineNumber());
        }
    }

    private static class XposedReleaseTree extends ReleaseTree {
        static final HashSet<Integer> priorityWhitelist;

        static {
            priorityWhitelist = new HashSet<>();
            priorityWhitelist.add(Log.ERROR);
            priorityWhitelist.add(Log.ASSERT);
            priorityWhitelist.add(Log.WARN);
        }

        @Override protected void log(int priority, String tag, String message, Throwable t) {
            if (isLoggable(tag, priority)) {
                super.log(priority, tag, message, t);

                XposedBridge.log(tag + ": " + message);
                if (t != null)
                    XposedBridge.log(t);
            }
        }
    }

    private static class XposedDebugTree extends DebugTree {
        @Override
        protected String createStackElementTag(@NonNull StackTraceElement element) {
            return String.format(
                    "%s-[%s ⇢ %s:%s]",
                    FCMApplication.MODULE_TAG,
                    super.createStackElementTag(element),
                    element.getMethodName(),
                    element.getLineNumber());
        }

        @Override protected void log(int priority, String tag, String message, Throwable t) {
            super.log(priority, tag, message, t);

            XposedBridge.log(tag + ": " + message);
            if (t != null)
                XposedBridge.log(t);
        }
    }
}
