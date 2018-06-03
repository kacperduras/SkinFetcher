package pl.kacperduras.skinfetcher

import com.google.common.io.ByteStreams
import net.md_5.bungee.api.ChatColor
import retrofit2.Call
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.*

inline fun <T> closeable(vararg elements: AutoCloseable, body: () -> T) {
  body.invoke()

  elements.forEach { it.close() }
}

fun File.createDirectoryIfNotExists(): File {
  if (!this.isFile && !this.exists()) {
    this.mkdirs()
  }

  return this
}

fun File.createFromStream(input: InputStream): File {
  if (!this.exists()) {
    this.createNewFile()

    val output = FileOutputStream(this)
    closeable(input, output) {
      ByteStreams.copy(input, output)
    }
  }

  return this
}

fun <T> Call<T>.fetch(): T? = this.execute().body()

fun String.color(): String = ChatColor.translateAlternateColorCodes('&', this)

fun UUID.trim(): String = this.toString().replace("-", "")

fun String.toUUID(): UUID = UUID.fromString(
  this.replace("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})".toRegex(), "$1-$2-$3-$4-$5"))
