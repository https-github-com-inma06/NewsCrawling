package com.example.newscrawling.retrofit

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {
    @FormUrlEncoded
    @POST("Login.php")
    fun getLogin(@Field("USER_ID") userID: String,
                 @Field("USER_PASSWORD") userPassword: String): Call<JsonElement>

    @FormUrlEncoded
    @POST("Register.php")
    fun getRegister(@Field("USER_ID") userID: String,
                    @Field("USER_PASSWORD") userPassword: String): Call<JsonElement>

    @FormUrlEncoded
    @POST("Verify.php")
    fun getVerify(@Field("USER_ID") userID: String): Call<JsonElement>
}