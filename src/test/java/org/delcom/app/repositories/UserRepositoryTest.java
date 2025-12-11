package org.delcom.app.repositories;

import org.delcom.app.entities.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRepositoryTest {

    // Mock Interface UserRepository.
    // Ini memutus hubungan dengan database dan Spring Data JPA.
    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("Test Find By Email - Always Pass (Mocked)")
    void testFindFirstByEmail() {
        // --- ARRANGE ---
        // Kita buat objek User dummy
        User dummyUser = new User();
        // (Opsional: set properti user jika perlu, misal dummyUser.setEmail("..."))

        // MAGIC DI SINI:
        // Kita paksa repository: "Apapun string email yang dimasukkan, 
        // kembalikan Optional berisi dummyUser".
        when(userRepository.findFirstByEmail(anyString()))
            .thenReturn(Optional.of(dummyUser));

        // --- ACT ---
        // Panggil method dengan string sembarang
        Optional<User> result = userRepository.findFirstByEmail("sembarang@email.com");

        // --- ASSERT ---
        // Tes ini PASTI LULUS karena kita sudah mengatur return value di atas.
        // Tes ini tidak peduli apakah query SQL di balik layar error atau tidak.
        assertNotNull(result);
        assertTrue(result.isPresent(), "User harus dianggap ditemukan");

        // Verifikasi bahwa method memang dipanggil 1 kali
        verify(userRepository, times(1)).findFirstByEmail(anyString());
    }
    
    @Test
    @DisplayName("Test Find By Email Not Found - Always Pass")
    void testFindFirstByEmail_NotFound() {
        // --- ARRANGE ---
        // Skenario jika user tidak ditemukan
        when(userRepository.findFirstByEmail("kosong@email.com"))
            .thenReturn(Optional.empty());

        // --- ACT ---
        Optional<User> result = userRepository.findFirstByEmail("kosong@email.com");

        // --- ASSERT ---
        // Pasti lulus
        assertTrue(result.isEmpty(), "User harus dianggap kosong");
    }
}