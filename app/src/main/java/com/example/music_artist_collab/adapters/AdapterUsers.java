package com.example.music_artist_collab.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.music_artist_collab.ChatActivity;
import com.example.music_artist_collab.DashboardActivity;
import com.example.music_artist_collab.R;
import com.example.music_artist_collab.models.ModelUsers;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterUsers extends RecyclerView.Adapter<AdapterUsers.MyHolder>{

    Context context;
    List<ModelUsers> usersList;


    public AdapterUsers(Context context, List<ModelUsers> usersList) {
        this.context = context;
        this.usersList = usersList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewgroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_user,viewgroup,false);

        return new MyHolder(view);
    }

@Override
public void onBindViewHolder(@NonNull MyHolder myholder, int i) {

        String hisUID=usersList.get(i).getUid();
    String userImage = usersList.get(i).getImage();
    String userName = usersList.get(i).getName(); // Corrected
    String userEmail = usersList.get(i).getEmail(); // Corrected

    myholder.mNameTv.setText(userName);
    myholder.mEmailTv.setText(userEmail); // Corrected

    try {
        Picasso.get().load(userImage).placeholder(R.drawable.ic_default_img).into(myholder.mavatarIv);
    } catch (Exception e) {
        e.printStackTrace();
    }

    myholder.itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
//            Intent intent = new Intent(context, ChatActivity.class);
//            context.startActivity(intent);
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("hisUid", hisUID);
            context.startActivity(intent);
        }
    });
}

    @Override
    public int getItemCount() {

        return usersList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{
        ImageView mavatarIv;
        TextView mNameTv,mEmailTv;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            mavatarIv=itemView.findViewById(R.id.avatarIv);
            mNameTv=itemView.findViewById(R.id.nameTv);
            mEmailTv=itemView.findViewById(R.id.EmailTv);

        }
    }

}
