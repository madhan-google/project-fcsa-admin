package com.codekiller.fcsaadmin.Datas;

import androidx.annotation.NonNull;

public class UserField {
    String Name, Father_name, Mother_name, dob, Your_class, College_name, Board, Town, District, State, Opting_course, Mail_id, test;
    String ph_no, date_time;
    String pushKey, favorite_pushkey;
    boolean isSeen, isFavorite;

    public UserField() {
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public String getFavoritePushkey() {
        return favorite_pushkey;
    }

    public void setFavoritePushkey(String favorite_pushkey) {
        this.favorite_pushkey = favorite_pushkey;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public String getPushKey() {
        return pushKey;
    }

    public void setPushKey(String pushKey) {
        this.pushKey = pushKey;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getFather_name() {
        return Father_name;
    }

    public void setFather_name(String father_name) {
        Father_name = father_name;
    }

    public String getMother_name() {
        return Mother_name;
    }

    public void setMother_name(String mother_name) {
        Mother_name = mother_name;
    }

    public String getYour_class() {
        return Your_class;
    }

    public void setYour_class(String your_class) {
        Your_class = your_class;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }


    public String getCollege_name() {
        return College_name;
    }

    public void setCollege_name(String college_name) {
        College_name = college_name;
    }

    public String getBoard() {
        return Board;
    }

    public void setBoard(String board) {
        Board = board;
    }

    public String getTown() {
        return Town;
    }

    public void setTown(String town) {
        Town = town;
    }

    public String getDistrict() {
        return District;
    }

    public void setDistrict(String district) {
        District = district;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public String getOpting_course() {
        return Opting_course;
    }

    public void setOpting_course(String opting_course) {
        Opting_course = opting_course;
    }

    public String getMail_id() {
        return Mail_id;
    }

    public void setMail_id(String mail_id) {
        Mail_id = mail_id;
    }

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    public String getPh_no() {
        return ph_no;
    }

    public void setPh_no(String ph_no) {
        this.ph_no = ph_no;
    }
}
