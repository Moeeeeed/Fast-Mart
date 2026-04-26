package com.example.a2.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a2.R;
import com.example.a2.models.ChatMessage;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private List<ChatMessage> chatList;
    private String currentUserId;

    public ChatAdapter(List<ChatMessage> chatList, String currentUserId) {
        this.chatList = chatList;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemchatmessage, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatMessage message = chatList.get(position);

        if (message.getSenderId().equals(currentUserId)) {
            // Show sender layout, hide receiver
            holder.layoutSender.setVisibility(View.VISIBLE);
            holder.layoutReceiver.setVisibility(View.GONE);
            holder.tvSenderMessage.setText(message.getMessageText());
        } else {
            // Show receiver layout, hide sender
            holder.layoutSender.setVisibility(View.GONE);
            holder.layoutReceiver.setVisibility(View.VISIBLE);
            holder.tvReceiverMessage.setText(message.getMessageText());
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layoutSender, layoutReceiver;
        TextView tvSenderMessage, tvReceiverMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutSender = itemView.findViewById(R.id.layoutSender);
            layoutReceiver = itemView.findViewById(R.id.layoutReceiver);
            tvSenderMessage = itemView.findViewById(R.id.tvSenderMessage);
            tvReceiverMessage = itemView.findViewById(R.id.tvReceiverMessage);
        }
    }
}
