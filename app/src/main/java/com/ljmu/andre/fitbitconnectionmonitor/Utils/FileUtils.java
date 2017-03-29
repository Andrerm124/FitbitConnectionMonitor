package com.ljmu.andre.fitbitconnectionmonitor.Utils;

import android.os.Environment;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;

import timber.log.Timber;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class FileUtils {
    public static String getExternalPath() {
        try {
            Class<?> environment_cls = Class.forName("android.os.Environment");
            Method setUserRequiredM = environment_cls.getMethod("setUserRequired", boolean.class);
            setUserRequiredM.invoke(null, false);

            return Environment.getExternalStorageDirectory().getAbsolutePath();
        } catch (Exception e) {
            Timber.e(e, "Get external path exception");
        }

        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    public static String createCSV(Object... params) {
        StringBuilder builder = new StringBuilder();
        Iterable iterable = Arrays.asList(params);
        Iterator iterator = iterable.iterator();

        while(iterator.hasNext()) {
            Object next = iterator.next();
            String value;

            if(next != null)
                value = next.toString();
            else
                value = "-N-";

            builder.append(value);

            if(iterator.hasNext())
                builder.append(',');
        }

        return builder.toString();
    }
}
