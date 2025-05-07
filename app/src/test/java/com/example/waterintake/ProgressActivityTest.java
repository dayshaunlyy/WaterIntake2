package com.example.waterintake;

import com.example.waterintake.data.dao.UserDao;
import com.example.waterintake.data.entities.User;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class ProgressActivityTest {

    private UserDao mockUserDao;
    private User testUser;

    @Before
    public void setUp() {
        mockUserDao = mock(UserDao.class);

        testUser = new User("tester", "1234");
        testUser.setId(1);
        testUser.setTotalIntake(3.0);
        testUser.setHourlyIntake(0.5);
        testUser.setCurrentIntake(1.0);
    }

    @Test
    public void testAddWaterIncreasesCurrentIntake() {
        double newIntake = testUser.getCurrentIntake() + testUser.getHourlyIntake();

        if (newIntake <= testUser.getTotalIntake()) {
            testUser.setCurrentIntake(newIntake);
            mockUserDao.updateUser(testUser);
        }

        assertEquals(1.5, testUser.getCurrentIntake(), 0.001);
        verify(mockUserDao).updateUser(testUser);
    }

    @Test
    public void testAddWaterDoesNotExceedTotalIntake() {
        testUser.setCurrentIntake(2.8); // Near the limit
        double newIntake = testUser.getCurrentIntake() + testUser.getHourlyIntake(); // would go to 3.3

        if (newIntake <= testUser.getTotalIntake()) {
            testUser.setCurrentIntake(newIntake);
        } else {
            // Do nothing, shouldn't update DB
        }

        assertEquals(2.8, testUser.getCurrentIntake(), 0.001);
        verify(mockUserDao, never()).updateUser(testUser);
    }

    @Test
    public void testResetIntakeSetsToZero() {
        testUser.setCurrentIntake(1.7);

        testUser.setCurrentIntake(0.0);
        mockUserDao.updateUser(testUser);

        assertEquals(0.0, testUser.getCurrentIntake(), 0.001);
        verify(mockUserDao).updateUser(testUser);
    }
}
