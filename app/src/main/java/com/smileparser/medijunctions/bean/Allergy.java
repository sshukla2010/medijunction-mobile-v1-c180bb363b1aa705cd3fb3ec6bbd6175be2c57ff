package com.smileparser.medijunctions.bean;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by hardik on 25/4/18.
 */

@DatabaseTable
public class Allergy implements Serializable{

    @DatabaseField
    @SerializedName("Id")
    long Id;

    @DatabaseField
    @SerializedName("Name")
    String Name;

    boolean IsSelected;


    public Allergy() {
    }

    public Allergy(long id, String name) {
        Id = id;
        Name = name;
    }

    public Allergy(long id, String name, boolean isSelected) {
        Id = id;
        Name = name;
        IsSelected = isSelected;
    }

    public long getId() {
        return Id;
    }

    public void setId(long id) {
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
