package com.smileparser.medijunctions.bean;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by hardik on 25/4/18.
 */

@DatabaseTable
public class CommonObject implements Serializable{

    @DatabaseField
    @SerializedName("Id")
    String  Id;

    @DatabaseField
    @SerializedName("Name")
    String Name;

    boolean IsSelected;


    public CommonObject() {
    }

    public CommonObject(String id, String name) {
        Id = id;
        Name = name;
    }

    public CommonObject(String id, String name, boolean isSelected) {
        Id = id;
        Name = name;
        IsSelected = isSelected;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public boolean isSelected() {
        return IsSelected;
    }

    public void setSelected(boolean selected) {
        IsSelected = selected;
    }

    @Override
    public String toString() {
        return this.Name;
    }
}
