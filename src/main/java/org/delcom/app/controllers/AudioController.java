package org.delcom.app.controllers;

import org.delcom.app.services.AudioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping("/api/audio")
public class AudioController {
    
    @Autowired
    private AudioService audioService;
    
    @GetMapping("/list")
    public ResponseEntity<?> getAudioFiles() {
        return ResponseEntity.ok(audioService.getAvailableSounds());
    }
    
    @GetMapping("/play/{filename:.+}")
    public ResponseEntity<Resource> playAudio(@PathVariable String filename, 
                                             HttpServletRequest request) {
        Resource resource = audioService.loadAudioFile(filename);
        
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            // Default to MP3 if cannot determine
            contentType = "audio/mpeg";
        }
        
        if (contentType == null) {
            contentType = "audio/mpeg";
        }
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                        "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
    
    @GetMapping("/test/{filename:.+}")
    public ResponseEntity<Resource> testAudio(@PathVariable String filename, 
                                             HttpServletRequest request) {
        return playAudio(filename, request);
    }
}