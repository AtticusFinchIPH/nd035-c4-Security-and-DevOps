package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTests {
    private static final String USERNAME="admin";
    private static final String PASSWORD="admin@123";
    private static final String INCORRECT_PASSWORD ="INCORRECT_PASSWORD";

    private UserController userController;
    private final UserRepository userRepository = mock(UserRepository.class);
    private final CartRepository cartRepository = mock(CartRepository.class);
    private final BCryptPasswordEncoder bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp(){
        userController = new UserController();
        TestUtils.injectObject(userController, "userRepository", userRepository);
        TestUtils.injectObject(userController, "cartRepository", cartRepository);
        TestUtils.injectObject(userController, "bCryptPasswordEncoder", bCryptPasswordEncoder);
    }

    private static CreateUserRequest createUserRequest() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername(USERNAME);
        createUserRequest.setPassword(PASSWORD);
        createUserRequest.setConfirmPassword(PASSWORD);
        return createUserRequest;
    }

    private static final CreateUserRequest userRequest = createUserRequest();
    
    private User createUser() {
        final ResponseEntity<User> responseEntityCreateUser = userController.createUser(userRequest);
        Assert.assertTrue(responseEntityCreateUser.getStatusCode().is2xxSuccessful());
        User user = responseEntityCreateUser.getBody();
        Assert.assertNotNull(user);
        Assert.assertEquals(0, user.getId());
        return user;
    }

    @Test
    public void testFindById() {
        User user = createUser();
        
        Long id = user.getId();
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        final ResponseEntity<User> responseEntityFindUser = userController.findById(id);
        Assert.assertNotNull(responseEntityFindUser);
        Assert.assertTrue(responseEntityFindUser.getStatusCode().is2xxSuccessful());
        User testUser = responseEntityFindUser.getBody();
        Assert.assertNotNull(testUser);
        Assert.assertEquals(user.getId(), testUser.getId());
        Assert.assertSame(user, testUser);
    }

    @Test
    public void testFindByUserName() {
        User user = createUser();

        when(userRepository.findByUsername(USERNAME)).thenReturn(user);
        final ResponseEntity<User> responseEntityFindUser = userController.findByUserName(USERNAME);
        Assert.assertNotNull(responseEntityFindUser);
        Assert.assertTrue(responseEntityFindUser.getStatusCode().is2xxSuccessful());
        User testUser = responseEntityFindUser.getBody();
        Assert.assertNotNull(testUser);
        Assert.assertEquals(user.getUsername(), testUser.getUsername());
        Assert.assertSame(user, testUser);
    }

    @Test
    public void testCreateUserFail() {
        CreateUserRequest createUserRequest = createUserRequest();
        createUserRequest.setConfirmPassword(INCORRECT_PASSWORD);

        final ResponseEntity<User> responseEntity = userController.createUser(createUserRequest);
        Assert.assertNotNull(responseEntity);
        Assert.assertTrue(responseEntity.getStatusCode().is4xxClientError());
        User user = responseEntity.getBody();
        Assert.assertNull(user);
    }
}
