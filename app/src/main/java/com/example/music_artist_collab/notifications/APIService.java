package com.example.music_artist_collab.notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({

            "Content-Type:application/json",
"Authorization:key=AAAAGu8NXdA:APA91bHD_fyoWcd5eOG4b7s2LsS4j8gVTFT4Md9b2bjxG4BipFIATdhQm4U6FX1Yt-eeFOhQQQPA2VJxtiFtHaUf7EKnWaEPFYWoDDKg9HAXTg7Y5elklwh7sa7HEpGxx5WJoWenHFSL"
})

    @POST("fcm/send")
    Call<Response> sendNotification(@Body Sender body);
}
