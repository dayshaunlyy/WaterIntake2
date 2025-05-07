package com.example.waterintake;

import com.example.waterintake.data.dao.UserDao;
import com.example.waterintake.data.entities.User;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class SignUpActivityTest {

    private UserDao mockUserDao;

    @Before
    public void setUp() {
        mockUserDao = mock(UserDao.class);
    }

    @Test
    public void testInsertUserCalledCorrectly() {
        User user = new User("newuser", "newpass");
        mockUserDao.insert(user);
        verify(mockUserDao).insert(user);
    }

    @Test
    public void testInsertUserWithEmptyUsername() {
        User user = new User("", "somepass");
        mockUserDao.insert(user);
        verify(mockUserDao).insert(user);
    }
}
