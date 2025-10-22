package com.realityvote.model;

import jakarta.persistence.*;

@Entity
public class WhatsNew {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    @Column(length = 2000)
    private String description;

    private String mediaUrl; // Image or video URL
    private String type; // "image" or "video"

    public WhatsNew() {}

    public WhatsNew(String title, String description, String mediaUrl, String type) {
        this.title = title;
        this.description = description;
        this.mediaUrl = mediaUrl;
        this.type = type;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getMediaUrl() { return mediaUrl; }
    public void setMediaUrl(String mediaUrl) { this.mediaUrl = mediaUrl; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
