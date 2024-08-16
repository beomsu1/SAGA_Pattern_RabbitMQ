package com.market.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Order {

    private UUID orderId;
    private String userId;
    private String orderStatus;
    private String errorType;
    
    // 주문 취소
    public void cancelOrder(String receiveErrorType){
        this.orderStatus = "CANCELED";
        this.errorType = receiveErrorType;
    }
}
