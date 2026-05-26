package com.example.weatherapp

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.weatherapp.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val apiKey = "58eee5be2a962e0fdd8d2de32c5d8d2c"

    private val api = Retrofit.Builder()
        .baseUrl("https://api.openweathermap.org/data/2.5/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(WeatherApi::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSearch.setOnClickListener { search() }

        binding.etCity.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                search()
                true
            } else false
        }
    }

    private fun search() {
        val city = binding.etCity.text.toString().trim()
        if (city.isEmpty()) return

        binding.progressBar.visibility = View.VISIBLE
        binding.tvError.visibility = View.GONE
        binding.cardWeather.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val data = api.getWeather(city, apiKey)
                showWeather(data)
            } catch (e: Exception) {
                binding.tvError.text = "تعذر تحميل الطقس، تحقق من اسم المدينة"
                binding.tvError.visibility = View.VISIBLE
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun showWeather(data: WeatherResponse) {
        binding.tvCity.text = data.name
        binding.tvTemp.text = "${data.main.temp.roundToInt()}°C"
        binding.tvDesc.text = data.weather[0].description
        binding.tvHumidity.text = "${data.main.humidity}%"
        binding.tvFeelsLike.text = "${data.main.feels_like.roundToInt()}°"
        binding.tvWind.text = "${data.wind.speed} م/ث"

        Glide.with(this)
            .load("https://openweathermap.org/img/wn/${data.weather[0].icon}@2x.png")
            .into(binding.ivIcon)

        binding.cardWeather.visibility = View.VISIBLE
    }
}
