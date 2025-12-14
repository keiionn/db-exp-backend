package com.dbexp.db_experiment.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class EditCommentRequest {

    @NotBlank(message = "Comment content is required")
    @Size(max = 1000, message = "Comment content must not exceed 1000 characters")
    private String content;

    // Constructors
    public EditCommentRequest(String content) {
        this.content = content;
    }

    // Getters and Setters
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}