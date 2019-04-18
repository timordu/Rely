/*
 *    Copyright (c) 2017-2019 dugang
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.android.rely.common

import android.util.Base64
import com.android.rely.common.hexToByteArray
import com.android.rely.common.toHex
import com.blankj.ALog
import java.io.ByteArrayOutputStream
import java.nio.charset.Charset
import java.security.*
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

/*
  ---------- Base64编解码 ----------
 */

fun String.base64Encode2Str(charset: Charset = Charsets.UTF_8, flags: Int = Base64.NO_WRAP): String =
    String(base64Encode(flags), charset)

fun ByteArray.base64Encode2Str(charset: Charset = Charsets.UTF_8, flags: Int = Base64.NO_WRAP): String =
    String(base64Encode(flags), charset)

fun String.base64Encode(flags: Int = Base64.NO_WRAP): ByteArray = Base64.encode(toByteArray(), flags)

fun ByteArray.base64Encode(flags: Int = Base64.NO_WRAP): ByteArray = Base64.encode(this, flags)

fun String.base64Decode2Str(charset: Charset = Charsets.UTF_8, flags: Int = Base64.NO_WRAP): String =
    String(base64Decode(flags), charset)

fun ByteArray.base64Decode2Str(charset: Charset = Charsets.UTF_8, flags: Int = Base64.NO_WRAP): String =
    String(base64Decode(flags), charset)

fun String.base64Decode(flags: Int = Base64.NO_WRAP): ByteArray = Base64.decode(this, flags)

fun ByteArray.base64Decode(flags: Int = Base64.NO_WRAP): ByteArray = Base64.decode(this, flags)

/*
  ---------- hash加密 ----------
 */
private fun ByteArray.hashEncrypt(algorithm: String): String = MessageDigest.getInstance(algorithm).digest(this).toHex()

/** md5加密 */
fun String.md5(salt: String = ""): String = (this + salt).toByteArray().md5()

fun ByteArray.md5(): String = hashEncrypt("MD5")

/** SHA1加密 */
fun String.sha1(salt: String = ""): String = (this + salt).toByteArray().sha1()

fun ByteArray.sha1(): String = hashEncrypt("SHA1")

/** SHA224加密 */
fun String.sha224(salt: String = ""): String = (this + salt).toByteArray().sha224()

fun ByteArray.sha224(): String = hashEncrypt("SHA-224")

/** SHA256加密 */
fun String.sha256(salt: String = ""): String = (this + salt).toByteArray().sha256()

fun ByteArray.sha256(): String = hashEncrypt("SHA-256")

/** SHA384加密 */
fun String.sha384(salt: String = ""): String = (this + salt).toByteArray().sha384()

fun ByteArray.sha384(): String = hashEncrypt("SHA-384")

/** SHA512加密 */
fun String.sha512(salt: String = ""): String = (this + salt).toByteArray().sha512()

fun ByteArray.sha512(): String = hashEncrypt("SHA-512")

enum class Type { TYPE_HEX, TYPE_BASE64 }

/**
 * 生成DES/AES的SecretKey
 *
 * @param password 密钥
 * @param keySizeInBytes 密钥长度 需要*8  取值只能为 16 24 32 对应密钥长度为128、192、256
 */
private fun deriveKeyInsecurely(password: String, keySizeInBytes: Int, encryptType: String): SecretKey {
    val salt = password.replace("-", "").toByteArray()
    val keySpec = PBEKeySpec(password.toCharArray(), salt, 1000, keySizeInBytes * 8)
    val keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
    val keyBytes = keyFactory.generateSecret(keySpec).encoded
    return SecretKeySpec(keyBytes, encryptType)
}

/**
 * 获取DES/AES加密或解密后的ByteArray
 *
 * @param raw 生成的密钥
 * @param byteArray 待加密或解密的ByteArray
 * @param transformation 加密填充方式
 * @param isEncrypt 是否是加密模式
 */
private fun encryptOrDecrypt(raw: Key, byteArray: ByteArray, transformation: String, isEncrypt: Boolean): ByteArray {
    val algorithm = transformation.substring(0, transformation.indexOf('/')).toUpperCase()
    val cipher = Cipher.getInstance(algorithm)

    val model = if (isEncrypt) Cipher.ENCRYPT_MODE else Cipher.DECRYPT_MODE
    val aps = if (algorithm == "DES")
        IvParameterSpec("01020304".toByteArray())
    else
        IvParameterSpec(ByteArray(cipher.blockSize))

    cipher.init(model, raw, aps)
    return cipher.doFinal(byteArray)
}

/*
  ---------- AES加解密 ----------
 */
