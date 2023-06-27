package com.icplaza.sdk.common;

import java.util.HashMap;
import java.util.Map;


public class GlobalCtx {
    private static Map<String, AccountInfo> map = new HashMap<>();
    private static boolean lock = false;

    /**
     */
    public synchronized static AccountInfo getSequence(String key) {
        if (map.get(key) == null) {
            return null;
        }
        AccountInfo accountInfo = map.get(key);
        AccountInfo accountInfo1 = accountInfo.clone();
        accountInfo1.setSequenceNumber(accountInfo1.getSequenceNumber() + 1);
        setSequence(key, accountInfo1);
        return accountInfo;
    }

    /**
     */
    public synchronized static boolean setSequence(String key, AccountInfo accountInfo) {
        if (map.put(key, accountInfo) == null) {
            return false;
        }
        return true;
    }

    /**
     */
    public synchronized static void reset(String key) {
        map.remove(key);
    }
}
