package in.andres.kandroid.ui;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LoginActivityTest {

    @Test
    public void testUnsupportedVersion() {
        assertFalse(LoginActivity.isSupportedVersion(new int[] {1, 0, 30}));
    }

    @Test
    public void testOldSupportedVersion() {
        assertTrue(LoginActivity.isSupportedVersion(new int[] {1, 0, 39}));
    }

    @Test
    public void testRecentSupportedVersion() {
        assertTrue(LoginActivity.isSupportedVersion(new int[] {1, 2, 0}));
    }

}
