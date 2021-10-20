package com.smileparser.medijunctions.bean;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

/**
 * Created by hardik on 25/4/18.
 */

public class ApiResponse {

    @SerializedName("Message")
    String Message;

    @SerializedName("Response")
    boolean Response;

    @SerializedName("Result")
    JsonElement Result;

    @SerializedName("ErrorCode")
    int ErrorCode;

    public ApiResponse() {
    }

    public ApiResponse(String message, boolean response, JsonElement result, int errorCode) {
        Message = message;
        Response = response;
        Result = result;
        ErrorCode = errorCode;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public boolean isResponse() {
        return Response;
    }

    public void setResponse(boolean response) {
        Response = response;
    }

    public JsonElement getResult() {
        return Result;
    }

    public void setResult(JsonElement result) {
        Result = result;
    }

    public int getErrorCode() {
        return ErrorCode;
    }

    public void setErrorCode(int errorCode) {
        ErrorCode = errorCode;
    }
}
