package com.onlinebetting;


import com.onlinebetting.service.SessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
public class StakeControllerTest {

    @Autowired
    private SessionService sessionService;
    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;
    private String sessionKey;

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        sessionKey = sessionService.getSession(1);
    }

    private final long betOfferId = 1L;

    @Test
    public void offerStakeTest() throws Exception {
        String url = "/".concat(String.valueOf(betOfferId)).concat("/stake");
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(url)
                .accept(MediaType.APPLICATION_JSON)
                .param("sessionKey", sessionKey)
                .content("50")
                .contentType(MediaType.APPLICATION_JSON);
        ResultActions result = mockMvc.perform(requestBuilder);
        result.andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }


    @Test
    public void highStakesTest() throws Exception {
        String url = "/".concat(String.valueOf(betOfferId)).concat("/highStakes");
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(url)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);
        ResultActions result = mockMvc.perform(requestBuilder);
        result.andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }


}
