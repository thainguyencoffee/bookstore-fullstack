package com.bookstore.backend.orders;

import com.bookstore.backend.book.Book;
import com.bookstore.backend.book.BookService;
import com.bookstore.backend.book.exception.BookNotEnoughInventoryException;
import com.bookstore.backend.orders.dto.LineItemRequest;
import com.bookstore.backend.orders.dto.UserInformation;
import com.bookstore.backend.orders.exception.OrderNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    private final LineItemRepository lineItemRepository;
    private final OrderRepository orderRepository;
    private final BookService bookService;

    public Order findById(UUID id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }

    @Transactional
    public Order submitOrder(List<LineItemRequest> lineItemRequests, UserInformation userInformation) {
        List<LineItem> lineItems = new ArrayList<>();
        for (LineItemRequest lineItemRequest : lineItemRequests) {
            LineItem lineItem = convertLineItemRequestToLineItem(lineItemRequest);
            lineItems.add(lineItem);
        }
        Order order = Order.createOrder(lineItems, userInformation);
        orderRepository.save(order);
        /*===== ORDER WAITING FOR PAYMENT =====*/
        return order;
    }

    @Transactional
    public Order buildAcceptedOrder(UUID orderId) {
        Order order = findById(orderId);
        if (order.getStatus() != OrderStatus.WAITING_FOR_PAYMENT) {
            throw new ConsistencyDataException("Order's status is not waiting for payment");
        }
        for (LineItem lineItem : order.getLineItems()) {
            var book = bookService.findByIsbn(lineItem.getIsbn());
            if (book.getInventory() < lineItem.getQuantity()) {
                throw new BookNotEnoughInventoryException(book.getIsbn());
            }
            var bookUpdate = book;
            bookUpdate.setInventory(book.getInventory() - lineItem.getQuantity());
            bookUpdate.setPurchases(book.getPurchases() + lineItem.getQuantity());
            bookService.save(bookUpdate);
        }
        order.setStatus(OrderStatus.ACCEPTED);
        /*===== CREATED ORDER =====*/
        orderRepository.save(order);
        return order;
    }

    private LineItem convertLineItemRequestToLineItem(LineItemRequest lineItemRequest) {
        Book book = bookService.findByIsbn(lineItemRequest.getIsbn());
        if (book.getInventory() < lineItemRequest.getQuantity()) {
            throw new BookNotEnoughInventoryException(lineItemRequest.getIsbn());
        }

        LineItem lineItem = new LineItem();
        lineItem.setQuantity(lineItemRequest.getQuantity());
        lineItem.setPrice(book.getPrice());
        lineItem.setIsbn(lineItemRequest.getIsbn());
        return lineItem;
    }

    public Page<Order> findAllByCreatedBy(String username, Pageable pageable) {
        return orderRepository.findAllByCreatedBy(username, pageable);
    }
}