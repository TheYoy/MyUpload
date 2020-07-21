package com.example.myupload

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiInterface {

    @Multipart
    @POST("uploads")
    fun postImageNodeJS(
            @Part image: MultipartBody.Part,
            @Part("username") username: RequestBody,
            @Part("password") password: RequestBody): Call<ResponseBody>

    @Multipart
    @POST("uploads/up.php")
    fun postImagePHP(
            @Part image: MultipartBody.Part,
            @Part("username") username: RequestBody,
            @Part("password") password: RequestBody): Call<ResponseBody>

    companion object Factory {

        private val BASE_URL = "http://192.168.0.126:3000/"

        var retrofit: Retrofit? = null

        fun getClient(): ApiInterface {

            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .build()
            }

            return retrofit!!.create(ApiInterface::class.java)
        }
    }
}

