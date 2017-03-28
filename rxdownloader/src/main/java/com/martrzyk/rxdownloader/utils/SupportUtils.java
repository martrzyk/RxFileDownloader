package com.martrzyk.rxdownloader.utils;

import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

public class SupportUtils {

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

    /**
     * Getting MIME type from url
     *
     * @param url = file path or whatever suitable URL you want.
     * @return - string value of mime
     */
    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }

        return type;
    }

    public static boolean isAnyTextEmpty(CharSequence... texts) {
        if (texts.length <= 0)
            return true;

        for (CharSequence charSequence : texts) {
            if (TextUtils.isEmpty(charSequence))
                return true;
        }

        return false;
    }
}
