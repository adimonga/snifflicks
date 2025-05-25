package com.amonga.snifflicks.core.util

import java.security.SecureRandom
import java.security.spec.KeySpec
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import java.util.Base64

object AESUtil {
    private const val AES_ALGORITHM = "AES/CBC/PKCS5Padding"
    private const val KEY_DERIVATION_FUNCTION = "PBKDF2WithHmacSHA256"
    private const val SALT_LENGTH = 16
    private const val IV_LENGTH = 16
    private const val KEY_LENGTH = 256
    private const val ITERATIONS = 65536

    // Encrypts a string with the given password
    fun encrypt(plainText: String, password: String): String {
        val salt = ByteArray(SALT_LENGTH).also { SecureRandom().nextBytes(it) }
        val iv = ByteArray(IV_LENGTH).also { SecureRandom().nextBytes(it) }

        val secretKey = generateKey(password, salt)
        val cipher = Cipher.getInstance(AES_ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, IvParameterSpec(iv))

        val encrypted = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))

        val result = ByteArray(salt.size + iv.size + encrypted.size)
        System.arraycopy(salt, 0, result, 0, salt.size)
        System.arraycopy(iv, 0, result, salt.size, iv.size)
        System.arraycopy(encrypted, 0, result, salt.size + iv.size, encrypted.size)

        return Base64.getEncoder().encodeToString(result)
    }

    // Decrypts a string with the given password
    fun decrypt(cipherText: String, password: String): String? {
        val decoded = Base64.getDecoder().decode(cipherText)

        val salt = decoded.copyOfRange(0, SALT_LENGTH)
        val iv = decoded.copyOfRange(SALT_LENGTH, SALT_LENGTH + IV_LENGTH)
        val encrypted = decoded.copyOfRange(SALT_LENGTH + IV_LENGTH, decoded.size)

        val secretKey = generateKey(password, salt)
        val cipher = Cipher.getInstance(AES_ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, IvParameterSpec(iv))

        val decrypted = cipher.doFinal(encrypted)
        return String(decrypted, Charsets.UTF_8)
    }

    // Key generation from password and salt
    private fun generateKey(password: String, salt: ByteArray): SecretKeySpec {
        val factory = SecretKeyFactory.getInstance(KEY_DERIVATION_FUNCTION)
        val spec: KeySpec = PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH)
        val tmp = factory.generateSecret(spec)
        return SecretKeySpec(tmp.encoded, "AES")
    }
}
