package br.com.votify.test;

import br.com.votify.core.properties.user.UserProperties;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import com.jayway.jsonpath.JsonPath;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RequiredArgsConstructor
public class MockMvcHelper {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final UserProperties userProperties;

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

    public ResultActions validateCookie(ResultActions resultActions, String name, String value, int maxAge) throws Exception {
        resultActions.andExpect(cookie().exists(name))
                .andExpect(cookie().value(name, value))
                .andExpect(cookie().maxAge(name, maxAge))
                .andExpect(cookie().secure(name, userProperties.isCookieSecure()))
                .andExpect(cookie().httpOnly(name, userProperties.isCookieHttpOnly()))
                .andExpect(cookie().path(name, "/"));
        return resultActions;
    }

    public int extractId(ResultActions resultActions, String jsonPath) throws Exception {
        String response = resultActions.andReturn().getResponse().getContentAsString();
        return JsonPath.parse(response).read(jsonPath, Integer.class);
    }
}
