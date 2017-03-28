package com.martrzyk.rxdownloader.model;

import android.text.TextUtils;

import com.martrzyk.rxdownloader.utils.SupportUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * POJO class for maintaining downloads
 * Created by mar3k on 2016-10-14.
 */

@Getter
@Setter
@AllArgsConstructor
public class Download {
    String name;
    String tag;
    String url;

    public String getHashedTag() {
        if (TextUtils.isEmpty(tag)) {
            throw new NoSuchFieldError("tag cannot be empty!");
        }

        return SupportUtils.createMd5FromText(tag);
    }
}
