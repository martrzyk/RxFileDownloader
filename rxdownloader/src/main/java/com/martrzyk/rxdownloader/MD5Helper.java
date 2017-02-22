package com.martrzyk.rxdownloader;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

public class MD5Helper {

    public static String md5(String text) {
        if (text == null)
            return "";

        String s = new String(Hex.encodeHex(DigestUtils.md5(text)));

        return s;
    }
}
