package org.delcom.app.entities;

public class AudioFile {
    private String name;
    private String path;
    private String displayName;
    
    public AudioFile() {}
    
    public AudioFile(String name, String path, String displayName) {
        this.name = name;
        this.path = path;
        this.displayName = displayName;
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getPath() {
        return path;
    }
    
    public void setPath(String path) {
        this.path = path;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}