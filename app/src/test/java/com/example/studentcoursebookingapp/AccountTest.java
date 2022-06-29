package com.example.studentcoursebookingapp;

import junit.framework.TestCase;

import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AccountTest {

    @Test
    public void isValidUsername() {
        assertTrue(Account.isValidUsername("alexUsername"));
    }

    @Test
    public void isValidUsername2() {
        assertTrue(Account.isValidUsername("alexandre_Poirier"));
    }

    @Test
    public void isValidUsername3() {
        assertFalse(Account.isValidUsername("a"));
    }

    @Test
    public void isValidUsername4() {
        assertFalse(Account.isValidUsername("alex$$$"));
    }

    @Test
    public void isValidUsername5() {
        assertFalse(Account.isValidUsername("alex Poirier"));
    }

    @Test
    public void isValidUsername6() {
        assertFalse(Account.isValidUsername("a"));
    }

    @Test
    public void isValidPassword() {
        assertTrue(Account.isValidPassword("alexPwd123"));
    }

    @Test
    public void isValidPassword2() {
        assertFalse(Account.isValidPassword("a"));
    }

    @Test
    public void isValidPassword3() {
        assertFalse(Account.isValidPassword(""));
    }

}