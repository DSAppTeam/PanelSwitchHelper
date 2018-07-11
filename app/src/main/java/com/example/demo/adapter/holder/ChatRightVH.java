package com.example.demo.adapter.holder;

import com.effective.databinding.VhChatRightLayoutBinding;
import com.example.demo.bean.ChatInfo;
import com.example.demo.emotion.EmojiSpanBuilder;

/**
 * Created by yummyLau on 18-7-11
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */
public class ChatRightVH extends ChatBaseVH<VhChatRightLayoutBinding, ChatInfo> {

    public ChatRightVH(VhChatRightLayoutBinding binding) {
        super(binding);
    }

    @Override
    public void bindData(ChatInfo chatInfo, int position) {
        binding.text.setText(EmojiSpanBuilder.buildEmotionSpannable(binding.getRoot().getContext(), chatInfo.message));
    }
}
