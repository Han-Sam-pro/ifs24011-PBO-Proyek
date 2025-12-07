package org.delcom.app.services;

import org.delcom.app.entities.AudioFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class AudioService {
    
    private final Path audioStorageLocation;
    
    public AudioService() {
        this.audioStorageLocation = Paths.get("src/main/resources/static/sound")
                .toAbsolutePath().normalize();
        
        try {
            Files.createDirectories(this.audioStorageLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not create audio directory", e);
        }
    }
    
    public List<AudioFile> getAvailableSounds() {
        List<AudioFile> audioFiles = new ArrayList<>();
        
        // Default sounds
        audioFiles.add(new AudioFile("Yo-Phone-Long.mp3", "/sound/Yo-Phone-Long.mp3", "Yo Phone Long"));
        audioFiles.add(new AudioFile("alarm.mp3", "/sound/alarm.mp3", "Alarm Standar"));
        audioFiles.add(new AudioFile("beep.mp3", "/sound/beep.mp3", "Beep"));
        audioFiles.add(new AudioFile("bell.mp3", "/sound/bell.mp3", "Bell"));
        
        return audioFiles;
    }
    
    public Resource loadAudioFile(String filename) {
        try {
            Path filePath = this.audioStorageLocation.resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("File not found: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("File not found: " + filename, e);
        }
    }
    
    public boolean audioFileExists(String filename) {
        Path filePath = this.audioStorageLocation.resolve(filename).normalize();
        return Files.exists(filePath) && Files.isReadable(filePath);
    }
}