
import junit.framework.Assert.assertEquals
import org.junit.Test

class CipherTest {
    @Test
    fun testXorCipherWithNumber() {
        val data = 0b10110101010000111011010101000011
        val key = 0b10101010101010101010101010101010

        val encodedData = data xor key;

        val decodedData = encodedData xor key;

        assertEquals(data, decodedData)
    }
}