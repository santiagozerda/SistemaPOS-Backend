package com.application.supermercado_app.Pago.Controller;

import com.application.supermercado_app.Pago.Service.IMercadoPagoService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webhook/mercadopago")
@RequiredArgsConstructor
public class MercadoPagoWebhookController {

    @Autowired
    private IMercadoPagoService service;

    @PostMapping
    public ResponseEntity<Void> recibirWebhook(@RequestBody String body) {

        try {

            ObjectMapper mapper
                    = new ObjectMapper();

            JsonNode json
                    = mapper.readTree(body);

            String topic
                    = json.path("topic").asText();

            if ("payment".equals(topic)) {

                Long paymentId
                        = Long.valueOf(
                                json.path("resource")
                                        .asText()
                        );

                service.procesarWebhook(
                        paymentId
                );

                return ResponseEntity.ok().build();
            }

            String action
                    = json.path("action").asText();

            if (action.startsWith("payment.")) {

                Long paymentId
                        = json.path("data")
                                .path("id")
                                .asLong();

                service.procesarWebhook(
                        paymentId
                );
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return ResponseEntity.ok().build();
    }
}
