import org.junit.Test
import javax.crypto.Mac

class HmacTest {
    @Test
    fun testHmac(){
        Mac.getInstance("HmacDES")
    }
}