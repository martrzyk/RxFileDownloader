package com.martrzyk.filerxdownloader;

import android.content.Context;

import com.martrzyk.filerxdownloader.recycler_view.BaseRecyclerViewAdapter;

import java.util.List;

/**
 * Created by mar3k on 2017-02-22.
 */

public class ExamplePictureListAdapter extends BaseRecyclerViewAdapter {
    Context context;
    private List<Picture> pictures;

    public void refresh() {
        removeAll();

        for (Picture picture : pictures)
            addItem(new PictureItem(picture, context));

        notifyDataSetChanged();
    }

    public void setPictures(List<Picture> pictures) {
        this.pictures = pictures;
    }
}
