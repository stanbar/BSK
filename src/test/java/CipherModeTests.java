import com.milbar.logic.abstracts.Mode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CipherModeTests {
    
    @Test
    public void IfECBMode_ThenNoInitializeVector() {
        Mode ecbMode = Mode.ECB;
        Mode cbcMode = Mode.CBC;
        Mode cfbMode = Mode.CFB;
        Mode ofbMode = Mode.OFB;
        
        assertFalse(ecbMode.initVectorRequired);
        assertTrue(cbcMode.initVectorRequired);
        assertTrue(cfbMode.initVectorRequired);
        assertTrue(ofbMode.initVectorRequired);
    }
    
    @Test
    public void BlockModeTypeSuppler_ReturnsModeName() {
        Mode ecbMode = Mode.ECB;
        
        assertTrue(ecbMode.get().equals(ecbMode.fullName));
    }
}
