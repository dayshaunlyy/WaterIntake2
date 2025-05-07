package com.example.waterintake;

import com.example.waterintake.data.dao.UserDao;
import com.example.waterintake.data.entities.User;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class UserDashboardActivityTest {

    private UserDao mockUserDao;

    @Before
    public void setUp() {
        mockUserDao = mock(UserDao.class);
    }

    @Test
    public void testUserUpdateCallsDaoCorrectly() {
        User user = new User("testuser", "password");
        user.setId(1);
        user.setHeight(175.0);
        user.setWeight(70.0);
        user.setUseCreatine(true);
        user.setWorkoutLevel("Moderate (3–5 times a week)");
        mockUserDao.updateUser(user);
        verify(mockUserDao).updateUser(user);
    }
}
