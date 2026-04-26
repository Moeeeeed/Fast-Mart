package com.example.a2.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a2.R;
import com.example.a2.activities.ChatActivity;
import com.example.a2.adapters.ChatListAdapter;
import com.example.a2.models.ChatMessage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChatListFragment extends Fragment {

    RecyclerView rvChatList;
    ChatListAdapter adapter;
    List<String> userList;
    DatabaseReference chatRef;
    String currentUserId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);

        rvChatList = view.findViewById(R.id.rvChatList);
        userList = new ArrayList<>();
        currentUserId = FirebaseAuth.getInstance().getUid();
        chatRef = FirebaseDatabase.getInstance().getReference("Chats");

        adapter = new ChatListAdapter(userList, userId -> {
            Intent intent = new Intent(getActivity(), ChatActivity.class);
            intent.putExtra("REMOTE_USER_ID", userId);
            startActivity(intent);
        });

        rvChatList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvChatList.setAdapter(adapter);

        loadActiveChats();

        return view;
    }

    private void loadActiveChats() {
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Set<String> uniqueUsers = new HashSet<>();
                Log.d("FastMart", "Found " + snapshot.getChildrenCount() + " total messages");

                for (DataSnapshot data : snapshot.getChildren()) {
                    ChatMessage msg = data.getValue(ChatMessage.class);
                    if (msg != null) {
                        // Sellers should see anyone who messaged "Seller_AI" OR anyone they have messaged
                        if (msg.getReceiverId().equals("Seller_AI")) {
                            uniqueUsers.add(msg.getSenderId());
                        } else if (msg.getSenderId().equals(currentUserId)) {
                            uniqueUsers.add(msg.getReceiverId());
                        } else if (msg.getReceiverId().equals(currentUserId)) {
                            uniqueUsers.add(msg.getSenderId());
                        }
                    }
                }
                userList.clear();
                userList.addAll(uniqueUsers);
                Log.d("FastMart", "Unique chat partners: " + userList.size());
                
                if (userList.isEmpty()) {
                    Toast.makeText(getContext(), "No active customer chats found.", Toast.LENGTH_SHORT).show();
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
