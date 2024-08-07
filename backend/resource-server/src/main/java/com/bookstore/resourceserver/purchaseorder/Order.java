package com.bookstore.resourceserver.purchaseorder;

import com.bookstore.resourceserver.purchaseorder.dto.OrderRequest;
import com.bookstore.resourceserver.purchaseorder.dto.UserInformation;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Table("orders")
@Getter
@Setter
@NoArgsConstructor
public class Order  {
    @Id
    private UUID id;
    private Long totalPrice;
    private OrderStatus status;
    private PaymentMethod paymentMethod;
    @MappedCollection(idColumn = "order_id")
    private Set<LineItem> lineItems = new HashSet<>();
    @Embedded(onEmpty = Embedded.OnEmpty.USE_NULL)
    private UserInformation userInformation;
    private Long otp;
    private Instant otpExpiredAt;
    @CreatedDate
    private Instant createdDate;
    @CreatedBy
    private String createdBy;
    @LastModifiedDate
    private Instant lastModifiedDate;
    @LastModifiedBy
    private String lastModifiedBy;

    @Version
    private int version;

    public static Order createOrder(List<LineItem> lineItems, OrderRequest orderRequest) {
        Order order = new Order();
        order.setUserInformation(orderRequest.getUserInformation());
        order.setPaymentMethod(orderRequest.getPaymentMethod());
        if (orderRequest.getPaymentMethod() == PaymentMethod.VNPAY) {
            order.setStatus(OrderStatus.WAITING_FOR_PAYMENT);
        } else {
            order.setStatus(OrderStatus.WAITING_FOR_ACCEPTANCE);
        }
        Long totalPrice = 0L;
        for (LineItem lineItem : lineItems) {
            order.getLineItems().add(lineItem);
            lineItem.setOrderId(order.getId());
            totalPrice += lineItem.getTotalPrice();
        }
        order.setTotalPrice(totalPrice);
        return order;
    }

}