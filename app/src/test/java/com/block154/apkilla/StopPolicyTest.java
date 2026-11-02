package com.block154.apkilla;

import org.junit.Test;
import static org.junit.Assert.*;

public class StopPolicyTest {
    @Test public void rejectsExpiredAndFutureRequests() {
        assertTrue(StopPolicy.isExpired(0, 100, 100));
        assertTrue(StopPolicy.isExpired(200, 100, 100));
        assertTrue(StopPolicy.isExpired(100, 200, 100));
        assertFalse(StopPolicy.isExpired(100, 199, 100));
    }

    @Test public void activeKeyboardIsNotAnAppTarget() {
        assertTrue(StopPolicy.isInputMethodPackage(
                "com.example.keyboard", "com.example.keyboard/.ImeService"));
        assertFalse(StopPolicy.isInputMethodPackage(
                "com.example.app", "com.example.keyboard/.ImeService"));
        assertFalse(StopPolicy.isInputMethodPackage("com.example.app", null));
    }

    @Test public void checksActualAppTitleBeforeStopping() {
        assertTrue(StopPolicy.matchesAppInfoHeader("com.example.chat", "Chat", "Chat", true));
        assertTrue(StopPolicy.matchesAppInfoHeader("com.example.chat", null,
                "com.example.chat", true));
        assertFalse(StopPolicy.matchesAppInfoHeader("com.example.chat", "Chat",
                "Browser", true));
        assertFalse(StopPolicy.matchesAppInfoHeader("com.example.chat", "Chat",
                "Chat", false));
        assertFalse(StopPolicy.matchesAppInfoHeader("com.example.chat", "Chat",
                "App info", true));
        assertFalse(StopPolicy.matchesAppInfoHeader("com.example.chat", null,
                "Chat", true));
        assertFalse(StopPolicy.matchesAppInfoHeader("com.example.chat", "Chat",
                "", true));
    }

    @Test public void onlyLiveForegroundTargetsAreAccepted() {
        assertTrue(StopPolicy.isUsableTarget(true, true, false));
        assertFalse(StopPolicy.isUsableTarget(false, true, false));
        assertFalse(StopPolicy.isUsableTarget(true, false, false));
        assertFalse(StopPolicy.isUsableTarget(true, true, true));
    }
}
