package com.market.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryMessage {

    private UUID orderId;
    private UUID payment;

    private String userId;
    private Integer productId;
    private Integer productQuantity;
    private Integer payAmount;

    private String errorType;
}
