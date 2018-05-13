package login;

import com.milbar.logic.exceptions.ImplementationError;
import com.milbar.logic.login.CredentialsManager;
import org.junit.Test;

import static org.junit.Assert.fail;

public class HashingTests {
    
    private CredentialsManager credentialsManager = new CredentialsManager();
    
    
    @Test
    public void WhenCalculatingHashAgain_HashMatches() {
        String password = "testpassword123";
        byte[] salt = credentialsManager.getSalt();
        try {
            byte[] hash = credentialsManager.getHash(password, salt);
        } catch (ImplementationError e) {
            fail(e.getMessage());
        }
        
    }
    
}
