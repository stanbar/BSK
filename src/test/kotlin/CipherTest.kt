import junit.framework.Assert.assertEquals
import org.junit.Test
import java.security.SecureRandom
import java.util.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.xml.bind.DatatypeConverter


class CipherTest {
    enum class Algorithm(val ivLength: Int) {
        AES(16), DES(8), Blowfish(8)
    }

    enum class Mode {
        ECB, CBC, CFB, OFB
    }


    private val data = "Kotlin is the best lang ever !!!".toByteArray()
    // In no padding algo it must be multiple of 8(DES) or 16(AES)

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

    /**
     * DES
     */
    @Test
    fun testDesEcbWithPadding() {
        testCohesion(algorithm = Algorithm.DES,
                mode = Mode.ECB,
                isPadding = true)
    }

    @Test
    fun testDesEcbWithoutPadding() {
        testCohesion(algorithm = Algorithm.DES,
                mode = Mode.ECB,
                isPadding = false)

    }

    @Test
    fun testDesCbcWithPadding() {
        testCohesion(algorithm = Algorithm.DES,
                mode = Mode.CBC,
                isPadding = true)
    }

    @Test
    fun testDesCbcWithoutPadding() {
        testCohesion(algorithm = Algorithm.DES,
                mode = Mode.CBC,
                isPadding = false)
    }

    @Test
    fun testDesCfbWithPadding() {
        testCohesion(algorithm = Algorithm.DES,
                mode = Mode.CFB,
                isPadding = true)
    }

    @Test
    fun testDesOfbWithPadding() {
        testCohesion(algorithm = Algorithm.DES,
                mode = Mode.OFB,
                isPadding = true)
    }

    @Test
    fun testDesEcbDeterminism() {
        val key = KeyGenerator.getInstance("DES").generateKey()
        val initVector = "RandInit".toByteArray()
        val encrypted1 = testCohesion(Algorithm.DES, Mode.ECB, true, initVector, key)
        val encrypted2 = testCohesion(Algorithm.DES, Mode.ECB, true, initVector, key)
        assert(Arrays.equals(encrypted1, encrypted2))
    }

    @Test
    fun testDesCbcDeterminism() {
        val key = KeyGenerator.getInstance("DES").generateKey()
        val initVector = "RandInit".toByteArray()
        val encrypted1 = testCohesion(Algorithm.DES, Mode.CBC, true, initVector, key)
        val encrypted2 = testCohesion(Algorithm.DES, Mode.CBC, true, initVector, key)
        assert(Arrays.equals(encrypted1, encrypted2))
    }
    /**
     * Blowfish
     */
    @Test
    fun testBlowfishEcbWithPadding() {
        testCohesion(algorithm = Algorithm.Blowfish,
                mode = Mode.ECB,
                isPadding = true)
    }

    @Test
    fun testBlowfishEcbWithoutPadding() {
        testCohesion(algorithm = Algorithm.Blowfish,
                mode = Mode.ECB,
                isPadding = false)

    }

    @Test
    fun testBlowfishCbcWithPadding() {
        testCohesion(algorithm = Algorithm.Blowfish,
                mode = Mode.CBC,
                isPadding = true)
    }

    @Test
    fun testBlowfishCbcWithoutPadding() {
        testCohesion(algorithm = Algorithm.Blowfish,
                mode = Mode.CBC,
                isPadding = false)
    }

    @Test
    fun testBlowfishCfbWithPadding() {
        testCohesion(algorithm = Algorithm.Blowfish,
                mode = Mode.CFB,
                isPadding = true)
    }

    @Test
    fun testBlowfishOfbWithPadding() {
        testCohesion(algorithm = Algorithm.Blowfish,
                mode = Mode.OFB,
                isPadding = true)
    }

    @Test
    fun testBlowfishEcbDeterminism() {
        val key = KeyGenerator.getInstance("Blowfish").generateKey()
        val initVector = "RandInit".toByteArray()
        val encrypted1 = testCohesion(Algorithm.Blowfish, Mode.ECB, true, initVector, key)
        val encrypted2 = testCohesion(Algorithm.Blowfish, Mode.ECB, true, initVector, key)
        assert(Arrays.equals(encrypted1, encrypted2))
    }

