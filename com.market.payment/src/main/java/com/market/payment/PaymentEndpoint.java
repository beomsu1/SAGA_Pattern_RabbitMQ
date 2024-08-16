package com.market.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Log4j2
@RequiredArgsConstructor
@Component
public class PaymentEndpoint {

    private final PaymentService paymentService;

    @RabbitListener(queues = "${message.queue.payment}")
    public void receiveMessage(DeliveryMessage deliveryMessage){

        log.info("Receive Message: {}", deliveryMessage);
        paymentService.createPayment(deliveryMessage);
    }
}
