package com.example.reservebite.DTO;

public class MessageDto {
    private Long recipientId;
    private String content;

    // Getters and setters

    public Long getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(Long recipientId) {
        this.recipientId = recipientId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}