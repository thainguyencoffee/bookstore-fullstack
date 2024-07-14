package com.bookstore.backend.core.demo;

import com.bookstore.backend.book.*;
import com.bookstore.backend.purchaseorder.OrderRepository;
import com.bookstore.backend.shopppingcart.ShoppingCartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Profile("datatest")
@Slf4j
@RequiredArgsConstructor
public class DataTest {

    private final BookService bookService;
    private final OrderRepository orderRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private static final Random random = new Random();

    @EventListener(ApplicationReadyEvent.class)
    public void setupDataTest() {
        bookService.deleteAll();
        orderRepository.deleteAll();
        shoppingCartRepository.deleteAll();

        var listPhotos = List.of(
                "http://res.cloudinary.com/dl0v8gbku/image/upload/v1718216795/samples/ecommerce/accessories-bag.jpg",
                "http://res.cloudinary.com/dl0v8gbku/image/upload/v1718216795/samples/ecommerce/leather-bag-gray.jpg",
                "http://res.cloudinary.com/dl0v8gbku/image/upload/v1718216793/samples/landscapes/beach-boat.jpg"
        );
        Set<Long> generatedNumbers = new HashSet<>();

        while (generatedNumbers.size() < 50) {
            long number = (long) Math.floor(Math.random() * 9_000_000_000L) + 1_000_000_000L;
            generatedNumbers.add(number);
        }
        // fake data

        var list = new ArrayList<Book>();
        for (long isbn : generatedNumbers) {
            var book = Book.builder()
                    .isbn(String.valueOf(isbn))
                    .title(generateRandomName())
                    .author(generateRandomName())
                    .publisher(generateRandomName())
                    .supplier(generateRandomName())
                    .price(19000000L)
                    .inventory(3)
                    .language(Language.ENGLISH)
                    .coverType(CoverType.HARDCOVER)
                    .numberOfPages(25)
                    .measure(new Measure(120d, 180d, 10d, 200d))
                    .purchases(0)
                    .photos(listPhotos)
                    .build();
            list.add(book);
        }

        bookService.saveAll(list);


        // Shopping Cart
    }

    private static final String[] SIMPLE_NAMES = {
            "Max", "Bella", "Charlie", "Lucy", "Cooper", "Daisy", "Buddy", "Luna",
            "Rocky", "Sadie", "Bailey", "Chloe", "Jack", "Lily", "Toby", "Zoe"
    };

    private static final String[] DOUBLE_NAMES = {
            "Bear", "Duke", "Lucky", "Princess", "Shadow", "Coco", "Buster", "Molly",
            "Harley", "Ruby", "Tucker", "Penny", "Riley", "Maggie", "Bentley", "Sophie"
    };

    public static String generateRandomName() {
        boolean isDoubleName = random.nextBoolean();

        if (isDoubleName) {
            String firstName = SIMPLE_NAMES[random.nextInt(SIMPLE_NAMES.length)];
            String secondName = DOUBLE_NAMES[random.nextInt(DOUBLE_NAMES.length)];
            return firstName + " " + secondName;
        } else {
            return SIMPLE_NAMES[random.nextInt(SIMPLE_NAMES.length)];
        }
    }

}
