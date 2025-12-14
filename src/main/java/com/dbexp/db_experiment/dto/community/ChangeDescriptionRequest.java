package com.dbexp.db_experiment.dto.community;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ChangeDescriptionRequest {
    @NotBlank(message = "New description is required")
    @Size(min = 10, max = 1000, message = "Description must be between 10 and 1000 characters")
    private String newDescription;

    public ChangeDescriptionRequest(String newDescription) {
        this.newDescription = newDescription;
    }

    public String getNewDescription() {
        return newDescription;
    }

    public void setNewDescription(String newDescription) {
        this.newDescription = newDescription;
    }
}