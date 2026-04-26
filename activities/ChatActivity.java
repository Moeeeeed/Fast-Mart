package com.example.a2.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a2.R;
import com.example.a2.adapters.ChatAdapter;
import com.example.a2.models.ChatMessage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    // camelCase variables
    RecyclerView rvChatMessages;
    EditText etChatMessage;
    ImageButton btnSendMessage, btnChatBack;
    
    ChatAdapter chatAdapter;
    List<ChatMessage> chatList;
    
    String currentUserId, remoteUserId, userType;
    DatabaseReference chatRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitychat);

        initViews();
        
        currentUserId = FirebaseAuth.getInstance().getUid();
        
        // Get user type from Prefs to know if this is a Buyer or Seller
        SharedPreferences spref = getSharedPreferences("FastMartPrefs", MODE_PRIVATE);
        userType = spref.getString("accountType", "Buyer");

        remoteUserId = getIntent().getStringExtra("REMOTE_USER_ID");
        
        // If this is a Buyer opening chat, they always talk to the "Seller_AI" portal
        if (remoteUserId == null && userType.equals("Buyer")) {
            remoteUserId = "Seller_AI";
        }

        Log.d("FastMartChat", "Current User: " + currentUserId + " (" + userType + ")");
        Log.d("FastMartChat", "Remote User: " + remoteUserId);

        chatRef = FirebaseDatabase.getInstance().getReference("Chats");

        chatList = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatList, currentUserId);
        
        rvChatMessages.setLayoutManager(new LinearLayoutManager(this));
        rvChatMessages.setAdapter(chatAdapter);

        btnChatBack.setOnClickListener(v -> finish());

        btnSendMessage.setOnClickListener(v -> {
            sendMessage();
        });

        loadMessages();
    }

    private void initViews() {
        rvChatMessages = findViewById(R.id.rvChatMessages);
        etChatMessage = findViewById(R.id.etChatMessage);
        btnSendMessage = findViewById(R.id.btnSendMessage);
        btnChatBack = findViewById(R.id.btnChatBack);
    }

    private void sendMessage() {
        String msgText = etChatMessage.getText().toString().trim();
        if (msgText.isEmpty()) return;

        String msgId = chatRef.push().getKey();
        long time = System.currentTimeMillis();

        ChatMessage message = new ChatMessage(currentUserId, remoteUserId, msgText, time);

        if (msgId != null) {
            chatRef.child(msgId).setValue(message).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    etChatMessage.setText("");
                } else {
                    Toast.makeText(ChatActivity.this, "Failed to send", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void loadMessages() {
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();
                
                for (DataSnapshot data : snapshot.getChildren()) {
                    ChatMessage message = data.getValue(ChatMessage.class);
                    
                    if (message != null) {
                        String sId = message.getSenderId();
                        String rId = message.getReceiverId();

                        // Logic for Buyer
                        if (userType.equals("Buyer")) {
                            // Buyer sees: messages they sent to AI/Seller AND messages they received from AI/Seller
                            boolean involvesMe = sId.equals(currentUserId) || rId.equals(currentUserId);
                            boolean involvesAI = sId.equals("Seller_AI") || rId.equals("Seller_AI");
                            // For buyer, we also show messages from the actual seller to this buyer
                            if (involvesMe) {
                                chatList.add(message);
                            }
                        } 
                        // Logic for Seller
                        else {
                            // Seller sees: messages between them and this specific remoteUserId (Buyer)
                            // PLUS messages that Buyer sent to the AI portal
                            boolean isDirect = (sId.equals(currentUserId) && rId.equals(remoteUserId)) ||
                                             (rId.equals(currentUserId) && sId.equals(remoteUserId));
                            
                            boolean isBuyerToAI = (sId.equals(remoteUserId) && rId.equals("Seller_AI"));
                            
                            if (isDirect || isBuyerToAI) {
                                chatList.add(message);
                            }
                        }
                    }
                }
                chatAdapter.notifyDataSetChanged();
                if (!chatList.isEmpty()) {
                    rvChatMessages.smoothScrollToPosition(chatList.size() - 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatActivity.this, "Chat Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
