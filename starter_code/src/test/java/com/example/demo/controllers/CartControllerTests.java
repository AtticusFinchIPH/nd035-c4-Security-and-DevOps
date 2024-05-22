package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTests {
    private static final String USERNAME="admin";
    private static final String PASSWORD="admin@123";
    private CartController cartController;
    private final UserRepository userRepository = mock(UserRepository.class);
    private final CartRepository cartRepository = mock(CartRepository.class);
    private final ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setUp() {
        cartController = new CartController();
        TestUtils.injectObject(cartController, "cartRepository", cartRepository);
        TestUtils.injectObject(cartController, "userRepository", userRepository);
        TestUtils.injectObject(cartController, "itemRepository", itemRepository);
    }

    @Test
    public void testAddToCartSuccessfully() {
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
        when(itemRepository.findById(0L)).thenReturn(Optional.of(item));

        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(0L);
        modifyCartRequest.setQuantity(1);
        modifyCartRequest.setUsername(USERNAME);

        final ResponseEntity<Cart> responseEntity = cartController.addToCart(modifyCartRequest);
        Cart responseCart = responseEntity.getBody();
        Item responseItem = responseCart.getItems().get(0);
        User responseUser = responseCart.getUser();

        Assert.assertNotNull(responseEntity);
        Assert.assertNotNull(responseCart);
        Assert.assertNotNull(responseItem);
        Assert.assertNotNull(responseUser);

        Assert.assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
        Assert.assertEquals(item, responseItem);
        Assert.assertEquals(user, responseUser);
    }

    @Test
    public void testAddToCartWithNoUser() {
        when(userRepository.findByUsername(USERNAME)).thenReturn(null);

        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(0L);
        modifyCartRequest.setQuantity(1);
        modifyCartRequest.setUsername(USERNAME);

        final ResponseEntity<Cart> responseEntity = cartController.addToCart(modifyCartRequest);

        Assert.assertTrue(responseEntity.getStatusCode().is4xxClientError());
    }

    @Test
    public void testAddToCartWithIncorrectItem() {
        Item item = new Item(0L,"An item", new BigDecimal("1.99"), "Item description");
        List<Item> items = new ArrayList<>();
        items.add(item);

        User user = new User();
        Cart cart = new Cart(0L,items,user,item.getPrice());
        user.setId(0L);
        user.setUsername(USERNAME);
        user.setPassword(PASSWORD);
        user.setCart(cart);

        when(userRepository.findByUsername(USERNAME)).thenReturn(user);
        when(itemRepository.findById(0L)).thenReturn(Optional.empty());

        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(0L);
        modifyCartRequest.setQuantity(1);
        modifyCartRequest.setUsername(USERNAME);

        final ResponseEntity<Cart> responseEntity = cartController.addToCart(modifyCartRequest);

        Assert.assertTrue(responseEntity.getStatusCode().is4xxClientError());
    }

    @Test
    public void testRemoveFromCartSuccessfully() {
        Item item1 = new Item(0L,"Item 1", new BigDecimal("1"), "Item 1 description");
        Item item2 = new Item(1L,"Item 2", new BigDecimal("2"), "Item 2 description");
        List<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);

        BigDecimal total = item1.getPrice().add(item2.getPrice());
        User user = new User();
        Cart cart = new Cart(0L, items, user, total);
        user.setId(0L);
        user.setUsername(USERNAME);
        user.setPassword(PASSWORD);
        user.setCart(cart);

        when(itemRepository.findById(0L)).thenReturn(Optional.of(item1));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item2));
        when(userRepository.findByUsername(USERNAME)).thenReturn(user);

        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(0L);
        modifyCartRequest.setQuantity(1);
        modifyCartRequest.setUsername(USERNAME);
        ResponseEntity <Cart> responseEntity = cartController.addToCart(modifyCartRequest);

        Assert.assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void testRemoveFromCartWithNoUser() {
        when(userRepository.findByUsername(USERNAME)).thenReturn(null);

        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(0L);
        modifyCartRequest.setQuantity(1);
        modifyCartRequest.setUsername(USERNAME);

        final ResponseEntity<Cart> responseEntity = cartController.removeFromCart(modifyCartRequest);

        Assert.assertTrue(responseEntity.getStatusCode().is4xxClientError());
    }

    @Test
    public void testRemoveFromCartWithIncorrectItem() {
        Item item = new Item(0L,"An item", new BigDecimal("1.99"), "Item description");
        List<Item> items = new ArrayList<>();
        items.add(item);

        User user = new User();
        Cart cart = new Cart(0L,items,user,item.getPrice());
        user.setId(0L);
        user.setUsername(USERNAME);
        user.setPassword(PASSWORD);
        user.setCart(cart);

        when(userRepository.findByUsername(USERNAME)).thenReturn(user);
        when(itemRepository.findById(0L)).thenReturn(Optional.empty());

        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(0L);
        modifyCartRequest.setQuantity(1);
        modifyCartRequest.setUsername(USERNAME);

        final ResponseEntity<Cart> responseEntity = cartController.removeFromCart(modifyCartRequest);

        Assert.assertTrue(responseEntity.getStatusCode().is4xxClientError());
    }
}
