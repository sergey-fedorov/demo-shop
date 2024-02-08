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
public class CustomerControllerTests {

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

    /* getCustomer */

    @Test
    public void shouldReturnCustomerDetails() throws Exception {
        when(customerService.get(anyLong())).thenReturn(customer);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/customers/1");

        mockMvc.perform(requestBuilder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(customer.getId()))
                .andExpect(jsonPath("$.email").value(customer.getEmail()))
                .andExpect(jsonPath("$.address").value(customer.getAddress()))
                .andExpect(jsonPath("$.fullName").value(customer.getFullName()));
    }

    @Test
    public void shouldTrowBadRequest_WrongPathParameterType() throws Exception {
        when(customerService.get(anyLong())).thenReturn(customer);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/customers/abc");

        mockMvc.perform(requestBuilder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("Invalid type of parameter customerId"));
    }

    @Test
    public void shouldTrowBadRequest_PathParameterLessOrZero() throws Exception {
        when(customerService.get(anyLong())).thenReturn(customer);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/customers/0");

        mockMvc.perform(requestBuilder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("must be greater than or equal to 1"));
    }


    @Test
    public void shouldTrowResourceNotFound_MissingCustomer() throws Exception {
        when(customerService.get(anyLong())).thenThrow(ResourceNotFoundException.class);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/customers/1");

        mockMvc.perform(requestBuilder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound());
    }

    /* createCustomer */

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
                .andExpect(jsonPath("$.id").value(customer.getId()))
                .andExpect(jsonPath("$.email").value(customer.getEmail()))
                .andExpect(jsonPath("$.address").value(customer.getAddress()))
                .andExpect(jsonPath("$.fullName").value(customer.getFullName()));
    }

    @Test
    public void shouldTrowBadRequest_EmailHasWrongFormat() throws Exception {
        EmailValidatorDto emailValidatorDto = EmailValidatorDto.builder()
                .format(false)
                .build();

        when(emailValidatorService.request(customer.getEmail())).thenReturn(emailValidatorDto);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/customers")
                .content(new ObjectMapper().writeValueAsString(customer))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.errorMessage").value("Email format is not valid"));
    }

    @Test
    public void shouldTrowBadRequest_EmailIsDisposable() throws Exception {
        EmailValidatorDto emailValidatorDto = EmailValidatorDto.builder()
                .format(true)
                .domain("example.com")
                .disposable(true)
                .dns(true)
                .build();

        when(emailValidatorService.request(customer.getEmail())).thenReturn(emailValidatorDto);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/customers")
                .content(new ObjectMapper().writeValueAsString(customer))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.errorMessage").value("Email address is disposable"));
    }

    @Test
    public void shouldTrowBadRequest_EmailIsInvalid() throws Exception {
        EmailValidatorDto emailValidatorDto = EmailValidatorDto.builder()
                .format(true)
                .domain("example.com")
                .disposable(false)
                .dns(false)
                .build();

        when(emailValidatorService.request(customer.getEmail())).thenReturn(emailValidatorDto);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/customers")
                .content(new ObjectMapper().writeValueAsString(customer))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.errorMessage").value("Email address is not valid"));
    }

}
