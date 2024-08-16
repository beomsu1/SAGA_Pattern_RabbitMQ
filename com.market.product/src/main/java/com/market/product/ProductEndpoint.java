package com.market.product;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class ProductEndpoint {

    private final ProductService productService;

    @RabbitListener(queues = "${message.queue.product}")
    public void receiveMessage(DeliveryMessage deliveryMessage){
        log.info("Product Receive: "+ deliveryMessage.toString());

        productService.reduceProductAmount(deliveryMessage);
    }

    @RabbitListener(queues = "${message.queue.err.product}")
    public void receiveErrorMessage(DeliveryMessage deliveryMessage){
        log.info("ERROR RECEIVE !");
        productService.rollbackProduct(deliveryMessage);
    }
}
