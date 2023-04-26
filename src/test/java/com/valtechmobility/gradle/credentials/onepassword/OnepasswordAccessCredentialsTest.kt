package com.valtechmobility.gradle.credentials.onepassword

import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
internal class OnepasswordAccessCredentialsTest {

    private lateinit var credentials: OnepasswordAccessCredentials

    @Before
    fun setup() {
        credentials = OnepasswordAccessCredentials(EXAMPLE_VAULT_KEY)
        mockkStatic(Runtime::getRuntime)
        every { Runtime.getRuntime().exec(any<String>()) } returns mockk {
            every { waitFor() } returns 0
            every { inputStream } returns EXAMPLE_RESULT.byteInputStream()
            every { destroyForcibly() } returns this
        }
    }

    @After
    fun teardown() {
        unmockkAll()
    }

    @Test
    fun credentialsCreated_usernameRequested_usernameQueriedFromOP() {
        credentials.username
        verify {
            Runtime.getRuntime()
                .exec("op item get \"$EXAMPLE_VAULT_KEY\" --fields label=username")
        }
    }

    @Test
    fun credentialsCreated_passwordRequested_passwordQueriedFromOP() {
        credentials.password
        verify {
            Runtime.getRuntime()
                .exec("op item get \"$EXAMPLE_VAULT_KEY\" --fields label=password")
        }
    }

    @Test
    fun credentialsCreated_usernameRequestedMultipleTimes_usernameQueriedFromOPOnce() {
        credentials.username
        credentials.username
        verify(exactly = 1) {
            Runtime.getRuntime()
                .exec("op item get \"$EXAMPLE_VAULT_KEY\" --fields label=username")
        }
    }

    @Test
    fun credentialsCreated_passwordRequestedMultipleTimes_passwordQueriedFromOPOnce() {
        credentials.password
        credentials.password
        verify(exactly = 1) {
            Runtime.getRuntime()
                .exec("op item get \"$EXAMPLE_VAULT_KEY\" --fields label=password")
        }
    }

    companion object {
        private const val EXAMPLE_VAULT_KEY = "EXAMPLE_VAULT_KEY"
        private const val EXAMPLE_RESULT = "EXAMPLE_RESULT"
    }
}
