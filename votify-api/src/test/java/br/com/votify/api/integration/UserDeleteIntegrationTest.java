package br.com.votify.api.integration;

import br.com.votify.core.domain.entities.tokens.RefreshToken;
import br.com.votify.core.domain.entities.users.CommonUser;
import br.com.votify.core.domain.entities.users.User;
import br.com.votify.core.repository.RefreshTokenRepository;
import br.com.votify.core.repository.UserRepository;
import br.com.votify.core.service.TokenService;
import br.com.votify.core.utils.exceptions.VotifyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserDeleteIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private TokenService tokenService;

    private User testUser;
    private RefreshToken testRefreshToken;
    private String accessToken;

    @BeforeEach
    void setUp() throws VotifyException {
        // Criar usuário de teste
        testUser = new CommonUser(null, "test-user", "Test User", "test@example.com", "password123");
        testUser = userRepository.save(testUser);

        // Criar token de refresh
        testRefreshToken = new RefreshToken("token123", new Date(), testUser);
        testRefreshToken = refreshTokenRepository.save(testRefreshToken);

        // Criar token de acesso
        accessToken = tokenService.createAccessToken(testRefreshToken);
    }

    @Test
    void deleteAccount_WhenAuthenticated_ShouldDeleteUserAndTokens() throws Exception {
        // Act
        ResultActions result = mockMvc.perform(delete("/users/me")
            .cookie(new javax.servlet.http.Cookie("access_token", accessToken)));

        // Assert
        result.andExpect(status().isNoContent());

        // Verificar se o usuário foi deletado
        assertFalse(userRepository.existsById(testUser.getId()));

        // Verificar se os tokens foram deletados
        assertTrue(refreshTokenRepository.findAllByUser(testUser).isEmpty());

        // Verificar se os cookies foram limpos
        result.andExpect(cookie().exists("access_token"))
              .andExpect(cookie().maxAge("access_token", 0))
              .andExpect(cookie().exists("refresh_token"))
              .andExpect(cookie().maxAge("refresh_token", 0));
    }

    @Test
    void deleteAccount_WhenNotAuthenticated_ShouldReturnUnauthorized() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/users/me"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.errorCode").value("common.unauthorized"));
    }

    @Test
    void deleteAccount_WithInvalidToken_ShouldReturnUnauthorized() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/users/me")
            .cookie(new javax.servlet.http.Cookie("access_token", "invalid-token")))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.errorCode").value("access.token.invalid"));
    }
} 