/**
 * AES加密
 *
 * @param password  密钥
 * @param type 加密结果输出方式  DES.TYPE_HEX 或者 DES.TYPE_BASE64
 * @param transformation 加密填充方式
 * @param keySizeInBytes 密钥长度 需要*8  取值只能为 16 24 32 对应密钥长度为128、192、256
 */
fun String.encryptByAES(
    password: String,
    type: Type = Type.TYPE_BASE64,
    transformation: String = "AES/CBC/PKCS5Padding",
    keySizeInBytes: Int = 32
): String {
    val result = toByteArray().encryptByAES(password, transformation, keySizeInBytes)
    return if (type == Type.TYPE_HEX) result.toHex() else result.base64Encode2Str()
}

fun ByteArray.encryptByAES(
    password: String,
    transformation: String = "AES/CBC/PKCS5Padding",
    keySizeInBytes: Int = 32
): ByteArray {
    val rawKey = deriveKeyInsecurely(password, keySizeInBytes, "AES")
    return encryptOrDecrypt(rawKey, this, transformation, true)
}


/**
 * AES解密
 *
 * @param password  密钥
 * @param type 加密数据格式  DES.TYPE_HEX 或者 DES.TYPE_BASE64
 * @param transformation 加密填充方式
 * @param keySizeInBytes 密钥长度 需要*8  取值只能为 16 24 32 对应密钥长度为128、192、256
 */
fun String.decryptByAES(
    password: String,
    type: Type = Type.TYPE_BASE64,
    transformation: String = "AES/CBC/PKCS5Padding",
    keySizeInBytes: Int = 32
): String {
    val ba = if (type == Type.TYPE_HEX) hexToByteArray() else base64Decode()
    val result = ba.decryptByAES(password, transformation, keySizeInBytes)
    return String(result)
}

fun ByteArray.decryptByAES(
    password: String,
    transformation: String = "AES/CBC/PKCS5Padding",
    keySizeInBytes: Int = 32
): ByteArray {
    ALog.dTag("aes", "password: $password")
    val rawKey = deriveKeyInsecurely(password, keySizeInBytes, "AES")
    ALog.dTag("aes", "rawKey: ${rawKey.encoded.base64Encode2Str()}")
    return encryptOrDecrypt(rawKey, this, transformation, false)
}

/*
  ---------- DES加解密 ----------
*/
/**
 * DES加密
 * @param password 密钥
 * @param type 加密结果输出方式  DES.TYPE_HEX 或者 DES.TYPE_BASE64
 * @param transformation 加密填充方式
 */
fun String.encryptByDES(
    password: String,
    type: Type = Type.TYPE_BASE64,
    transformation: String = "DES/CBC/PKCS5Padding"
): String {
    val result = toByteArray().encryptByDES(password, transformation)
    return if (type == Type.TYPE_HEX) result.toHex() else result.base64Encode2Str()
}

fun ByteArray.encryptByDES(password: String, transformation: String = "DES/CBC/PKCS5Padding"): ByteArray {
    val rawKey = deriveKeyInsecurely(password, 8, "DES")
    return encryptOrDecrypt(rawKey, this, transformation, true)
}


/**
 * DES解密
 * @param password 密钥
 * @param type 加密字符串的结果输出方式
 * @param transformation 加密填充方式
 */
fun String.decryptByDES(
    password: String,
    type: Type = Type.TYPE_BASE64,
    transformation: String = "DES/CBC/PKCS5Padding"
): String {
    val ba = if (type == Type.TYPE_HEX) hexToByteArray() else base64Decode()
    val result = ba.decryptByDES(password, transformation)
    return String(result)
}

fun ByteArray.decryptByDES(password: String, transformation: String = "DES/CBC/PKCS5Padding"): ByteArray {
    val rawKey = deriveKeyInsecurely(password, 8, "DES")
    return encryptOrDecrypt(rawKey, this, transformation, false)
}
/*
  ---------- RSA加解密 ----------
 */
/**
 * 生成RSA的密钥对
 *
 * @param keyLength 密钥长度
 */
fun generateRSAKeyPair(keyLength: Int = 2048): KeyPair =
    KeyPairGenerator.getInstance("RSA").apply { initialize(keyLength) }.genKeyPair()

/**
 * 在生成的密钥对中获取公钥和私钥
 *
 * @param isPublicKey true 公钥 false 私钥
 */
fun KeyPair.getRSAKey(isPublicKey: Boolean): String =
    (if (isPublicKey) public.encoded else private.encoded).base64Encode2Str()

/**
 * 用私钥对信息生成数字签名
 *
 * @param privateKey 私钥
 * @param algorithm 签名算法
 *
 * @return 签名结果,根据type生成对应类型的签名字符串
 */
fun String.rsaSign(privateKey: String, algorithm: String = "MD5withRSA"): String {
    return toByteArray().rsaSign(privateKey.base64Decode(), algorithm).base64Encode2Str()
}

