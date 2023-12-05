package com.demo.shop.delivery.service;

import lombok.Data;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/delivery")
public class DeliveryController {

    /* Delivery service uses it when the order is shipped by a courier */
    @PostMapping("/complete")
    @ResponseStatus(HttpStatus.OK)
    public void completeDelivery(@RequestBody OrderRequest orderRequest){
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<OrderRequest> request = new HttpEntity<>(orderRequest, headers);
        restTemplate.exchange(
                "http://localhost:8081/api/orders/status",
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<>() {}
        );

        /* Some logic based on response from api-orders: status code, error message */

    }

    /* Delivery service uses it to get the list of orders for shipping */
    @GetMapping("/list")
    @ResponseStatus(HttpStatus.OK)
    public void getDeliveryList(){
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("status", "PAYMENT_SUCCEEDED");
        RestTemplate restTemplate = new RestTemplate();

        restTemplate.exchange(
                "http://localhost:8081/api/orders",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {},
                queryParams
        );

        /* Some logic based on response from api-orders:

        [
            {
                "id": 1,
                "dateCreated": "2023-12-04 20:53:00",
                "status": "DELIVERED",
                "transactionId": 900780,
                "customerId": 1,
                "totalOrderPrice": 2.0,
                "numberOfProducts": 1,
                "orderItems": [
                    {
                        "quantity": 2,
                        "totalPrice": 2.0,
                        "productId": 1
                    }
                ]
            }
        ]
        */
    }


    @Data
    public static class OrderRequest {
        private long orderId;
    }
}
