package com.example.music_artist_collab.models;

//package com.example.music_artist_collab.models;
//
//public class ModelPost {
//    String pId,pTitle,pImage,pdescr,pTime,uid,uEmail,uDp,uName;
//public ModelPost(){}
//    public ModelPost(String pId, String pTitle, String pImage, String pdescr, String pTime, String uid, String uEmail, String uDp, String uName) {
//        this.pId = pId;
//        this.pTitle = pTitle;
//        this.pImage = pImage;
//        this.pdescr = pdescr;
//        this.pTime = pTime;
//        this.uid = uid;
//        this.uEmail = uEmail;
//        this.uDp = uDp;
//        this.uName = uName;
//    }
//    public String getpId() {
//        return pId;
//    }
//
//    public void setpId(String pId) {
//        this.pId = pId;
//    }
//
//    public String getpTitle() {
//        return pTitle;
//    }
//
//    public void setpTitle(String pTitle) {
//        this.pTitle = pTitle;
//    }
//
//    public String getpImage() {
//        return pImage;
//    }
//
//    public void setpImage(String pImage) {
//        this.pImage = pImage;
//    }
//
//    public String getPdescr() {
//        return pdescr;
//    }
//
//    public void setPdescr(String pdescr) {
//        this.pdescr = pdescr;
//    }
//
//    public String getpTime() {
//        return pTime;
//    }
//
//    public void setpTime(String pTime) {
//        this.pTime = pTime;
//    }
//
//    public String getUid() {
//        return uid;
//    }
//
//    public void setUid(String uid) {
//        this.uid = uid;
//    }
//
//    public String getuEmail() {
//        return uEmail;
//    }
//
//    public void setuEmail(String uEmail) {
//        this.uEmail = uEmail;
//    }
//
//    public String getuDp() {
//        return uDp;
//    }
//
//    public void setuDp(String uDp) {
//        this.uDp = uDp;
//    }
//
//    public String getuName() {
//        return uName;
//    }
//
//    public void setuName(String uName) {
//        this.uName = uName;
//    }
//}


public class ModelPost {
    String pId, pTitle, pImage, pVideo, pDescription, pTime, uid, uEmail, uDp, uName;

    public ModelPost() {
        // Default constructor required for Firebase
    }

    public ModelPost(String pId, String pTitle, String pImage, String pVideo, String pDescription, String pTime, String uid, String uEmail, String uDp, String uName) {
        this.pId = pId;
        this.pTitle = pTitle;
        this.pImage = pImage;
        this.pVideo = pVideo; // Add the pVideo field
        this.pDescription = pDescription;
        this.pTime = pTime;
        this.uid = uid;
        this.uEmail = uEmail;
        this.uDp = uDp;
        this.uName = uName;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getpTitle() {
        return pTitle;
    }

    public void setpTitle(String pTitle) {
        this.pTitle = pTitle;
    }

    public String getpImage() {
        return pImage;
    }

    public void setpImage(String pImage) {
        this.pImage = pImage;
    }

    public String getpVideo() {
        return pVideo;
    }

    public void setpVideo(String pVideo) {
        this.pVideo = pVideo;
    }

    public String getpTime() {
        return pTime;
    }

    public void setpTime(String pTime) {
        this.pTime = pTime;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getuEmail() {
        return uEmail;
    }

    public void setuEmail(String uEmail) {
        this.uEmail = uEmail;
    }

    public String getuDp() {
        return uDp;
    }

    public void setuDp(String uDp) {
        this.uDp = uDp;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }
    public String getpDescription() {
        return pDescription;
    }

    public void setpDescription(String pDescription) {
        this.pDescription = pDescription;
    }

}

