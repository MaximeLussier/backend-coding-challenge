package com.maximelussier;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SuggestionControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void noParamSuggestionsShouldEmptyMessage() throws Exception {

        this.mockMvc.perform(get("/suggestions"))
        		.andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.suggestions").isEmpty());
    }

    @Test
    public void paramSuggestionsShouldReturnCities() throws Exception {

        this.mockMvc.perform(get("/suggestions").param("q", "Lond"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.suggestions").isNotEmpty())
                .andExpect(jsonPath("$.suggestions[0].name").value(org.hamcrest.Matchers.containsString("London, ON, Canada")));
    }

    // Here would be more tests for corner cases and invalid input.
}
