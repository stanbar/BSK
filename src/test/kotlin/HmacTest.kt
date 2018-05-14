
import org.junit.Assert.assertEquals
import org.junit.Test
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import javax.xml.bind.DatatypeConverter
import kotlin.system.measureTimeMillis


class HmacTest {
    private val message = "Kotlin is the best"
    private val privateKey = "myPrivateKey"

    @Test
    fun testHmacSha1() {
        val algorithm = "HmacSHA1"
        val data = message.toByteArray()
        val mac = Mac.getInstance(algorithm)
        val key = SecretKeySpec(privateKey.toByteArray(), algorithm)
        mac.init(key)
        val digest = mac.doFinal(data)

        assertEquals(Utils.byteArrayToHex(digest), "de262839aa0bafea75d0d43f05cc433952dc2b3c".toUpperCase())

    }

    @Test
    fun testHmacSha256() {
        val algorithm = "HmacSHA256"
        val data = message.toByteArray()
        val mac = Mac.getInstance(algorithm)
        val key = SecretKeySpec(privateKey.toByteArray(), algorithm)
        mac.init(key)
        val digest = mac.doFinal(data)

        assertEquals(Utils.byteArrayToHex(digest), "405fdc5c2054a4b75c4f7d285b0c8b6cc82708cb8d5f67d374bc382de7af214f".toUpperCase())

    }

    @Test
    fun testHmacMD5() {
        val algorithm = "HmacMD5"
        val data = message.toByteArray()
        val mac = Mac.getInstance(algorithm)
        val key = SecretKeySpec(privateKey.toByteArray(), algorithm)
        mac.init(key)
        val digest = mac.doFinal(data)

        assertEquals(Utils.byteArrayToHex(digest), "e5c0c66988dbabcc35c9bb234c74095d".toUpperCase())
    }

    @Test
    fun testSpeedHmacMD5() {
        var digest: ByteArray = byteArrayOf()
        val time = measureTimeMillis{
            val algorithm = "HmacMD5"
            val data = message.repeat(10000000).toByteArray()
            val mac = Mac.getInstance(algorithm)
            val key = SecretKeySpec(privateKey.toByteArray(), algorithm)
            mac.init(key)
            digest = mac.doFinal(data)
        }

        println(Utils.byteArrayToHex(digest))
        println("MD5 Seconds ${time / 1000.0 }")


    }

    @Test
    fun testSpeedHmacSHA1() {
        var digest: ByteArray = byteArrayOf()
        val time = measureTimeMillis {
            val algorithm = "HmacSHA1"
            val data = message.repeat(10000000).toByteArray()
            val mac = Mac.getInstance(algorithm)
            val key = SecretKeySpec(privateKey.toByteArray(), algorithm)
            mac.init(key)
            digest = mac.doFinal(data)
        }

        println(Utils.byteArrayToHex(digest))
        println("SHA1 Seconds ${time / 1000.0}")
    }
}