package com.example.demo.chat;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.effective.R;
import com.effective.databinding.VhChatLeftLayoutBinding;
import com.effective.databinding.VhChatRightLayoutBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * chatting pager adapter
 * Created by yummyLau on 18-7-11
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatBaseVH> {

    private List<ChatInfo> mData;
    private Context mContext;

    public ChatAdapter(Context context) {
        mData = new ArrayList<>();
        mContext = context;
    }

    public void insertInfo(ChatInfo chatInfo) {
        if (chatInfo != null) {
            mData.add(chatInfo);
            notifyItemInserted(mData.size() - 1);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mData.get(position).owner) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public ChatBaseVH onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            VhChatRightLayoutBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.vh_chat_right_layout, parent, false);
            return new ChatRightVH(binding);
        } else {
            VhChatLeftLayoutBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.vh_chat_left_layout, parent, false);
            return new ChatLeftVH(binding);
        }
    }

    @Override
    public void onBindViewHolder(ChatBaseVH holder, int position) {
        holder.bindData(mData.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
