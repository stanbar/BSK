package com.milbar.model

import com.milbar.Utils
import com.milbar.logic.encryption.Algorithm
import com.milbar.logic.encryption.Mode
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

data class CipherConfig(val secretKeyBytes: ByteArray,
                        val initialVectorBytes: ByteArray,
                        val algorithm: Algorithm,
                        val mode: Mode) {
    val secretKeyHex: String = Utils.byteArrayToHex(secretKeyBytes)
    val initialVectorHex: String = Utils.byteArrayToHex(initialVectorBytes)

    fun getSecretKey() : SecretKey = SecretKeySpec(secretKeyBytes, algorithm.algorithmName)
}