package pl.kacperduras.skinfetcher

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.md_5.bungee.api.plugin.Plugin
import net.md_5.bungee.config.Configuration
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import net.md_5.bungee.config.YamlConfiguration
import net.md_5.bungee.config.ConfigurationProvider
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import pl.kacperduras.skinfetcher.api.APIExecutor
import java.util.concurrent.TimeUnit

class SFPlugin: Plugin() {

  companion object {
    val GSON: Gson = GsonBuilder().disableHtmlEscaping().create()

    const val UUID_URL: String = "https://api.mojang.com"
    const val SESSION_URL: String =
      "https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false"
    val TRY_AGAIN_MESSAGE: String =
      "&cToo many people are trying to connect at one time, wait and try again later.".color()
  }

  private lateinit var configuration: Configuration
  private lateinit var retrofit: Retrofit
  lateinit var executor: APIExecutor

  override fun onLoad() {
    this.dataFolder.createDirectoryIfNotExists()
    val configurationFile: File = File(this.dataFolder, "config.yml")
      .createFromStream(this.getResourceAsStream("config.yml"))
    this.configuration =
      ConfigurationProvider.getProvider(YamlConfiguration::class.java).load(configurationFile)

    this.retrofit = Retrofit.Builder()
      .addConverterFactory(GsonConverterFactory.create(GSON))
      .baseUrl(UUID_URL)
      .debug()
      .build()
    this.executor = APIExecutor(this)
  }

  override fun onEnable() {
    this.proxy.scheduler.schedule(this, executor, 3, 3, TimeUnit.SECONDS)
    this.proxy.pluginManager.registerListener(this, SFListeners(this))

    MetricsLite(this)
  }

  fun <T> service(classT: Class<T>): T = this.retrofit.create(classT)

  private fun Retrofit.Builder.debug(): Retrofit.Builder {
    val isDebug = configuration.getBoolean("debug")
    if (isDebug) {
      val logging = HttpLoggingInterceptor()
      logging.level = HttpLoggingInterceptor.Level.BODY

      val client: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()
      return this.client(client)
    }

    return this
  }

}
