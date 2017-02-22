package com.martrzyk.filerxdownloader.recycler_view;

public interface Loadable {
    void setOnLoadListener(OnLoadedListener listener);
    void loadData();

    interface OnLoadedListener {
        void onLoaded(RecyclerViewItem item, boolean hasData);
    }
}
