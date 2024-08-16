package com.market.order;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Log4j2
@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final RabbitTemplate rabbitTemplate;
    
    @Value("${message.queue.product}")
    private String productQueue;

    private Map<UUID, Order> orderStore = new HashMap<>();

    public Order createOrder(OrderEndpoint.OrderRequestDTO orderRequestDTO) {

        Order order = orderRequestDTO.toOrder();

        orderStore.put(order.getOrderId(), order);

        DeliveryMessage deliveryMessage = orderRequestDTO.toDeliveryMessage(order.getOrderId());
        
        // 메세지를 ProductQueue로 전송
        rabbitTemplate.convertAndSend(productQueue, deliveryMessage);

        return order;
    }

    public Order getOrder(UUID orderId) {

        return orderStore.get(orderId);
    }

    public void rollbackOrder(DeliveryMessage deliveryMessage) {
        Order order = orderStore.get(deliveryMessage.getOrderId());
        order.cancelOrder(deliveryMessage.getErrorType());
        log.info(order);
    }
}
