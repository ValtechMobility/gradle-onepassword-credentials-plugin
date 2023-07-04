/*
 * Copyright (C) 2023 Valtech Mobility
 */
package io.github.valtechmobility.gradle.credentials.onepassword

import org.gradle.api.credentials.PasswordCredentials

/**
 *
 * Provides lazy access to passwords through the 1password cli.
 * 1. Enable 1password CLI: https://developer.1password.com/docs/cli/get-started
 * 2. Create a gradle.properties in your home directory
 * 3. Place the key of the vault in the gradle.properties (Default is Private)
 * 4. Place the key of the username&password in the vault in the gradle.properties
 *
 * Not storing the username and password here is very slow (gradle sync takes ~ 3minutes),
 * thus username and password are stored here temporarily.
 */
public class OnepasswordAccessCredentials(
    private val vaultKey: String,
    private val itemKey: String
) : PasswordCredentials {

    private var vaultUsername: CharArray? = null
    private var vaultPassword: CharArray? = null

    override fun getUsername(): String {
        val chars = vaultUsername ?: requestVaultEntry("username").also {
            vaultUsername = it
        }
        return chars.concatToString()
    }

    override fun getPassword(): String {
        val chars = vaultPassword ?: requestVaultEntry("password").also {
            vaultPassword = it
        }
        return chars.concatToString()
    }

    private fun requestVaultEntry(label: String): CharArray {
        var process: Process? = null
        try {
            val runtime = Runtime.getRuntime()
            val uri = "op://$vaultKey/$itemKey/$label"
            process = runtime.exec(arrayOf("op", "read", uri))
            val result = process.waitFor()
            if (result != 0) {
                println("Could not access vault!")
                println(String(process.errorStream.readBytes()))
                return CharArray(0)
            }
            val bytes = process.inputStream.readBytes()
            val string = bytes.decodeToString()
            for (i in bytes.indices) {
                bytes[i] = 0
            }
            val endIndex = string.lastIndexOf { it.isISOControl() }
            return string.toCharArray(0, endIndex)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            process?.destroyForcibly()
        }
        return CharArray(0)
    }

    private inline fun CharSequence.lastIndexOf(crossinline predicate: (Char) -> Boolean): Int {
        for (i in length - 1 downTo 0) {
            if (!predicate(get(i))) {
                return i + 1
            }
        }
        return 0
    }

    override fun setUsername(userName: String?) {
        throw UnsupportedOperationException("Can not set the username on these inaccessible credentials!")
    }

    override fun setPassword(password: String?) {
        throw UnsupportedOperationException("Can not set the password on these inaccessible credentials!")
    }

    protected fun finalize() {
        vaultUsername?.let {
            for (i in it.indices) {
                it[i] = '\u0000'
            }
        }
        vaultPassword?.let {
            for (i in it.indices) {
                it[i] = '\u0000'
            }
        }
    }
}
