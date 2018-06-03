package pl.kacperduras.skinfetcher.api

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import net.md_5.bungee.api.event.PreLoginEvent
import pl.kacperduras.skinfetcher.SFPlugin
import pl.kacperduras.skinfetcher.fetch
import pl.kacperduras.skinfetcher.toUUID
import retrofit2.Call
import java.util.*

class APIExecutor(private val plugin: SFPlugin): Runnable {

  private val service: APIService = this.plugin.service(APIService::class.java)
  private val pendings: MutableList<PreLoginEvent> = mutableListOf()
  private val results: MutableMap<String, UUID> = mutableMapOf()

  override fun run() {
    if (pendings.size == 0) {
      return
    }

    val request = JsonArray()
    pendings.forEach { request.add(it.connection.name) }

    val call: Call<JsonArray> = this.service.uuid(request)
    if (call.isCanceled) {
      return
    }

    val result: JsonArray = call.fetch() ?: return
    results.clear()

    result.forEach {
      it as JsonObject

      val name = it.get("name").asString
      val id = it.get("id").asString.toUUID()

      results[name] = id
    }

    pendings.forEach { it.completeIntent(this.plugin) }
    pendings.clear()
  }

  fun addPending(event: PreLoginEvent): Boolean {
    if (this.pendings.size < 100) {
      return this.pendings.add(event)
    }

    return false
  }

  fun getUUID(name: String): UUID? {
    return this.results[name]
  }

}
