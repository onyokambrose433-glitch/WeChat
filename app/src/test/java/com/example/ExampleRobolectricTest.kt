package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.db.WeChatDatabase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("WeChat", appName)
  }

  @Test
  fun `password hash produces secure sha256 output`() {
    val hash1 = WeChatDatabase.hashPassword("Secret123", "salt_alpha")
    val hash2 = WeChatDatabase.hashPassword("Secret123", "salt_beta")
    val hash3 = WeChatDatabase.hashPassword("Secret123", "salt_alpha")

    assertEquals(hash1, hash3)
    assertNotEquals(hash1, hash2)
    assertEquals(64, hash1.length)
  }
}
