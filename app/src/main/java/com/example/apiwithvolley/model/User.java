
package com.example.apiwithvolley.model;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class User {

    private String country;
    @SerializedName("email_id")
    private String emailId;

    @SerializedName("first_name")
    private String firstName;

    private String gender;

    private Long id;

    @SerializedName("last_name")
    private String lastName;

    @SerializedName("profile_img_compress")
    private String profileImgCompress;

    @SerializedName("profile_img_original")
    private String profileImgOriginal;

    @SerializedName("profile_img_thumbnail")
    private String profileImgThumbnail;

    @SerializedName("social_uid")
    private String socialUid;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getProfileImgCompress() {
        return profileImgCompress;
    }

    public void setProfileImgCompress(String profileImgCompress) {
        this.profileImgCompress = profileImgCompress;
    }

    public String getProfileImgOriginal() {
        return profileImgOriginal;
    }

    public void setProfileImgOriginal(String profileImgOriginal) {
        this.profileImgOriginal = profileImgOriginal;
    }

    public String getProfileImgThumbnail() {
        return profileImgThumbnail;
    }

    public void setProfileImgThumbnail(String profileImgThumbnail) {
        this.profileImgThumbnail = profileImgThumbnail;
    }

    public String getSocialUid() {
        return socialUid;
    }

    public void setSocialUid(String socialUid) {
        this.socialUid = socialUid;
    }

}
