package com.market.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Log4j2
@RequiredArgsConstructor
@Service
public class PaymentService {

    private final RabbitTemplate rabbitTemplate;

    @Value("${message.queue.err.product}")
    private String productErrQueue;

    public void createPayment(DeliveryMessage deliveryMessage){

        Payment payment = Payment.builder()
                .paymentId(UUID.randomUUID())
                .userId(deliveryMessage.getUserId())
                .payAmount(deliveryMessage.getPayAmount())
                .payStatus("SUCCESS")
                .build();

        if(payment.getPayAmount() >= 10000){
            log.error("Payment Amount Exceeds Limit: {}", payment.getPayAmount());
            deliveryMessage.setErrorType("PAYMENT_LIMIT_EXCEEDED");
            payment.setPayStatus("FAILED");

            rollbackPayment(deliveryMessage);
        }
    }

    // 주문 실패 메서드 - ProductErrQueue로 deliveryMEssage 전송
    public void rollbackPayment(DeliveryMessage deliveryMessage){
        log.info("PAYMENT ROLLBACK!");

        rabbitTemplate.convertAndSend(productErrQueue,deliveryMessage);
    }
}
