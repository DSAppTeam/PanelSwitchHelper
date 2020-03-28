package com.example.demo.chat;

import android.view.View;
import android.widget.Toast;

import com.effective.databinding.VhChatRightLayoutBinding;
import com.example.demo.emotion.EmojiSpanBuilder;

/**
 * the right of chatting item
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
        binding.avatar.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(view.getContext(),"长按了头像",Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        binding.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(),"点击了头像",Toast.LENGTH_SHORT).show();
            }
        });
        binding.text.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(view.getContext(),"长按了消息",Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        binding.text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(),"点击了消息",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
