package com.example

import org.junit.Assert.*
import org.junit.Test
import java.net.URL
import java.io.File

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
      try {
          val url = URL("https://raw.githubusercontent.com/recloudstream/extensions/builds/plugins.json")
          val conn = url.openConnection()
          val text = conn.getInputStream().bufferedReader().readText()
          File("test_output.json").writeText(text.take(1500))
      } catch (e: Exception) {
          e.printStackTrace()
      }
    assertEquals(4, 2 + 2)
  }
}
