package com.github.cloudyrock.mongock.integrationtests.spring5.springdata3.client;

public class ActivationModel {
    private String status;
    private String comment;

    public ActivationModel() {
    }

    public ActivationModel(String status, String comment) {
        this.status = status;
        this.comment = comment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
