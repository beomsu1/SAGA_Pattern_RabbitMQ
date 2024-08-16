package com.market.order;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Log4j2
@RestController
@RequiredArgsConstructor
public class OrderEndpoint {

    @RabbitListener(queues = "${message.queue.err.order}")
    public void errOrder(DeliveryMessage deliveryMessage){
        log.info("ERROR RECEIVE !");

        log.info(deliveryMessage.toString());

        orderService.rollbackOrder(deliveryMessage);
    }

    private final OrderService orderService;

    @GetMapping("/order/{orderId}")
    public ResponseEntity<Order> getOrder(@PathVariable UUID orderId) {

        Order order = orderService.getOrder(orderId);
        log.info(order);
        return ResponseEntity.ok(order);
    }

    @PostMapping("/order")
    public ResponseEntity<Order> order(@RequestBody OrderRequestDTO orderRequestDTO) {

        return ResponseEntity.ok(orderService.createOrder(orderRequestDTO));
    }

    @Data
    public static class OrderRequestDTO {

        private String userId;
        private Integer productId;
        private Integer productQuantity;
        private Integer payAmount;

        public Order toOrder() {
            return Order.builder()
                    .orderId(UUID.randomUUID())
                    .userId(userId)
                    .orderStatus("RECEIPT")
                    .build();
        }

        public DeliveryMessage toDeliveryMessage(UUID orderId) {
            return DeliveryMessage.builder()
                    .orderId(orderId)
                    .userId(userId)
                    .productId(productId)
                    .productQuantity(productQuantity)
                    .payAmount(payAmount)
                    .build();
        }
    }
}
