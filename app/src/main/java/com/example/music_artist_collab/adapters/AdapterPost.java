package com.example.music_artist_collab.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.music_artist_collab.ChatActivity;
import com.example.music_artist_collab.R;
import com.example.music_artist_collab.ViewProfile;
import com.example.music_artist_collab.models.ModelPost;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.squareup.picasso.Picasso;
import android.os.Handler;
import com.google.android.exoplayer2.MediaItem;

import java.util.List;

import android.app.ProgressDialog;
public class AdapterPost extends RecyclerView.Adapter<AdapterPost.MyHolder> {
    private SimpleExoPlayer player;
    private PlayerView playerView;

    Context context;
    ProgressDialog progressDialog;

    List<ModelPost> postList;
    public AdapterPost(Context context, List<ModelPost> postList) {
        this.context = context;
        this.postList = postList;
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Processing...");
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewgroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_posts, viewgroup, false);
        playerView = view.findViewById(R.id.playerView);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int position) {
        // Now you can access the data from the ModelPost object



        String uid = postList.get(position).getUid();
        String uEmail = postList.get(position).getuEmail();
        String pTitle =postList.get(position).getpTitle();
        String pImage = postList.get(position).getpImage();
        String pDescr =postList.get(position).getpDescription();
        String uName = postList.get(position).getuName();
        String uDp=postList.get(position).getuDp();
        String ptimestamp=postList.get(position).getpTime();
        Log.d("AdapterPost", "adapter Description: " + pDescr);

//        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
//        cal.setTimeInMillis(Long.parseLong(ptimestamp));
//        String pTime = DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString();


        String pTime="1 PM";


        myHolder.uNameTv.setText(uName);
        myHolder.pTimeTv.setText(pTime);
        myHolder.pdescriptionTv.setText(pDescr);
        myHolder.pTitleTv.setText(pTitle);


        if (pImage != null && !pImage.isEmpty() ) {
            try {
                Picasso.get().load(pImage).placeholder(R.drawable.ic_default_img).into(myHolder.uPictureIv);
            } catch (Exception e) {
                Toast.makeText(context, "Adapter error", Toast.LENGTH_SHORT).show();

                e.printStackTrace();

            }
        } else {
            myHolder.uPictureIv.setVisibility(View.GONE);
        }



//        if (uDp != null && !uDp.isEmpty()) {
            try {
                Log.d("user ","DP check"+uDp);
                Picasso.get().load(uDp).placeholder(R.drawable.ic_default_img).into(myHolder.pImageIv);
            } catch (Exception e) {
//                Toast.makeText(context, "Adapter error", Toast.LENGTH_SHORT).show();

                e.printStackTrace();

            }
//        } else {
//
//        }



        String videoUrl = postList.get(position).getpVideo();
        if (videoUrl != null && !videoUrl.isEmpty()) {
            playerView = myHolder.playerView;
            initializePlayer(videoUrl, playerView);
        } else {
            myHolder.playerView.setVisibility(View.GONE);
            releasePlayer(myHolder);

        }




        myHolder.likebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(context, "like", Toast.LENGTH_SHORT).show();

            }
        });

        myHolder.userProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show(); // Show the progress dialog

                // Simulate a delay for processing (you can replace this with your actual processing logic)
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss(); // Dismiss the progress dialog

                        // Create an Intent to open the ViewProfile activity
//                        String userId = postList.get(myHolder.getAdapterPosition()).getUid();
                        Intent intent = new Intent(context, ViewProfile.class);

                        // Pass data to the ViewProfile activity (user ID in this case)
                        intent.putExtra("userId", uid);

                        // Start the ViewProfile activity
                        context.startActivity(intent);
                    }
                }, 2000); // Delay for 2 seconds (adjust as needed)
            }
        });


        myHolder.collab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show(); // Show the progress dialog

                // Simulate a delay for processing (you can replace this with your actual processing logic)
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss(); // Dismiss the progress dialog
                        Intent intent = new Intent(context, ChatActivity.class);
                        intent.putExtra("hisUid", uid);
                        context.startActivity(intent);
                    }
                }, 2000); // Delay for 2 seconds (adjust as needed)
            }
        });


    }



    @Override
    public int getItemCount() {
        return postList.size();
    }


    class MyHolder extends RecyclerView.ViewHolder {
        ImageView uPictureIv;
        ImageView pImageIv;
        TextView uNameTv,pTimeTv,pTitleTv,pdescriptionTv,pLikesTv;
        Button likebtn,collab,userProfile;
        PlayerView playerView;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            uPictureIv=itemView.findViewById(R.id.mainPic);
            pImageIv=itemView.findViewById(R.id.dp);
            uNameTv=itemView.findViewById(R.id.uNameTv);
            pTimeTv=itemView.findViewById(R.id.pTimeTv);
            pTitleTv=itemView.findViewById(R.id.pTitleTv);
            pdescriptionTv=itemView.findViewById(R.id.pDescriptionTv);
            likebtn=itemView.findViewById(R.id.likeBtn);
            userProfile=itemView.findViewById(R.id.userProfileBtn);
            collab=itemView.findViewById(R.id.collabBtn);
            playerView = itemView.findViewById(R.id.playerView);

        }
        // Define your views here as you already have in your code
    }
    public void onViewRecycled(@NonNull MyHolder holder) {
        super.onViewRecycled(holder);
        releasePlayer(holder);
    }

    private void initializePlayer(String videoUrl, PlayerView playerView) {
        if (player == null) {
            player = new SimpleExoPlayer.Builder(context).build();
            playerView.setPlayer(player);
        }

        MediaItem mediaItem = MediaItem.fromUri(videoUrl);
        player.setMediaItem(mediaItem);
        player.prepare();
        player.setPlayWhenReady(false);
    }

    private void releasePlayer(MyHolder holder) {
        if (player != null) {
            player.release();
            player = null;
        }
    }

}