package com.example.music_artist_collab.notifications;

import android.content.Intent;

public class Data {
  String user,body,Title,Sent;

  Integer icon;

    public Data() {
    }

    public Data(String user, String body, String title, String sent, Integer icon) {
        this.user = user;
        this.body = body;
        Title = title;
        Sent = sent;
        this.icon = icon;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getSent() {
        return Sent;
    }

    public void setSent(String sent) {
        Sent = sent;
    }

    public Integer getIcon() {
        return icon;
    }

    public void setIcon(Integer icon) {
        this.icon = icon;
    }
}
