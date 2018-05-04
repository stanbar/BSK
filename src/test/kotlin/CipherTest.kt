import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertFalse
import org.junit.Test
import java.security.SecureRandom
import java.util.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.xml.bind.DatatypeConverter


class CipherTest {

    private val data = "Kotlin is the best !!!!!".toByteArray() // in no padding cipher it must be multiple of 8

    @Test
    fun testXorCipherWithNumber() {
        val data = 0b10110101010000111011010101000011
        val key = 0b10101010101010101010101010101010

        val encodedData = data xor key;

        val decodedData = encodedData xor key;

        assertEquals(data, decodedData)
    }

    @Test
    fun testKeyGeneration() {
        val keyGenerator: KeyGenerator = KeyGenerator.getInstance("DES")
        val secretKey = keyGenerator.generateKey()
        println("Key: ${DatatypeConverter.printHexBinary(secretKey.encoded)}")
    }

    @Test
    fun testDesEcbWithPadding() {
        testDesEcb(false, KeyGenerator.getInstance("DES").generateKey())

    }

    @Test
    fun testDesEcbWithoutPadding() {
        testDesEcb(true, KeyGenerator.getInstance("DES").generateKey())

    }

    @Test
    fun testDesCbcWithPadding() {
        testDesCbc(true, KeyGenerator.getInstance("DES").generateKey())
    }

    @Test
    fun testDesCbcWithoutPadding() {
        testDesCbc(false, KeyGenerator.getInstance("DES").generateKey())
    }

    @Test
    fun testDesEcbDeterminism() {
        val key = KeyGenerator.getInstance("DES").generateKey()
        val encrypted1 = testDesEcb(true, key)
        val encrypted2 = testDesEcb(true, key)
        assert(Arrays.equals(encrypted1, encrypted2))
    }

    @Test
    fun testDesCbcPseudoRandomness() {
        val key = KeyGenerator.getInstance("DES").generateKey()
        val encrypted1 = testDesCbc(true, key)
        val encrypted2 = testDesCbc(true, key)
        assertFalse(Arrays.equals(encrypted1, encrypted2))
    }

    private fun testDesCbc(isPadding: Boolean, secretKey: SecretKey): ByteArray {
        val cipher = Cipher.getInstance("DES/CBC/${if (isPadding) "PKCS5Padding" else "NoPadding"}")
        val iv = IvParameterSpec(SecureRandom().generateSeed(8))
        println("Using key: ${DatatypeConverter.printHexBinary(secretKey.encoded)}")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv)
        val encrypted = cipher.doFinal(data)
        println("Encrypted: ${String(encrypted)}");

        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv)
        val decrypted = cipher.doFinal(encrypted)
        println("Decrypted: ${String(decrypted)}")

        assert(Arrays.equals(data, decrypted))
        return encrypted
    }

    private fun testDesEcb(isPadding: Boolean, secretKey: SecretKey): ByteArray {
        val cipher = Cipher.getInstance("DES/ECB/${if (isPadding) "PKCS5Padding" else "NoPadding"}")

        println("Using key: ${DatatypeConverter.printHexBinary(secretKey.encoded)}")

        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val encrypted = cipher.doFinal(data)
        println("Encrypted: ${String(encrypted)}");

        cipher.init(Cipher.DECRYPT_MODE, secretKey)
        val decrypted = cipher.doFinal(encrypted)
        println("Decrypted: ${String(decrypted)}")

        assert(Arrays.equals(data, decrypted))
        return encrypted
    }

}