package com.milbar

object Utils{
    @JvmStatic
    fun byteArrayToHex(a: ByteArray): String {
        val sb = StringBuilder(a.size * 2)
        for (b in a)
            sb.append(String.format("%02x", b))
        return sb.toString()
    }
}