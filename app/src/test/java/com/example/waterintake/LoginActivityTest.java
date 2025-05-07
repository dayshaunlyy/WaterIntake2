package com.example.waterintake;

import com.example.waterintake.data.AppDatabase;
import com.example.waterintake.data.dao.UserDao;
import com.example.waterintake.data.entities.User;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class LoginActivityTest {

    private UserDao mockUserDao;

    @Before
    public void setUp() {
        mockUserDao = mock(UserDao.class);
    }

    @Test
    public void testValidUserAndPassword() {
        // Given
        User user = new User("user1", "pass123");
        when(mockUserDao.getUserByUsernameSync("user1")).thenReturn(user);

        // When
        User fetchedUser = mockUserDao.getUserByUsernameSync("user1");

        // Then
        assertNotNull(fetchedUser);
        assertEquals("pass123", fetchedUser.getPassword());
    }

    @Test
    public void testUserNotFound() {
        when(mockUserDao.getUserByUsernameSync("ghost")).thenReturn(null);

        User result = mockUserDao.getUserByUsernameSync("ghost");
        assertNull(result);
    }
}
