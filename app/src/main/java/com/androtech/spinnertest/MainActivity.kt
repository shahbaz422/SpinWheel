package com.androtech.spinnertest

import android.R
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.androtech.spinnertest.custom.spinner.model.SpinWheelItemSectionModel
import com.androtech.spinnertest.databinding.ActivityMainBinding
import kotlin.random.Random

/**
 * Created by mohammed shahbaz on 17/04/24.
 */

class MainActivity : AppCompatActivity() {
    private lateinit var binding:  ActivityMainBinding
    private var data: ArrayList<SpinWheelItemSectionModel> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setMyTitleColor()
        setSpinner()
    }

    private fun setMyTitleColor() {
        window.statusBarColor = ContextCompat.getColor(this, R.color.holo_red_dark)
    }

    private fun setSpinner() {
        val luckySpinnerView = binding.luckySpinnerView

        val colors = listOf("#D32F2F", "#eb5a4f")
        val colorCount = colors.size
        for (i in 1..12) {
            val spinWheelItemSectionModel =
                SpinWheelItemSectionModel()
            spinWheelItemSectionModel.topText = i.toString()
            spinWheelItemSectionModel.secondaryText = if (i % 2 == 0) "Yes" else "No"
            val colorIndex = (i - 1) % colorCount
            spinWheelItemSectionModel.color = Color.parseColor(colors[colorIndex])
            data.add(spinWheelItemSectionModel)
        }



        luckySpinnerView.setData(data)
        luckySpinnerView.setRound(5)

        //before every spin, you need to provide the target index where you want your spinner to be stopped.
        binding.play.setOnClickListener {
            val index: Int = getRandomIndex()
            luckySpinnerView.startLuckyWheelWithTargetIndex(index)
        }

        //you will get the value of result in this callback.
        luckySpinnerView.setLuckyRoundItemSelectedListener { index ->
            Toast.makeText(
                applicationContext,
                "You got index ${data[index].topText}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun getRandomIndex(): Int {
        val rand = Random
        return rand.nextInt(data.size - 1) + 0
    }
}