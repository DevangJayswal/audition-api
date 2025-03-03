package com.audition.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
public class AuditionPost {

    private int userId;
    private int id;
    private String title;
    private String body;

    @Getter(AccessLevel.NONE)
    // Disable Lombok's getter for comments - tells Lombok not to generate a getter for this field.
    @Setter(AccessLevel.NONE)
    // Disable Lombok's setter for comments - tells Lombok not to generate a setter for this field.
    private List<Comment> comments; // Added comments field

    // Custom getter for comments - manually implemented a getComments() method that returns an unmodifiable view of the list if it's not null.
    // The custom getter ensures that external code cannot modify the internal representation of comments directly.
    public List<Comment> getComments() {
        return comments == null ? null : Collections.unmodifiableList(comments);
    }

    // Custom setter for comments - manually implemented a setComments() method that creates a new ArrayList from the input list if it's not null.
    // Creates a defensive copy of the input list in the setter, preventing external changes from affecting the internal state.
    public void setComments(List<Comment> comments) {
        this.comments = comments == null ? null : new ArrayList<>(comments);
    }
}
