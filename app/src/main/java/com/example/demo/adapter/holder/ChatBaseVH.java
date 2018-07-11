package com.example.demo.adapter.holder;

import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by yummyLau on 18-7-11
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */
public abstract class ChatBaseVH<dataBinding extends ViewDataBinding, data> extends RecyclerView.ViewHolder {

    protected dataBinding binding;
    protected data data;

    public ChatBaseVH(dataBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public abstract void bindData(data data, int position);
}
