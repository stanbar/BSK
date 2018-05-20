package login;

import com.milbar.logic.exceptions.ImplementationError;
import com.milbar.logic.login.CredentialsManager;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.fail;

public class HashingTests {
    
    private CredentialsManager credentialsManager = new CredentialsManager();
    
    private String password = "testpassword123";
    
    @Test
    public void WhenCalculatingHashAgain_HashMatches() {
        byte[] salt = credentialsManager.getSalt();
        try {
            byte[] hash = credentialsManager.getHash(password, salt);
            byte[] hashSecond = credentialsManager.getHash(password, salt);
            
            if (!Arrays.equals(hash, hashSecond))
                fail("Hashes does not match after recalculation.");
        } catch (ImplementationError e) {
            fail(e.getMessage());
        }
    }
    
    @Test
    public void WhenValidatingPassword_HashValidationIsCorrect() {
        byte[] salt = credentialsManager.getSalt();
        try {
            byte[] hash = credentialsManager.getHash(password, salt);
            if (!credentialsManager.validatePassword(password, salt, hash))
                fail("Password validation for the same string and salt returned false.");
        } catch (ImplementationError e) {
            fail(e.getMessage());
        }
    }
    
    @Test
    public void WhenComparingHashes_HashWithDifferentSaltIsDifferent() {
        byte[] salt = credentialsManager.getSalt();
        byte[] saltSecond = credentialsManager.getSalt();
        try {
            byte[] hash = credentialsManager.getHash(password, salt);
            byte[] hashSecond = credentialsManager.getHash(password, saltSecond);
            if (Arrays.equals(hash, hashSecond))
                fail("Hashes with different salt are equals. They should be different.");
        } catch (ImplementationError e) {
            fail(e.getMessage());
        }
    }
    
    @Test
    public void WhenValidatingPassword_HashWithDifferentSaltIsNotValid() {
        byte[] salt = credentialsManager.getSalt();
        byte[] saltSecond = credentialsManager.getSalt();
        try {
            byte[] hash = credentialsManager.getHash(password, salt);
            byte[] hashSecond = credentialsManager.getHash(password, saltSecond);
            if (credentialsManager.validatePassword(password, saltSecond, hash))
                fail("Password's hash with different salt is valid. Should be not valid.");
        } catch (ImplementationError e) {
            fail(e.getMessage());
        }
    }
    
    @Test(timeout = 1000)
    //@Test
    public void WhenGettingSalt_SaltsDoesNotRepeat() {
        int setSize = 100000;
        Integer hash;
        Set<Integer> saltsSet = new HashSet<>(setSize);
        
        for (int i = 0; i < setSize; i++) {
            hash = Arrays.hashCode(credentialsManager.getSalt());
            if (!saltsSet.add(hash))
                fail("Duplicate salt after " + i + " iterations.");
        }
    }
    
}
