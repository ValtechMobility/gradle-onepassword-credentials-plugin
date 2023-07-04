package io.github.valtechmobility.gradle.credentials.onepassword

import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
internal class OnepasswordAccessCredentialsTest {

    private lateinit var credentials: OnepasswordAccessCredentials
    private lateinit var process: Process
    @Before
    fun setup() {
        credentials = OnepasswordAccessCredentials(EXAMPLE_VAULT_KEY, EXAMPLE_ITEM_KEY)
        mockkStatic(Runtime::getRuntime)
        process = mockk {
            every { waitFor() } returns 0
            every { inputStream } returns EXAMPLE_RESULT.byteInputStream()
            every { destroyForcibly() } returns this
        }
        every { Runtime.getRuntime().exec(any<Array<String>>()) } returns process
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
                .exec(arrayOf("op", "read", "op://$EXAMPLE_VAULT_KEY/$EXAMPLE_ITEM_KEY/username"))
        }
    }

    @Test
    fun credentialsCreated_passwordRequested_passwordQueriedFromOP() {
        credentials.password
        verify {
            Runtime.getRuntime()
                .exec(arrayOf("op", "read", "op://$EXAMPLE_VAULT_KEY/$EXAMPLE_ITEM_KEY/password"))
        }
    }

    @Test
    fun credentialsCreated_usernameRequestedMultipleTimes_usernameQueriedFromOPOnce() {
        credentials.username
        credentials.username
        verify(exactly = 1) {
            Runtime.getRuntime()
                .exec(arrayOf("op", "read", "op://$EXAMPLE_VAULT_KEY/$EXAMPLE_ITEM_KEY/username"))
        }
    }

    @Test
    fun credentialsCreated_passwordRequestedMultipleTimes_passwordQueriedFromOPOnce() {
        credentials.password
        credentials.password
        verify(exactly = 1) {
            Runtime.getRuntime()
                .exec(arrayOf("op", "read", "op://$EXAMPLE_VAULT_KEY/$EXAMPLE_ITEM_KEY/password"))
        }
    }

    @Test
    fun credentialsContainControlChars_usernameRequested_usernameReturnedWithoutControlChars() {
        every { process.inputStream } returns
            "$EXAMPLE_RESULT\u0000\u0001\u001E\u001F\u007F\u0080\u009E\u009F"
                .byteInputStream()
        val username = credentials.username
        Assert.assertEquals(
            "Username did not have all iso control chars removed!",
            EXAMPLE_RESULT,
            username,
        )
    }

    @Test
    fun credentialsContainControlChars_passwordRequested_passwordReturnedWithoutControlChars() {
        every { process.inputStream } returns
            "$EXAMPLE_RESULT\u0000\u0001\u001E\u001F\u007F\u0080\u009E\u009F"
                .byteInputStream()
        val password = credentials.password
        Assert.assertEquals(
            "Password did not have all iso control chars removed!",
            EXAMPLE_RESULT,
            password,
        )
    }

    companion object {
        private const val EXAMPLE_VAULT_KEY = "EXAMPLE_VAULT_KEY"
        private const val EXAMPLE_ITEM_KEY = "EXAMPLE_ITEM_KEY"
        private const val EXAMPLE_RESULT = "EXAMPLE_RESULT"
    }
}
