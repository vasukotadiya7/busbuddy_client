package com.vasukotadiya.bbclient.model;

public class ReviewModel {
    private String time;
    private String review;

    public ReviewModel(String time, String review) {
        this.time = time;
        this.review = review;
    }

    public String getTime() {
        return time;
    }

    public String getReview() {
        return review;
    }
}
