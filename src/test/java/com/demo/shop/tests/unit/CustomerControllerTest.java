package com.demo.shop.tests.unit;

import com.demo.shop.controller.CustomerController;
import com.demo.shop.dto.EmailValidatorDto;
import com.demo.shop.exception.ResourceNotFoundException;
import com.demo.shop.model.Customer;
import com.demo.shop.service.CustomerService;
import com.demo.shop.service.EmailValidatorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(CustomerController.class)
public class CustomerControllerTest {

    /* @WebMvcTest is limited to a single controller and is used in combination
     with @MockBean to provide mock implementations for required collaborators.

     @WebMvcTest also auto-configures MockMvc. Mock MVC offers a powerful way to
     quickly test MVC controllers without needing to start a full HTTP server.

     Auto-configure MockMvc in a non-@WebMvcTest (such as @SpringBootTest)
     by annotating it with @AutoConfigureMockMvc.*/

    @Autowired
    MockMvc mockMvc;
    @MockBean
    CustomerService customerService;
    @MockBean
    EmailValidatorService emailValidatorService;


    Customer customer = Customer.builder()
            .id(1L)
            .email("testname@email.com")
            .address("City")
            .fullName("Test Name")
            .build();

    @Test
    public void shouldReturnCustomerDetails() throws Exception {
        when(customerService.get(anyLong())).thenReturn(customer);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/customers/1");

        mockMvc.perform(requestBuilder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("testname@email.com"))
                .andExpect(jsonPath("$.address").value("City"))
                .andExpect(jsonPath("$.fullName").value("Test Name"));

        requestBuilder = MockMvcRequestBuilders
                .get("/api/customers/abc");

        mockMvc.perform(requestBuilder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest());
    }


    @Test
    public void shouldReturnBadRequest() throws Exception {

        when(customerService.get(anyLong())).thenThrow(ResourceNotFoundException.class);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/customers/1");

        mockMvc.perform(requestBuilder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldCreateNewCustomer() throws Exception {
        EmailValidatorDto emailValidatorDto = EmailValidatorDto.builder()
                .format(true)
                .domain("example.com")
                .disposable(false)
                .dns(true)
                .build();

        when(emailValidatorService.request(customer.getEmail())).thenReturn(emailValidatorDto);
        when(customerService.create(customer)).thenReturn(customer);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/customers")
                .content(new ObjectMapper().writeValueAsString(customer))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("testname@email.com"))
                .andExpect(jsonPath("$.address").value("City"))
                .andExpect(jsonPath("$.fullName").value("Test Name"));
    }

}
