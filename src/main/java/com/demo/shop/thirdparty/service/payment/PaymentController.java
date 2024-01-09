package com.demo.shop.thirdparty.service.payment;

import com.demo.shop.exception.BadRequestException;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/payment")
@Tag(name = "api-payment", description = "Payment service")
public class PaymentController {

    @PostMapping("/proceed")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Map<String, Long>> proceed(@RequestBody PaymentRequest paymentRequest) {
        long transactionId;

        if (paymentRequest.getType().equals("card")){
            transactionId = new Random().nextLong(1, 999999);
        } else
            throw new BadRequestException("Payment failed");

        return new ResponseEntity<>(Map.of("transactionId", transactionId), HttpStatus.OK);
    }

    @Data
    public static class PaymentRequest {
        @NotNull
        private String type;
        @NotNull
        private long orderId;
    }

}
