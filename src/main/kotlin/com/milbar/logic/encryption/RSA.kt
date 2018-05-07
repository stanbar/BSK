package com.milbar.logic.encryption

import java.math.BigInteger
import java.util.*

class RSA(size: Int = 512) {
    /* Select two large prime numbers. Say p and q. */
    private val p: BigInteger = BigInteger(size, 15, Random())
    private val q: BigInteger = BigInteger(size, 15, Random())
    /* Calculate n = p.q */
    private val n: BigInteger = p.multiply(q)
    /* Calculate ø(n) = (p - 1).(q - 1) */
    private val phiN = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE))
    private val e: BigInteger
    private val d: BigInteger

    init {
        /* Find e such that gcd(e, ø(n)) = 1 ; 1 < e < ø(n) */
        var _e: BigInteger
        do {
            _e = BigInteger(2 * size, Random())
        } while (_e.compareTo(phiN) != 1 || _e.gcd(phiN).compareTo(BigInteger.valueOf(1)) != 0)
        e = _e
        /* Calculate d such that e.d = 1 (mod ø(n)) */
        d = e.modInverse(phiN)
    }

    fun encrypt(plaintext: BigInteger): BigInteger {
        return plaintext.modPow(e, n)
    }

    fun encrypt(bytes: ByteArray): ByteArray {
        return BigInteger(bytes).modPow(e, n).toByteArray()
    }

    fun decrypt(plaintext: BigInteger): BigInteger {
        return plaintext.modPow(d, n)
    }

    fun decrypt(bytes: ByteArray): ByteArray {
        return BigInteger(bytes).modPow(d, n).toByteArray()
    }

    companion object {
        fun maxBlockSizeForKey(keySize: Int): Int {
            return ((keySize - 384) / 8) + 37
        }

        fun keySizeForBlock(blockSize: Int): Int {
            return ((blockSize - 37) * 8) + 384
        }
        fun keySizeForBlock(blockSize: Long): Int {
            return (((blockSize - 37) * 8) + 384).toInt()
        }
    }
}