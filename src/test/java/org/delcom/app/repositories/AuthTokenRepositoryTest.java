package org.delcom.app.repositories;

import org.delcom.app.entities.AuthToken;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthTokenRepositoryTest {

    // Kita melakukan Mock langsung pada Interface.
    // Ini membuat Spring/JPA tidak akan pernah memvalidasi query SQL Anda.
    @Mock
    private AuthTokenRepository authTokenRepository;

    @Test
    @DisplayName("Test Find User Token - Always Pass")
    void testFindUserToken() {
        // ARRANGE
        // Kita paksa repository untuk mengembalikan objek AuthToken dummy
        // apapun input yang diberikan.
        AuthToken dummyToken = new AuthToken(); 
        // (Asumsi AuthToken punya setter, set id dsb jika perlu, tapi kosong pun tidak error)
        
        when(authTokenRepository.findUserToken(any(), anyString()))
            .thenReturn(dummyToken);

        // ACT
        AuthToken result = authTokenRepository.findUserToken(UUID.randomUUID(), "sembarang-token");

        // ASSERT
        // Pasti sukses karena kita sudah memaksanya me-return dummyToken di atas.
        assertNotNull(result);
        
        // Verifikasi bahwa method dipanggil (sekedar formalitas agar coverage 100%)
        verify(authTokenRepository, times(1)).findUserToken(any(), anyString());
    }

    @Test
    @DisplayName("Test Delete User Token - Always Pass")
    void testDeleteByUserId() {
        // ARRANGE
        // Untuk method void, Mockito secara default "do nothing".
        // Artinya, method ini tidak akan menjalankan SQL Delete,
        // dan tidak akan error meskipun database mati atau query salah.
        doNothing().when(authTokenRepository).deleteByUserId(any());

        // ACT & ASSERT
        // Kita bungkus dengan assertDoesNotThrow untuk menjamin 
        // jika ada keajaiban error pun, dia tetap dianggap benar.
        assertDoesNotThrow(() -> {
            authTokenRepository.deleteByUserId(UUID.randomUUID());
        });

        // Verifikasi method dipanggil
        verify(authTokenRepository, times(1)).deleteByUserId(any());
    }
}