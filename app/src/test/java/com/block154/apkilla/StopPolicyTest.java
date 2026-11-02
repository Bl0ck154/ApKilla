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

    @Test public void onlyLiveForegroundTargetsAreAccepted() {
        assertTrue(StopPolicy.isUsableTarget(true, true, false));
        assertFalse(StopPolicy.isUsableTarget(false, true, false));
        assertFalse(StopPolicy.isUsableTarget(true, false, false));
        assertFalse(StopPolicy.isUsableTarget(true, true, true));
    }
}
