import com.milbar.logic.encryption.RSA
import org.junit.Test
import java.io.File
import java.util.*

class TestRsa {

    @Test
    fun testRsa() {
        val rsa = RSA(128)
        val plainText = "Kotlin is the best".toByteArray()

        val encrypted = rsa.encrypt(plainText)
        val decrypted = rsa.decrypt(encrypted)

        println("Plaintext[${plainText.size}]: ${String(plainText)}")
        println("Encrypted[${encrypted.size}]: ${String(encrypted)}")
        println("Decrypted[${decrypted.size}]: ${String(decrypted)}")

        assert(Arrays.equals(plainText, decrypted))

    }


    @Test
    fun testRsaOnSmallFile() {
        val rsa = RSA()
        val testFile = File("test.txt")
        testFile.writeBytes("Kotlin is the best".toByteArray())

        val encryptedBytes = rsa.encrypt(testFile.readBytes())
        val encryptedFile = File("encrypted_${testFile.name}")
        encryptedFile.writeBytes(encryptedBytes)

        val decryptedBytes = rsa.decrypt(encryptedBytes)
        val decryptedFile = File("decrypted_encrypted_${testFile.name}")
        decryptedFile.writeBytes(decryptedBytes)

        assert(Arrays.equals(testFile.readBytes(), decryptedFile.readBytes()))
    }

    @Test
    fun testRsaOnMediumFile() {

        val testFile = File("test.txt")
        testFile.writeBytes("Kotlin is the best\n".repeat(50).toByteArray())

        println("File length: ${testFile.length()}")
        val keySize = RSA.keySizeForBlock(testFile.length())
        println("KeySize: $keySize")
        val rsa = RSA(keySize)

        val encryptedBytes = rsa.encrypt(testFile.readBytes())
        val encryptedFile = File("encrypted_${testFile.name}")
        encryptedFile.writeBytes(encryptedBytes)

        val decryptedBytes = rsa.decrypt(encryptedBytes)
        val decryptedFile = File("decrypted_encrypted_${testFile.name}")
        decryptedFile.writeBytes(decryptedBytes)

        assert(Arrays.equals(testFile.readBytes(), decryptedFile.readBytes()))
    }

}