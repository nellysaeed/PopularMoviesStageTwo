package com.nadeveloper.popularmoviesfinal.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ApiResponse<T> {
    @SerializedName("results")
    public List<T> results;
}