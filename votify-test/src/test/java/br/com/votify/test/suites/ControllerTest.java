package br.com.votify.test.suites;

import br.com.votify.api.VotifyApiApplication;
import br.com.votify.api.configuration.SecurityConfig;
import br.com.votify.core.service.user.ContextService;
import br.com.votify.core.service.user.PasswordEncoderService;
import br.com.votify.core.service.user.UserService;
import br.com.votify.test.MockMvcHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;

@SpringBootTest(classes = VotifyApiApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class ControllerTest {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockitoBean
    protected UserService userService;

    @MockitoBean
    protected ContextService contextService;

    @MockitoBean
    protected SecurityConfig securityConfig;

    @MockitoBean
    protected PasswordEncoderService passwordEncoderService;

    protected MockMvcHelper mockMvcHelper;

    @BeforeEach
    void setupBeforeEach() {
        when(userService.getContext()).thenReturn(contextService);
        this.mockMvcHelper = new MockMvcHelper(mockMvc, objectMapper, userService, securityConfig);
    }
}
