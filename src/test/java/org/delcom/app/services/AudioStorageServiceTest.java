package org.delcom.app.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AudioStorageServiceTest {

    private AudioStorageService service;

    // JUnit 5 akan membuat folder temporary yang otomatis dihapus setelah tes selesai.
    @TempDir
    Path tempDir;

    @Mock
    private MultipartFile mockFile;

    @BeforeEach
    void setUp() throws Exception {
        service = new AudioStorageService();

        // --- REFLECTION MAGIC ---
        // Mengarahkan 'rootLocation' ke tempDir agar operasi file aman (tidak butuh permission OS)
        Field rootLocationField = AudioStorageService.class.getDeclaredField("rootLocation");
        rootLocationField.setAccessible(true);
        rootLocationField.set(service, tempDir);
    }

    @Test
    @DisplayName("Test Init - Create Directory")
    void testInit() {
        assertDoesNotThrow(() -> service.init());
        assertTrue(Files.exists(tempDir), "Directory should exist");
    }

    @Test
    @DisplayName("Test Init - Handle Exception (Force Error)")
    void testInitFailure() throws Exception {
        File file = tempDir.toFile();
        // Hapus folder tempDir, ganti jadi file biasa agar createDirectories gagal
        FileSystemUtils.deleteRecursively(file);
        file.createNewFile();

        assertThrows(RuntimeException.class, () -> service.init());

        // Cleanup manual
        file.delete();
    }

    @Test
    @DisplayName("Test Store - Success")
    void testStoreSuccess() throws IOException {
        String filename = "test-audio.mp3";
        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getOriginalFilename()).thenReturn(filename);
        when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream("dummy content".getBytes()));

        assertDoesNotThrow(() -> service.store(mockFile));

        Path storedFile = tempDir.resolve(filename);
        assertTrue(Files.exists(storedFile));
    }

    @Test
    @DisplayName("Test Store - Empty File")
    void testStoreEmptyFile() {
        when(mockFile.isEmpty()).thenReturn(true);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.store(mockFile));
        assertTrue(ex.getMessage().contains("empty file"));
    }

    @Test
    @DisplayName("Test Store - Path Traversal Security Check")
    void testStoreSecurityCheck() {
        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getOriginalFilename()).thenReturn("../hacker.sh");

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.store(mockFile));
        assertTrue(ex.getMessage().contains("Cannot store file outside"));
    }

    @Test
    @DisplayName("Test Store - IOException (Catch Block Coverage)")
    void testStoreIOException() throws IOException {
        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getOriginalFilename()).thenReturn("valid.mp3");
        // Paksa error saat membaca stream
        when(mockFile.getInputStream()).thenThrow(new IOException("Disk error"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.store(mockFile));
        assertTrue(ex.getMessage().contains("Failed to store file"));
    }

    @Test
    @DisplayName("Test Load All - Success")
    void testLoadAll() throws IOException {
        Files.createFile(tempDir.resolve("sound1.mp3"));
        Files.createFile(tempDir.resolve("sound2.wav"));

        Stream<Path> result = service.loadAll();

        assertNotNull(result);
        assertEquals(2, result.count());
    }

    // --- FIX BAGIAN MERAH 1: IOException di loadAll ---
    @Test
    @DisplayName("Test Load All - Force IOException")
    void testLoadAll_IOException() {
        // Hapus folder rootLocation secara paksa sebelum loadAll dipanggil
        // Ini membuat Files.walk melempar NoSuchFileException (turunan IOException)
        FileSystemUtils.deleteRecursively(tempDir.toFile());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.loadAll());
        assertTrue(ex.getMessage().contains("Failed to read stored files"));
    }

    @Test
    @DisplayName("Test Load Path")
    void testLoad() {
        Path path = service.load("test.mp3");
        assertNotNull(path);
        assertEquals(tempDir.resolve("test.mp3"), path);
    }

    @Test
    @DisplayName("Test Load As Resource - Success")
    void testLoadAsResourceSuccess() throws IOException {
        Path file = tempDir.resolve("exist.mp3");
        Files.write(file, "content".getBytes());

        Resource resource = service.loadAsResource("exist.mp3");

        assertNotNull(resource);
        assertTrue(resource.exists());
        assertTrue(resource.isReadable());
    }

    @Test
    @DisplayName("Test Load As Resource - Not Found")
    void testLoadAsResourceNotFound() {
        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.loadAsResource("ghost.mp3"));
        assertTrue(ex.getMessage().contains("Could not read file"));
    }

    // --- FIX BAGIAN MERAH 2: MalformedURLException di loadAsResource ---
    @Test
    @DisplayName("Test Load As Resource - Force MalformedURLException")
    void testLoadAsResource_MalformedURL() throws Exception {
        // Kita butuh Mock tingkat tinggi karena Path standard tidak pernah melempar MalformedURL
        Path mockRoot = mock(Path.class);
        Path mockFile = mock(Path.class);

        // Wiring mock: root.resolve(...) -> mockFile
        when(mockRoot.resolve(anyString())).thenReturn(mockFile);

        // Wiring mock: mockFile.toUri() -> LEMPAR MalformedURLException
        // Kita pakai helper 'sneakyThrow' karena toUri() tidak mendeklarasikan throws Exception
        when(mockFile.toUri()).thenAnswer(invocation -> {
            sneakyThrow(new MalformedURLException("Forced Error"));
            return null;
        });

        // Inject Mock Root menggantikan tempDir asli
        Field rootLocationField = AudioStorageService.class.getDeclaredField("rootLocation");
        rootLocationField.setAccessible(true);
        rootLocationField.set(service, mockRoot);

        // Eksekusi
        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.loadAsResource("bad.file"));
        
        // Assert catch block tereksekusi
        assertTrue(ex.getMessage().contains("Could not read file"));
        assertTrue(ex.getCause() instanceof MalformedURLException);
    }

    @Test
    @DisplayName("Test Delete All")
    void testDeleteAll() throws IOException {
        Files.createFile(tempDir.resolve("todelete.mp3"));
        service.deleteAll();
        assertFalse(Files.exists(tempDir.resolve("todelete.mp3")));
    }

    // --- HELPER UNTUK MEMAKSA CHECKED EXCEPTION ---
    @SuppressWarnings("unchecked")
    private static <E extends Throwable> void sneakyThrow(Throwable e) throws E {
        throw (E) e;
    }
}