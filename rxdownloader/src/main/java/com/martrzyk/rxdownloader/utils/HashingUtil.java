package com.martrzyk.rxdownloader.utils;

import android.text.TextUtils;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

public class HashingUtil {

    /**
     * Simple creation of unique name for files to be stored as
     *
     * @param text - name of file
     * @return - md5 hash of provided text
     */
    public static String createMd5FromText(String text) {
        if (TextUtils.isEmpty(text))
            return null;

        return new String(Hex.encodeHex(DigestUtils.md5(text)));
    }
}
