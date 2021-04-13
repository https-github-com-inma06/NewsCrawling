package com.example.newscrawling.retrofit

import android.util.Log
import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.Response

class RetrofitManager {

    val TAG: String = "로그"
    companion object {
        val instanse = RetrofitManager()
    }

    val httpCall: ApiService? = RetrofitClient.getClient("http://3.34.61.169/")
        ?.create(ApiService::class.java)

    fun getLogin(userID : String, userPassword : String, completion: (Response<JsonElement>) -> Unit) {
        val call = httpCall?.getLogin(userID, userPassword)
        call?.enqueue(object : retrofit2.Callback<JsonElement> {
            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                Log.d(TAG, "RetrofitManager - onFailure() called / t: $t")
            }
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                completion(response)
            }
        })
    }

    fun getRegister(userID : String, userPassword : String, completion: (Response<JsonElement>) -> Unit) {
        val call = httpCall?.getRegister(userID, userPassword)
        call?.enqueue(object : retrofit2.Callback<JsonElement> {
            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                Log.d(TAG, "RetrofitManager - onFailure() called / t: $t")
            }
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                completion(response)
            }
        })
    }

    fun getVerify(userID : String, completion: (Response<JsonElement>) -> Unit) {
        val call = httpCall?.getVerify(userID)
        call?.enqueue(object : retrofit2.Callback<JsonElement> {
            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                Log.d(TAG, "RetrofitManager - onFailure() called / t: $t")
            }
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                completion(response)
            }
        })
    }
}