package pl.kacperduras.skinfetcher

import org.junit.Assert
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.runners.MockitoJUnitRunner
import pl.kacperduras.skinfetcher.mock.MockClosable
import retrofit2.Call
import retrofit2.Response
import java.io.File
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class UtilsTest {

  @Test
  fun `closeable test`() {
    val mock = MockClosable()
    Assert.assertFalse(mock.closed)

    closeable(mock) {
      Assert.assertFalse(mock.closed)
    }

    Assert.assertTrue(mock.closed)
  }

  @Test
  fun `directory test`() {
    val directory: File = File.createTempFile("temp", System.nanoTime().toString())

    Assert.assertTrue(directory.createDirectoryIfNotExists().exists())
  }

  @Test
  @Ignore // TODO: wait for Mockito 2 stable - https://stackoverflow.com/questions/14292863/how-to-mock-a-final-class-with-mockito/40018295#40018295
  fun `fetch test`() {
    val mockedCall: Call<Any> = Mockito.mock(Call::class.java) as Call<Any>
    val mockedResponse: Response<Any> = Mockito.mock(Response::class.java) as Response<Any>

    Mockito.`when`(mockedResponse.body()).thenReturn(Any())
    Mockito.`when`(mockedCall.execute()).thenReturn(mockedResponse)

    Assert.assertNotNull(mockedCall.fetch())
  }

  @Test
  fun `color test`() {
    val string = "&cColored message"
    val expected = "§cColored message"

    Assert.assertEquals(expected, string.color())
  }

  @Test
  fun `from uuid to trimmed uuid test`() {
    val uuid: UUID = UUID.fromString("6477d13c-835e-4701-ae79-238324166a85")
    val expected = "6477d13c835e4701ae79238324166a85"

    Assert.assertEquals(expected, uuid.trim())
  }

  @Test
  fun `from trimmed uuid to uuid test`() {
    val uuid = "6477d13c835e4701ae79238324166a85"
    val expected: UUID = UUID.fromString("6477d13c-835e-4701-ae79-238324166a85")

    Assert.assertEquals(expected, uuid.toUUID())
  }

}
