import org.junit.Test
import java.math.BigInteger
import java.util.*

class TestRsa {
    @Test
    fun testRsa(args: Array<String>) {

        val plainText = BigInteger("Kotlin is the best".toByteArray())

        val size = 512
        /* Step 1: Select two large prime numbers. Say p and q. */
        val p = BigInteger(size, 15, Random())
        val q = BigInteger(size, 15, Random())
        /* Step 2: Calculate n = p.q */
        val n: BigInteger = p.multiply(q)
        /* Step 3: Calculate ø(n) = (p - 1).(q - 1) */
        val phiN = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE))
        /* Step 4: Find e such that gcd(e, ø(n)) = 1 ; 1 < e < ø(n) */
        var e: BigInteger
        do {
            e = BigInteger(2 * size, Random())
        } while (e.compareTo(phiN) != 1 || e.gcd(phiN).compareTo(BigInteger.valueOf(1)) != 0)
        /* Step 5: Calculate d such that e.d = 1 (mod ø(n)) */
        val d: BigInteger = e.modInverse(phiN)

        val cipherText: BigInteger = encrypt(plainText, e, n)

        println("Plaintext : ${String(plainText.toByteArray())}")
        println("CipherText : ${String(cipherText.toByteArray())}")
        val decrypted = decrypt(cipherText, d, n)
        println("After Decryption Plaintext : ${String(decrypted.toByteArray())}")
    }


    private fun encrypt(plaintext: BigInteger, e: BigInteger, n: BigInteger): BigInteger {
        return plaintext.modPow(e, n)
    }

    private fun decrypt(cipherText: BigInteger, d: BigInteger, n: BigInteger): BigInteger {
        return cipherText.modPow(d, n)
    }


}