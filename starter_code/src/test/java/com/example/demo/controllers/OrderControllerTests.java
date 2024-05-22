package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTests {
    private static final String USERNAME="admin";
    private static final String UNKNOWN_USERNAME="unknown";
    private static final String PASSWORD="admin@123";
    private OrderController orderController;
    private final UserRepository userRepository = mock(UserRepository.class);
    private final OrderRepository orderRepository = mock(OrderRepository.class);

    @Before
    public void setUp() {
        orderController = new OrderController();
        TestUtils.injectObject(orderController, "userRepository", userRepository);
        TestUtils.injectObject(orderController, "orderRepository", orderRepository);
    }

    @Test
    public void testSubmitSuccessfully() {
        Item item = new Item(0L,"An item", new BigDecimal("1.99"), "Item description");
        List<Item> items = new ArrayList<>();
        items.add(item);

        User user = new User();
        Cart cart = new Cart(0L, items, user, item.getPrice());
        user.setId(0L);
        user.setUsername(USERNAME);
        user.setPassword(PASSWORD);
        user.setCart(cart);

        when(userRepository.findByUsername(USERNAME)).thenReturn(user);
        final ResponseEntity<UserOrder> responseEntity = orderController.submit(USERNAME);
        Assert.assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void testSubmitFail() {
        when(userRepository.findByUsername(UNKNOWN_USERNAME)).thenReturn(null);
        final ResponseEntity<UserOrder> responseEntity = orderController.submit(UNKNOWN_USERNAME);
        Assert.assertTrue(responseEntity.getStatusCode().is4xxClientError());
    }

    @Test
    public void testGetOrdersForUserSuccessfully() {
        Item item = new Item(0L,"An item", new BigDecimal("1.99"), "Item description");
        List<Item> items = new ArrayList<>();
        items.add(item);

        User user = new User();
        Cart cart = new Cart(0L, items, user, item.getPrice());
        user.setId(0L);
        user.setUsername(USERNAME);
        user.setPassword(PASSWORD);
        user.setCart(cart);

        when(userRepository.findByUsername(USERNAME)).thenReturn(user);
        final ResponseEntity<UserOrder> submitResponseEntity = orderController.submit(USERNAME);
        Assert.assertTrue(submitResponseEntity.getStatusCode().is2xxSuccessful());

        UserOrder submittedOrder = submitResponseEntity.getBody();
        List<UserOrder> submittedOrders = new ArrayList<>();
        submittedOrders.add(submittedOrder);
        when(orderRepository.findByUser(user)).thenReturn(submittedOrders);
        final ResponseEntity<List<UserOrder>> responseEntity = orderController.getOrdersForUser(USERNAME);
        Assert.assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void testGetOrdersForUserFail() {
        when(userRepository.findByUsername(UNKNOWN_USERNAME)).thenReturn(null);
        final ResponseEntity<List<UserOrder>> responseEntity = orderController.getOrdersForUser(UNKNOWN_USERNAME);
        Assert.assertTrue(responseEntity.getStatusCode().is4xxClientError());
    }
}
