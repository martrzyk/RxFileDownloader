package com.martrzyk.filerxdownloader.recycler_view;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class BaseRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<RecyclerViewItem> items = Collections.synchronizedList(new ArrayList());
    private Map<RecyclerViewItem, Integer> itemPositionMap = new HashMap<>();
    private AtomicInteger loadedItemLeft = new AtomicInteger(0);
    private OnLoadFinishListener listener;

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getViewType();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        for (RecyclerViewItem item : items) {
            if (item.getViewType() == viewType) {
                return item.getViewHolder(parent);
            }
        }
        return null;
    }

    public void addItem(RecyclerViewItem newItem) {
        itemPositionMap.put(newItem, itemPositionMap.size());
        newItem.setViewType(items.size() + 1);
        items.add(newItem);
    }

    public void setOnLoadFinishListener(OnLoadFinishListener listener) {
        this.listener = listener;
    }

    private synchronized void addItemOnCorrectPosition(RecyclerViewItem newItem) {
        synchronized (items) {
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i) == newItem) {
                    notifyItemChanged(i);
                    return;
                }
            }

            Integer newItemPosition = itemPositionMap.get(newItem);
            if (newItemPosition != null) {
                for (int i = items.size() - 1; i >= 0; i--) {
                    int currentItemPosition = itemPositionMap.get(items.get(i));
                    if (newItemPosition < currentItemPosition) {
                        items.add(i, newItem);
                        notifyItemInserted(i);
                        return;
                    }
                }
                items.add(newItem);
                newItem.setViewType(items.size());
                notifyItemInserted(items.size());
            }
        }
    }

    private void removeItem(RecyclerViewItem item) {
        synchronized (items) {
            int position = getItemPosition(item);
            if (position > 0) {
                items.remove(item);
                notifyItemRemoved(position);
            }
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        items.get(position).bind(holder);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public int getItemPosition(RecyclerViewItem item) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i) == item) {
                return i;
            }
        }
        return -1;
    }

    public void removeAll() {
        int size = items.size();
        for (RecyclerViewItem item : items) {
            if (item instanceof Loadable) {
                ((Loadable) item).setOnLoadListener((newItem, hasData) -> {
                    //do nothing
                });
            }
        }
        items.clear();
        notifyItemRangeRemoved(0, size);
        loadedItemLeft.set(0);
        itemPositionMap.clear();
    }

    public void removeAllWithoutNotify() {
        int size = items.size();
        for (RecyclerViewItem item : items) {
            if (item instanceof Loadable) {
                ((Loadable) item).setOnLoadListener((newItem, hasData) -> {
                    //do nothing
                });
            }
        }
        items.clear();
        loadedItemLeft.set(0);
        itemPositionMap.clear();
    }

    public void onDestroy() {
        for (RecyclerViewItem item : items) {
            item.onDestroy();
        }
    }

    public interface OnLoadFinishListener {
        void onAllItemLoaded();
    }
}
