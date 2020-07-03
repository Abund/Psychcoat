package com.example.psychcoat.model;

public class Psychologist {


    private int userId;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String uid;
    private String onlineStatus;
    private String typingTo;
    private String imageUrl;
    private String cover;
    boolean isBlocked = false;

    public Psychologist(){

    }

    public Psychologist(int userId, String firstName, String lastName, String email, String password, String uid, String onlineStatus, String typingTo, String imageUrl, String cover, boolean isBlocked) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.uid = uid;
        this.onlineStatus = onlineStatus;
        this.typingTo = typingTo;
        this.imageUrl = imageUrl;
        this.cover = cover;
        this.isBlocked = isBlocked;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(String onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public String getTypingTo() {
        return typingTo;
    }

    public void setTypingTo(String typingTo) {
        this.typingTo = typingTo;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }
}