    @Test
    fun testBlowfishCbcDeterminism() {
        val key = KeyGenerator.getInstance("Blowfish").generateKey()
        val initVector = "RandInit".toByteArray()
        val encrypted1 = testCohesion(Algorithm.Blowfish, Mode.CBC, true, initVector, key)
        val encrypted2 = testCohesion(Algorithm.Blowfish, Mode.CBC, true, initVector, key)
        assert(Arrays.equals(encrypted1, encrypted2))
    }
    /**
     * AES
     */
    @Test
    fun testAesEcbWithPadding() {
        testCohesion(algorithm = Algorithm.AES,
                mode = Mode.ECB,
                isPadding = true)
    }

    @Test
    fun testAesEcbWithoutPadding() {
        testCohesion(algorithm = Algorithm.AES,
                mode = Mode.ECB,
                isPadding = false)

    }

    @Test
    fun testAesCbcWithPadding() {
        testCohesion(algorithm = Algorithm.AES,
                mode = Mode.CBC,
                isPadding = true)
    }

    @Test
    fun testAesCbcWithoutPadding() {
        testCohesion(algorithm = Algorithm.AES,
                mode = Mode.CBC,
                isPadding = false)
    }

    @Test
    fun testAesCfbWithPadding() {
        testCohesion(algorithm = Algorithm.AES,
                mode = Mode.CFB,
                isPadding = true)
    }

    @Test
    fun testAesOfbWithPadding() {
        testCohesion(algorithm = Algorithm.AES,
                mode = Mode.OFB,
                isPadding = true)
    }

    @Test
    fun testAesEcbDeterminism() {
        val key = KeyGenerator.getInstance(Algorithm.AES.name).generateKey()
        val initVector = "RandomInitVector".toByteArray()
        val encrypted1 = testCohesion(Algorithm.AES, Mode.ECB, true, initVector, key)
        val encrypted2 = testCohesion(Algorithm.AES, Mode.ECB, true, initVector, key)
        assert(Arrays.equals(encrypted1, encrypted2))
    }

    @Test
    fun testAesCbcDeterminism() {
        val key = KeyGenerator.getInstance(Algorithm.AES.name).generateKey()
        val initVector = "RandomInitVector".toByteArray()
        val encrypted1 = testCohesion(Algorithm.AES, Mode.CBC, true, initVector, key)
        val encrypted2 = testCohesion(Algorithm.AES, Mode.CBC, true, initVector, key)
        assert(Arrays.equals(encrypted1, encrypted2))
    }


    private fun testCohesion(algorithm: Algorithm,
                             mode: Mode,
                             isPadding: Boolean,
                             initVectorBytes: ByteArray = SecureRandom.getInstanceStrong().generateSeed(algorithm.ivLength),
                             secretKey: SecretKey = KeyGenerator.getInstance(algorithm.name).generateKey()): ByteArray {

        val cipher = Cipher.getInstance("${algorithm.name}/${mode.name}/${if (isPadding) "PKCS5Padding" else "NoPadding"}")

        val initVector = IvParameterSpec(initVectorBytes)

        println("Using key: ${DatatypeConverter.printHexBinary(secretKey.encoded)} and InitVector: ${DatatypeConverter.printHexBinary(initVectorBytes)}")

        if (mode == Mode.ECB)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        else
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, initVector)

        val encrypted = cipher.doFinal(data)
        println("Encrypted: ${String(encrypted)}");

        if (mode == Mode.ECB)
            cipher.init(Cipher.DECRYPT_MODE, secretKey)
        else
            cipher.init(Cipher.DECRYPT_MODE, secretKey, initVector)
        val decrypted = cipher.doFinal(encrypted)

        println("Decrypted: ${String(decrypted)}")

        assert(Arrays.equals(data, decrypted))
        return encrypted
    }


}