fun ByteArray.rsaSign(privateKey: ByteArray, algorithm: String = "MD5withRSA"): ByteArray {
    val priKey = KeyFactory.getInstance("RSA").generatePrivate(PKCS8EncodedKeySpec(privateKey))
    val signature = Signature.getInstance(algorithm).apply {
        initSign(priKey)
        update(this@rsaSign)
    }
    return signature.sign()
}

/**
 * 验证数字签名
 *
 * @param publicKey
 * @param sign 签名
 * @param algorithm 签名算法
 *
 * @return 验证结果
 */
fun String.verifyRsaSign(publicKey: String, sign: String, algorithm: String = "MD5withRSA"): Boolean {
    return toByteArray().verifyRsaSign(publicKey.base64Decode(), sign, algorithm)
}

fun ByteArray.verifyRsaSign(publicKey: ByteArray, sign: String, algorithm: String = "MD5withRSA"): Boolean {
    val pubKey = KeyFactory.getInstance("RSA").generatePublic(X509EncodedKeySpec(publicKey))
    val signature = Signature.getInstance(algorithm).apply {
        initVerify(pubKey)
        update(this@verifyRsaSign)
    }
    return signature.verify(sign.base64Decode())
}

/**
 * RSA加密
 *
 * @param key 密钥字符串
 * @param isPublicKey 是否是公钥,默认true
 * @param transformation 填充方式
 * @param keyLength 密钥长度
 */
fun String.encryptByRSA(
    key: String,
    isPublicKey: Boolean = true,
    transformation: String = "RSA/ECB/PKCS1Padding",
    keyLength: Int = 2048
): String {
    return toByteArray().encryptByRSA(key.base64Decode(), isPublicKey, transformation, keyLength).base64Encode2Str()
}

fun ByteArray.encryptByRSA(
    key: ByteArray,
    isPublicKey: Boolean = true,
    transformation: String = "RSA/ECB/PKCS1Padding",
    keyLength: Int = 2048
): ByteArray {
    val cleartextLen = keyLength / 8 - 11
    val keyLen = keyLength / 8
    val blockSize = if (size % cleartextLen == 0) size / cleartextLen else size / cleartextLen + 1
    val outBuffer = ByteArrayOutputStream(blockSize * keyLen)

    val keyFactory = KeyFactory.getInstance("RSA")
    val securityKey =
        if (isPublicKey) keyFactory.generatePublic(X509EncodedKeySpec(key)) else keyFactory.generatePrivate(
            PKCS8EncodedKeySpec(key)
        )
    val cipher = Cipher.getInstance(transformation).apply { init(Cipher.ENCRYPT_MODE, securityKey) }

    outBuffer.use {
        for (offset in 0 until size step cleartextLen) {
            val inputLen = if (size - offset > cleartextLen) cleartextLen else size - offset
            val encryptedBlock = cipher.doFinal(this, offset, inputLen)
            outBuffer.write(encryptedBlock, 0, encryptedBlock.size)
        }
        return outBuffer.toByteArray()
    }
}


/**
 * RSA解密
 *
 * @param key 私钥
 * @param isPublicKey 是否是公钥,默认false
 * @param transformation 填充方式
 * @param keyLength 密钥长度
 */
fun String.decryptByRSA(
    key: String,
    isPublicKey: Boolean = false,
    transformation: String = "RSA/ECB/PKCS1Padding",
    keyLength: Int = 2048
): String {
    val decrypt = base64Decode().decryptByRSA(key.base64Decode(), isPublicKey, transformation, keyLength)
    return String(decrypt)
}

fun ByteArray.decryptByRSA(
    key: ByteArray,
    isPublicKey: Boolean = false,
    transformation: String = "RSA/ECB/PKCS1Padding",
    keyLength: Int = 2048
): ByteArray {
    val cleartextLen = keyLength / 8 - 11
    val keyLen = keyLength / 8
    val blockSize = if (size % cleartextLen == 0) size / cleartextLen else size / cleartextLen + 1
    val outBuffer = ByteArrayOutputStream(blockSize * cleartextLen)

    val keyFactory = KeyFactory.getInstance("RSA")
    val securityKey =
        if (isPublicKey) keyFactory.generatePublic(X509EncodedKeySpec(key)) else keyFactory.generatePrivate(
            PKCS8EncodedKeySpec(key)
        )
    val cipher = Cipher.getInstance(transformation).apply { init(Cipher.DECRYPT_MODE, securityKey) }

    outBuffer.use {
        for (offset in 0 until size step keyLen) {
            val inputLen = if (size - offset > keyLen) keyLen else size - offset
            val decryptedBlock = cipher.doFinal(this, offset, inputLen)
            outBuffer.write(decryptedBlock, 0, decryptedBlock.size)
        }
        return outBuffer.toByteArray()
    }
}
