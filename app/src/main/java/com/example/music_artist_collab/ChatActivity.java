package com.example.music_artist_collab;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.music_artist_collab.adapters.AdapterChat;
import com.example.music_artist_collab.models.ModelChat;
import com.example.music_artist_collab.models.ModelUsers;
import com.example.music_artist_collab.notifications.APIService;
import com.example.music_artist_collab.notifications.Client;
import com.example.music_artist_collab.notifications.Data;
import com.example.music_artist_collab.notifications.Response;
import com.example.music_artist_collab.notifications.Sender;
import com.example.music_artist_collab.notifications.Token;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;

public class ChatActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerView;
    ImageView profileIv;
    TextView nameTv,userstatusTv;
    EditText messageEt;
    ImageButton sendBtn;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference userDbRef;
    ValueEventListener seenListner;
    DatabaseReference userRefForSeen;
    List<ModelChat> chatList;
    AdapterChat adapterChat;
    String hisUid;
    String myUid;
    String hisimage;
    APIService apiService;
    boolean notify=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        recyclerView=findViewById(R.id.chat_recyclerView);
        profileIv=findViewById(R.id.profileIv);
        nameTv=findViewById(R.id.nameTv);
        userstatusTv=findViewById(R.id.userStatusTv);
        messageEt=findViewById(R.id.messageEt);
        sendBtn=findViewById(R.id.sendBtn);

        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        apiService= Client.getRetrofit("https://fcm.googleapis.com/").create(APIService.class);


        Intent intent=getIntent();
        hisUid=intent.getStringExtra("hisUid");

        firebaseAuth=FirebaseAuth.getInstance();

        firebaseDatabase=FirebaseDatabase.getInstance();
        userDbRef=firebaseDatabase.getReference("users");


        Query userQuery =userDbRef.orderByChild("uid").equalTo(hisUid);
        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    String name=""+ds.child("name").getValue();
                     hisimage=""+ds.child("image").getValue();
                    String typingStatus=""+ds.child("typingTo").getValue();
                    if(typingStatus.equals(myUid)){
                        userstatusTv.setText("typing...");
                    }else {

                        String onlineStatus=""+ds.child("onlineStatus").getValue();
                        if(onlineStatus.equals("online")){
                            userstatusTv.setText(onlineStatus);
                        }else{
                            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                            cal.setTimeInMillis(Long.parseLong(onlineStatus));
                            String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString();
                            userstatusTv.setText("Last Seen at :"+dateTime);
                        }
                    }
                    nameTv.setText(name);

                    try {
                        Picasso.get().load(hisimage).placeholder(R.drawable.ic_default_image_white).into(profileIv);
                    }catch(Exception e){
                        Picasso.get().load(R.drawable.ic_default_image_white).into(profileIv);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {


            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                notify = true;

                String message=messageEt.getText().toString().trim();
                if (TextUtils.isEmpty(message)){
                    Toast.makeText(ChatActivity.this, "Cannot send the empty message", Toast.LENGTH_SHORT).show();
                }
                else{

                    sendMessage(message);
                }
                messageEt.setText("");

            }
        });
        messageEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.toString().trim().length()==0){
                    checkTypingStatus("NoOne");
                }else {
                    checkTypingStatus(hisUid);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    readMessages();
    seenMessage();

    }

    private void seenMessage() {
        userRefForSeen=FirebaseDatabase.getInstance().getReference("Chats");
        seenListner=userRefForSeen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelChat chat=ds.getValue(ModelChat.class);
                    assert chat != null;
                    if(chat.getReceiver().equals(myUid) && chat.getSender().equals(hisUid)){
                        HashMap<String,Object> hasSeenHashMap = new HashMap<>();
                        hasSeenHashMap.put("isSeen",true);
                        ds.getRef().updateChildren(hasSeenHashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void readMessages() {
        chatList=new ArrayList<>();
        DatabaseReference dbRef= FirebaseDatabase.getInstance().getReference("Chats");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatList.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelChat chat=ds.getValue(ModelChat.class);
                    if(chat.getReceiver().equals(myUid) && chat.getSender().equals(hisUid)||
                    chat.getReceiver().equals(hisUid)&& chat.getSender().equals(myUid)){
                        chatList.add(chat);
                    }
                    adapterChat=new AdapterChat(ChatActivity.this,chatList,hisimage);
                    adapterChat.notifyDataSetChanged();
                    recyclerView.setAdapter(adapterChat);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendMessage(String message) {
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference();

        String timeStamp= String.valueOf(System.currentTimeMillis());
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("sender",myUid);
        hashMap.put("receiver",hisUid);
        hashMap.put("message",message);
        hashMap.put("timeStamp",timeStamp);
        hashMap.put("isSeen",false);
        databaseReference.child("Chats").push().setValue(hashMap);



        String msg=message;
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("users").child(myUid);
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ModelUsers users=dataSnapshot.getValue(ModelUsers.class);
                if(notify){
                    assert users != null;
                    sendNotification(hisUid,users.getName(),message);
                }
                notify=false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendNotification(String hisUid, String name, String message) {
        DatabaseReference allTokens=FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = allTokens.orderByKey().equalTo(hisUid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    Token token = ds.getValue(Token.class);
                    Data data =  new Data(myUid,name+":"+message,"New Message",hisUid,R.drawable.ic_default_img);
                    assert token != null;
                    Sender sender = new Sender(data,token.getToken());
                    apiService.sendNotification(sender).enqueue(new Callback<Response>() {
                                @Override
                                public void onResponse(@NonNull Call<Response> call, retrofit2.Response<Response> response) {
                                    Toast.makeText(ChatActivity.this,""+response.message(),Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(Call<Response> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkUserStatus(){
        FirebaseUser user= firebaseAuth.getCurrentUser();
        if(user!=null){
            myUid=user.getUid();
        }
        else{
            startActivity(new Intent(this,MainActivity.class));
            finish();
        }
    }
void  checkOnlineStatus(String status){
        DatabaseReference dbRef=FirebaseDatabase.getInstance().getReference("users").child(myUid);
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("onlineStatus",status);
    dbRef.updateChildren(hashMap);

}
void  checkTypingStatus(String typing){
        DatabaseReference dbRef=FirebaseDatabase.getInstance().getReference("users").child(myUid);
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("typingTo",typing);
        dbRef.updateChildren(hashMap);

    }
    @Override
    protected void onStart() {
        checkUserStatus();
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        String timeStamp= String.valueOf(System.currentTimeMillis());
        checkOnlineStatus(timeStamp);
        checkTypingStatus("NoOne");
        userRefForSeen.removeEventListener(seenListner);

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkOnlineStatus("online");

    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main,menu);
        menu.findItem(R.id.action_search).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id=item.getItemId();
        if(id==R.id.action_logout){
            firebaseAuth.signOut();
            checkUserStatus();
        }
        return super.onOptionsItemSelected(item);
    }
}