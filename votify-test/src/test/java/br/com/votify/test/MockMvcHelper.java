package br.com.votify.test;

import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import br.com.votify.dto.users.UserLoginDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import com.jayway.jsonpath.JsonPath;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RequiredArgsConstructor
public class MockMvcHelper {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    public Cookie[] login(
            String email,
            String password
    ) throws Exception {
        UserLoginDTO userLoginDTO = new UserLoginDTO(email, password);
        MvcResult mvcResult = testSuccessfulResponse(
                mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userLoginDTO))
                ),
                HttpStatus.OK
        ).andReturn();
        return mvcResult.getResponse().getCookies();
    }

    public ResultActions testSuccessfulResponse(
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

    public ResultActions testUnsuccessfulResponse(
            ResultActions resultActions,
            VotifyErrorCode votifyErrorCode,
            Object... messageArguments
    ) throws Exception {
        VotifyException votifyException = new VotifyException(votifyErrorCode);
        resultActions.andExpect(MockMvcResultMatchers.status().is(votifyErrorCode.getHttpStatusCode().value()))
                .andDo(print())
                .andExpect(jsonPath("data", is(nullValue())))
                .andExpect(jsonPath("success", is(false)))
                .andExpect(jsonPath("errorCode", is(votifyErrorCode.getMessageKey())))
                .andExpect(jsonPath("errorMessage", is(String.format(votifyException.getMessage(), messageArguments))));
        return resultActions;
    }

    public void loginExpectingError(
            String email,
            String password,
            VotifyErrorCode expectedErrorCode
    ) throws Exception {
        UserLoginDTO userLoginDTO = new UserLoginDTO(email, password);
        ResultActions resultActions = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userLoginDTO))
        );
        testUnsuccessfulResponse(resultActions, expectedErrorCode);
    }

    public int extractId(ResultActions resultActions, String jsonPath) throws Exception {
        String response = resultActions.andReturn().getResponse().getContentAsString();
        return JsonPath.parse(response).read(jsonPath, Integer.class);
    }
}
