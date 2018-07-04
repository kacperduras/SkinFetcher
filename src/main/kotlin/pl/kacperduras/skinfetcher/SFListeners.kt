package pl.kacperduras.skinfetcher

import com.google.gson.JsonObject
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.connection.PendingConnection
import net.md_5.bungee.api.event.LoginEvent
import net.md_5.bungee.api.event.PreLoginEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.connection.InitialHandler
import net.md_5.bungee.connection.LoginResult
import net.md_5.bungee.event.EventHandler
import pl.kacperduras.skinfetcher.api.APIService
import retrofit2.Call

class SFListeners(private val plugin: SFPlugin): Listener {

  private val service: APIService = this.plugin.service(APIService::class.java)

  @EventHandler(priority = 96)
  fun onLogin(event: PreLoginEvent) {
    if (!event.connection.isOnlineMode && !this.plugin.executor.addPending(event)) {
      event.setCancelReason(*TextComponent.fromLegacyText(this.plugin.tryAgainMessage))
      event.isCancelled = true
      return
    }
  }

  @EventHandler(priority = 96)
  fun onLogin(event: LoginEvent) {
    val connection: PendingConnection = event.connection
    if (connection.isOnlineMode) {
      return
    }

    val handler: InitialHandler = connection as InitialHandler
    var profile: LoginResult? = handler.loginProfile

    val uuid = this.plugin.executor.getUUID(connection.name) ?: return

    event.registerIntent(this.plugin)

    this.plugin.proxy.scheduler.runAsync(this.plugin) {
      try {
        val call: Call<JsonObject> = this.service.profile(SFPlugin.SESSION_URL.format(uuid.trim()))
        if (call.isCanceled) {
          event.completeIntent(this.plugin)
          return@runAsync
        }

        val result: JsonObject? = call.fetch()

        if (result == null) {
          event.completeIntent(this.plugin)
          return@runAsync
        }

        val properties: MutableList<LoginResult.Property?> = mutableListOf()
        result.getAsJsonArray("properties").forEach {
          it as JsonObject

          val name: String = it.get("name").asString
          val value: String = it.get("value").asString
          val signature: String = it.get("signature").asString

          properties.add(LoginResult.Property(name, value, signature))
        }

        if (profile == null) {
          profile = LoginResult(uuid.trim(), connection.name, properties.toTypedArray())
        }

        handler.inject(profile!!)
      } catch (ex: Throwable) {
        ex.printStackTrace()
      } finally {
        event.completeIntent(this.plugin)
      }
    }
  }

  private fun InitialHandler.inject(profile: LoginResult) {
    val field = this.javaClass.getDeclaredField("loginProfile")
    if (!field.isAccessible) {
      field.isAccessible = true
    }

    field.set(this, profile)
  }

}
