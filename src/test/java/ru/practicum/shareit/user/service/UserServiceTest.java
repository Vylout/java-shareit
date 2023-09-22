package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exeption.ElementNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.utils.ValidationErrors;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;

    @Test
    void getUserTest() {
        User user = new User(1L, "user1", "user1@mail.com");

        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(user));
        User gottenUser = userService.getUserById(1L);
        assertNotNull(gottenUser);
        assertEquals(gottenUser, user);
        verify(userRepository, times(1)).findById(any(Long.class));
    }

    @Test
    void getAllUsersTest() {
        User user1 = new User(1L, "user1", "user1@mail.com");
        User user2 = new User(2L, "user2", "user2@mail.com");

        when(userRepository.findAll())
                .thenReturn(List.of(user1, user2));
        Collection<User> users = userService.getAllUsers();
        assertNotNull(users);
        assertEquals(2, users.size());
        assertTrue(users.contains(user1));
        assertTrue(users.contains(user2));
        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void getUserByInvalidIdTest() {
        userService.addUser(User.builder().name("user").email("user@mail.com").build());
        ElementNotFoundException exception = assertThrows(ElementNotFoundException.class, () ->
                userService.getUserById(10L));
        assertThat(exception.getMessage(), equalTo(ValidationErrors.USER_NOT_FOUND));
    }

    @Test
    void createUserTest() {
        User user = new User(1L, "user1", "user1@mail.com");

        when(userRepository.save(any(User.class)))
                .thenReturn(user);
        User createdUser = userService.addUser(user);
        assertNotNull(createdUser);
        assertEquals(user, createdUser);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUserTest() {
        User user = new User(1L, "user1", null);

        when(userRepository.save(any(User.class)))
                .thenReturn(user);
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(user));
        User updatedUser = userService.updateUser(user);
        assertNotNull(updatedUser);
        assertEquals(updatedUser, user);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void updateUserByInvalidIdTest() {
        userService.addUser(User.builder().name("user").email("user@mail.com").build());
        ElementNotFoundException exception = assertThrows(ElementNotFoundException.class, () ->
                userService.updateUser(User.builder().id(2L).name("user").email("user@mail.com").build()));
        assertThat(exception.getMessage(), equalTo(ValidationErrors.USER_NOT_FOUND));
    }

    @Test
    void deleteUserByIdTest() {
        userService.removeUser(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }
}
