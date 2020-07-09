package com.example.demo.scene.chat.adapter;


import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

/**
 * base {@link androidx.recyclerview.widget.RecyclerView} for chatting pager.
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

    @SuppressWarnings("unchecked")
    public abstract void bindData(data data, int position);
}
