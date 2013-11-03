/*
 * Copyright (C) 2009-2010 Aubort Jean-Baptiste (Rorist)
 * Licensed under GNU's GPL 2, see README
 */

// http://standards.ieee.org/regauth/oui/oui.txt

package edu.hust.wifilanchat.networking;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.util.Log;

public class HardwareAddress {

    private final static String TAG = "HardwareAddress";
//    private final static String REQ = "select vendor from oui where mac=?";
    // 0x1 is HW Type:  Ethernet (10Mb) [JBP]
    // 0x2 is ARP Flag: completed entry (ha valid)
    private final static String MAC_RE = "^%s\\s+0x1\\s+0x2\\s+([:0-9a-fA-F]+)\\s+\\*\\s+\\w+$";
    private final static int BUF = 8 * 1024;
//    private WeakReference<Activity> mActivity;

    public HardwareAddress(Activity activity) {
    }

    public static String getHardwareAddress(String ip) {
        String hw = NetInfo.NOMAC;
        try {
            if (ip != null) {
                String ptrn = String.format(MAC_RE, ip.replace(".", "\\."));
                Pattern pattern = Pattern.compile(ptrn);
                BufferedReader bufferedReader = new BufferedReader(new FileReader("/proc/net/arp"), BUF);
                String line;
                Matcher matcher;
                while ((line = bufferedReader.readLine()) != null) {
                    matcher = pattern.matcher(line);
                    if (matcher.matches()) {
                        hw = matcher.group(1);
                        break;
                    }
                }
                bufferedReader.close();
            } else {
                Log.e(TAG, "ip is null");
            }
        } catch (IOException e) {
            Log.e(TAG, "Can't open/read file ARP: " + e.getMessage());
            return hw;
        }
        return hw;
    }

}
