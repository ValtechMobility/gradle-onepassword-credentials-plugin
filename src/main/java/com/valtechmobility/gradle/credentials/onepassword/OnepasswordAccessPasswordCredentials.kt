package com.valtechmobility.gradle.credentials.onepassword

import org.gradle.api.credentials.PasswordCredentials

/**
 * Provides lazy access to passwords through the 1password cli.
 * 1. Enable 1password CLI: https://developer.1password.com/docs/cli/get-started
 * 2. Create a gradle.properties in your home directory that contains the key of the username&password in the vault
 *
 * Not storing the username and password here is very slow (gradle sync takes ~ 3minutes),
 * thus username and password are stored here temporarily.
 */
public class OnepasswordAccessPasswordCredentials(
    private val vaultKey: String
) : PasswordCredentials {

    private var vaultUsername: ByteArray? = null
    private var vaultPassword: ByteArray? = null

    override fun getUsername(): String {
        val bytes = vaultUsername ?: requestVaultEntry("username").also {
            vaultUsername = it
        }
        return String(bytes)
    }

    override fun getPassword(): String {
        val bytes = vaultPassword ?: requestVaultEntry("password").also {
            vaultPassword = it
        }
        return String(bytes)
    }

    private fun requestVaultEntry(label: String): ByteArray {
        var process: Process? = null
        try {
            val runtime = Runtime.getRuntime()
            process = runtime.exec("op item get \"$vaultKey\" --fields label=$label")
            val result = process.waitFor()
            if (result != 0) {
                println("Could not access vault!")
                println(String(process.errorStream.readBytes()))
                Exception().printStackTrace()
                return ByteArray(0)
            }
            return process.inputStream.readBytes()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            process?.destroyForcibly()
        }
        return ByteArray(0)
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
                it[i] = 0
            }
        }
        vaultPassword?.let {
            for (i in it.indices) {
                it[i] = 0
            }
        }
    }
}
