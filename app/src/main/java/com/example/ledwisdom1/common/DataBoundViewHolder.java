package com.example.ledwisdom1.common;

import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;

//通用的ViewHolder
public class DataBoundViewHolder<T extends ViewDataBinding> extends RecyclerView.ViewHolder {
    public final T binding;

    public DataBoundViewHolder(T t) {
        super(t.getRoot());
        binding = t;
    }
}
