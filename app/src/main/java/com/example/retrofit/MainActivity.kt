package com.example.retrofit

import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.bumptech.glide.Glide
import com.example.retrofit.api.ApiRequest
import com.example.retrofit.api.BASE_URL
import com.example.retrofit.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    lateinit var bindg: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindg = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindg.root)


        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        
        
        bacgroundAnimation()
        makeApiRequest()
        
        bindg.floatingActionButton.setOnClickListener { 
             bindg.floatingActionButton.animate().apply {
                 rotationBy(360f)
                 duration = 1000
             }.start()

            makeApiRequest()
            bindg.ivRandomDog.visibility = View.GONE
        }
    }


    private fun bacgroundAnimation() {
        val animationDrawable: AnimationDrawable = bindg.rlLayout.background as  AnimationDrawable


        animationDrawable.apply {
            setEnterFadeDuration(1000)
            setExitFadeDuration(3000)
            start()
        }
    }


    private fun makeApiRequest() {
        val api = Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build().create(ApiRequest::class.java)

        GlobalScope.launch(Dispatchers.IO){
            try {
                val response = api.getRandomDog()
                Log.d("Main", "size: ${response.fileSizeByte}")


                if (response.fileSizeByte < 400_000) {
                    withContext(Dispatchers.Main){
                        Glide.with(applicationContext).load(response.url).into(bindg.ivRandomDog)
                        bindg.ivRandomDog.visibility = View.VISIBLE
                    }
                }else{
                    makeApiRequest()
                }
            }catch (e:Exception){
                Log.e("main", "Error: ${e.message}", )
            }
        }
    }
}