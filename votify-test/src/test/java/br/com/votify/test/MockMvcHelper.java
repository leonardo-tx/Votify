package br.com.votify.test;

import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import br.com.votify.dto.users.UserLoginDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class MockMvcHelper {
    public static Cookie[] login(
            MockMvc mockMvc,
            ObjectMapper objectMapper,
            String email,
            String password
    ) throws Exception {
        UserLoginDTO userLoginDTO = new UserLoginDTO(email, password);
        MvcResult mvcResult = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userLoginDTO))
        ).andReturn();
        
        return mvcResult.getResponse().getCookies();
    }

    public static ResultActions testSuccessfulResponse(
            ResultActions resultActions,
            HttpStatusCode httpStatusCode
    ) throws Exception {
        resultActions.andExpect(MockMvcResultMatchers.status().is(httpStatusCode.value()))
                .andDo(print())
                .andExpect(jsonPath("success", is(true)))
                .andExpect(jsonPath("errorCode", is(nullValue())))
                .andExpect(jsonPath("errorMessage", is(nullValue())));
        return resultActions;
    }

    public static ResultActions testUnsuccessfulResponse(
            ResultActions resultActions,
            VotifyErrorCode votifyErrorCode
    ) throws Exception {
        VotifyException votifyException = new VotifyException(votifyErrorCode);
        resultActions.andExpect(MockMvcResultMatchers.status().is(votifyErrorCode.getHttpStatusCode().value()))
                .andDo(print())
                .andExpect(jsonPath("data", is(nullValue())))
                .andExpect(jsonPath("success", is(false)))
                .andExpect(jsonPath("errorCode", is(votifyErrorCode.getMessageKey())))
                .andExpect(jsonPath("errorMessage", is(votifyException.getMessage())));
        return resultActions;
    }
}
