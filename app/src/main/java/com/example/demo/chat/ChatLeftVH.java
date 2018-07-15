package com.example.demo.chat;


import com.effective.databinding.VhChatLeftLayoutBinding;
import com.example.demo.emotion.EmojiSpanBuilder;

/**
 * the left of chatting item
 * Created by yummyLau on 18-7-11
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */
public class ChatLeftVH extends ChatBaseVH<VhChatLeftLayoutBinding, ChatInfo> {

    public ChatLeftVH(VhChatLeftLayoutBinding binding) {
        super(binding);
    }

    @Override
    public void bindData(ChatInfo chatInfo, int position) {
        binding.text.setText(EmojiSpanBuilder.buildEmotionSpannable(binding.getRoot().getContext(), chatInfo.message));
    }
}
