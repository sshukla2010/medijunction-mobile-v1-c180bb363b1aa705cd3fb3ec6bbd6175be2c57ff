package com.smileparser.medijunctions.bean;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by hardik on 25/4/18.
 */

@DatabaseTable
public class DeleteSync {

    @DatabaseField(generatedId = true)
    @SerializedName("AppId")
    int AppId;

    @DatabaseField
    @SerializedName("DBId")
    String DBId;

    @DatabaseField
    @SerializedName("UserId")
    String UserId;

    @DatabaseField(canBeNull = false)
    @SerializedName("IsInserted")
    public boolean IsInserted;


    public DeleteSync() {
    }

    public DeleteSync(int appId, String DBId, String userId, boolean isInserted) {
        AppId = appId;
        this.DBId = DBId;
        UserId = userId;
        IsInserted = isInserted;
    }

    public int getAppId() {
        return AppId;
    }

    public void setAppId(int appId) {
        AppId = appId;
    }

    public String getDBId() {
        return DBId;
    }

    public void setDBId(String DBId) {
        this.DBId = DBId;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public boolean isInserted() {
        return IsInserted;
    }

    public void setInserted(boolean inserted) {
        IsInserted = inserted;
    }
}
