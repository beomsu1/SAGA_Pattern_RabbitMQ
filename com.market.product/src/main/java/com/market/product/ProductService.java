package com.market.product;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@Log4j2
@RequiredArgsConstructor
public class ProductService {

    private final RabbitTemplate rabbitTemplate;

    @Value("${message.queue.payment}")
    private String paymentQueue;

    @Value("${message.queue.err.order}")
    private String orderErrQueue;

    public void reduceProductAmount(DeliveryMessage deliveryMessage){

        Integer productId = deliveryMessage.getProductId();
        Integer productQuantity = deliveryMessage.getProductQuantity();

        if(productId != 1 || productQuantity > 1){
            rollbackProduct(deliveryMessage);
            return;
        }

        log.info("PayAmount: {}", deliveryMessage.getPayAmount());

        rabbitTemplate.convertAndSend(paymentQueue, deliveryMessage);


    }

    // 주문 실패 시 재고 +1 시키라는 메서드 (orderErrQueue로 DeliveryMessage 전달)
    public void rollbackProduct(DeliveryMessage deliveryMessage) {
        log.info("PRODUCT ROLLBACK!");

        // 에러가 기록되어 있지 않으면 오더에서 들어온 deliveryMessage
        if(!StringUtils.hasText(deliveryMessage.getErrorType())){
            deliveryMessage.setErrorType("PRODUCT ERROR");
        }
        rabbitTemplate.convertAndSend(orderErrQueue,deliveryMessage);
    }
}
