package integration.tests;

import com.tosDev.tr.spring.rest.controller.DebitTrController;
import com.tosDev.tr.spring.service.DebitTrService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = DebitTrController.class)
@AutoConfigureMockMvc
class DebitTrControllerIT {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    DebitTrService debitTrService;

    /**
     * Сервер отвечает 4xx ошибкой на пустой запрос
     */
    @Test
    void receiveTrCheckResponse() throws Exception {
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.post("/api/v1/debit-tr/receive-tr");
        mockMvc.perform(builder)
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$").doesNotExist());
    }


}