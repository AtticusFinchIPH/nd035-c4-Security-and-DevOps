package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTests {
    private static final String ITEM_NAME = "ITEM_NAME";
    private ItemController itemController;
    private final ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setUp() {
        itemController = new ItemController();
        TestUtils.injectObject(itemController, "itemRepository", itemRepository);
    }

    @Test
    public void testGetItems() {
        List<Item> items = new ArrayList<>();
        Item item = new Item();
        item.setId(0L);
        items.add(item);
        when(itemRepository.findAll()).thenReturn(items);
        final ResponseEntity<List<Item>> responseEntity = itemController.getItems();
        List<Item> responseItems = responseEntity.getBody();

        Assert.assertEquals(items, responseItems);
    }

    @Test
    public void testGetItemById() {
        Item item = new Item();
        item.setId(0L);
        when(itemRepository.findById(0L)).thenReturn(Optional.of(item));
        final ResponseEntity<Item> responseEntity = itemController.getItemById(0L);
        Item responseItem = responseEntity.getBody();

        Assert.assertEquals(item, responseItem);
    }
    @Test
    public void testGetItemsByName() {
        List<Item> items = new ArrayList<>();
        Item item = new Item();
        item.setId(0L);
        item.setName(ITEM_NAME);
        items.add(item);
        when(itemRepository.findByName(ITEM_NAME)).thenReturn(items);
        final ResponseEntity<List<Item>> responseEntity = itemController.getItemsByName(ITEM_NAME);
        List<Item> responseItems = responseEntity.getBody();

        Assert.assertEquals(items, responseItems);
    }
}
