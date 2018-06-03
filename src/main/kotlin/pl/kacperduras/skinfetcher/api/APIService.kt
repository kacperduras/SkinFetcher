package pl.kacperduras.skinfetcher.api

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url

interface APIService {

  @GET
  fun profile(@Url url: String): Call<JsonObject>

  @POST("/profiles/minecraft")
  fun uuid(@Body body: JsonArray): Call<JsonArray>

}
