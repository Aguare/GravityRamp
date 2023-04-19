package com.aguare.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.connection_screen)
        val connection = ConnectionBT(this)
        connection.getConnect()
    }

    fun changeUpdateChart(){
        val chart: BarChart = findViewById(R.id.chart)
        val left = chart.axisLeft
        val right = chart.axisRight
        right.setDrawLabels(false)

        left.setDrawAxisLine(true)
        left.setDrawGridLines(false)
        left.axisMinimum = 8f
        left.axisMaximum = 10f

        val entries = listOf(
            BarEntry(1f, 8.23f),
            BarEntry(2f, 9.39f),
            BarEntry(3f, 9.38f),
            BarEntry(4f, 8.62f),
            BarEntry(5f, 9.43f),
            BarEntry(6f, 9.82f),
            BarEntry(7f, 8.41f),
            BarEntry(8f, 9.62f),
            BarEntry(9f, 8.51f),
            BarEntry(10f, 9.99f),
        )

        val dataSet = BarDataSet(entries, "Tiempo")
        val data = BarData(dataSet)

        chart.data = data
        chart.description.isEnabled = false
        chart.setDrawValueAboveBar(false)
        chart.setDrawGridBackground(false)
        chart.animateY(2000)
        chart.invalidate()
    }
}