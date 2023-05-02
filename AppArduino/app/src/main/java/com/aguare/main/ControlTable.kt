package com.aguare.main

import android.view.Gravity
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter

class ControlTable(var bundle: MainActivity) {

    //Charts 15
    private var distance_time_15: BarChart
    private lateinit var speed_time_15: BarChart
    private lateinit var ac_time_15: BarChart

    //Charts 30
    private lateinit var distance_time_30: BarChart
    private lateinit var speed_time_30: BarChart
    private lateinit var ac_time_30: BarChart

    //Charts 45
    private lateinit var distance_time_45: BarChart
    private lateinit var speed_time_45: BarChart
    private lateinit var ac_time_45: BarChart

    //Tables
    private lateinit var table_15: TableLayout
    private lateinit var table_30: TableLayout
    private lateinit var table_45: TableLayout

    //Distance in cm
    private final var distance = 50

    //15
    private var time_15 = ArrayList<Float>()
    private var speed_15 = ArrayList<Float>()
    private var ac_15 = ArrayList<Float>()

    //30
    private var time_30 = ArrayList<Float>()
    private var speed_30 = ArrayList<Float>()
    private var ac_30 = ArrayList<Float>()

    //45
    private var time_45 = ArrayList<Float>()
    private var speed_45 = ArrayList<Float>()
    private var ac_45 = ArrayList<Float>()

    init {
        this.table_15 = this.bundle.findViewById<TableLayout>(R.id.table_15)
        this.table_30 = this.bundle.findViewById<TableLayout>(R.id.table_30)
        this.table_45 = this.bundle.findViewById<TableLayout>(R.id.table_45)
        this.distance_time_15 = this.bundle.findViewById(R.id.distance_time_15)
        this.speed_time_15 = this.bundle.findViewById(R.id.speed_time_15)
        this.ac_time_15 = this.bundle.findViewById(R.id.ac_time_15)
        this.distance_time_30 = this.bundle.findViewById(R.id.distance_time_30)
        this.speed_time_30 = this.bundle.findViewById(R.id.speed_time_30)
        this.ac_time_30 = this.bundle.findViewById(R.id.ac_time_30)
        this.distance_time_45 = this.bundle.findViewById(R.id.distance_time_45)
        this.speed_time_45 = this.bundle.findViewById(R.id.speed_time_45)
        this.ac_time_45 = this.bundle.findViewById(R.id.ac_time_45)
    }

    fun entryTime(time: Float, angle: Int): Boolean {
        when (angle) {
            15 -> {
                time_15.add(time)
                calculate_list(time_15, speed_15, ac_15)
                addTableRow(table_15, time_15, speed_15, ac_15)
                generateGraphics15()
                return true
            }
            30 -> {
                time_30.add(time)
                calculate_list(time_30, speed_30, ac_30)
                addTableRow(table_30, time_30, speed_30, ac_30)
                generateGraphics30()
                return true
            }
            45 -> {
                time_45.add(time)
                calculate_list(time_45, speed_45, ac_45)
                addTableRow(table_45, time_45, speed_45, ac_45)
                generateGraphics45()
                return true
            }
        }
        return false
    }

    private fun addTableRow(
        table: TableLayout,
        time: ArrayList<Float>,
        speed: ArrayList<Float>,
        ac: ArrayList<Float>
    ) {
        clearTable(table)
        time.forEachIndexed { index, num ->
            val tableRow = TableRow(this.bundle)
            val text1 = TextView(this.bundle)
            text1.text = (index + 1).toString()
            text1.gravity = Gravity.CENTER_HORIZONTAL
            tableRow.addView(text1)

            val text2 = TextView(this.bundle)
            text2.text = "50 cm"
            text2.gravity = Gravity.CENTER_HORIZONTAL
            tableRow.addView(text2)

            val text3 = TextView(this.bundle)
            text3.text = String.format("%.2f", num) + " s"
            text3.gravity = Gravity.CENTER_HORIZONTAL
            tableRow.addView(text3)

            val text4 = TextView(this.bundle)
            text4.text = String.format("%.2f", speed.get(index)) + " cm/s"
            text4.gravity = Gravity.CENTER_HORIZONTAL
            tableRow.addView(text4)

            val text5 = TextView(this.bundle)
            text5.text = String.format("%.2f", ac.get(index)) + " cm/s²"
            text5.gravity = Gravity.CENTER_HORIZONTAL
            tableRow.addView(text5)

            table.addView(tableRow)
        }
    }

    private fun clearTable(table: TableLayout) {
        while (table.childCount > 1) {
            table.removeViewAt(1)
        }
    }

    private fun calculate_list(
        time_list: ArrayList<Float>,
        speed_list: ArrayList<Float>,
        ac_list: ArrayList<Float>
    ) {
        time_list.forEach { time ->
            val speed = distance / time
            val acceleration = speed / time

            speed_list.add(speed)
            ac_list.add(acceleration)
        }
    }

    private fun generateGraphics15() {
        var dis = ArrayList<Float>()
        generatedGraphic(time_15, dis, distance_time_15, "DISTANCIA VS TIEMPO", true)
        generatedGraphic(time_15, speed_15, speed_time_15, "VELOCIDAD VS TIEMPO", false)
        generatedGraphic(time_15, ac_15, ac_time_15, "ACELERACIÓN VS TIEMPO", false)
    }

    private fun generateGraphics30() {
        var dis = ArrayList<Float>()
        generatedGraphic(time_30, dis, distance_time_30, "DISTANCIA VS TIEMPO", true)
        generatedGraphic(time_30, speed_30, speed_time_30, "VELOCIDAD VS TIEMPO", false)
        generatedGraphic(time_30, ac_30, ac_time_30, "ACELERACIÓN VS TIEMPO", false)
    }

    private fun generateGraphics45() {
        var dis = ArrayList<Float>()
        generatedGraphic(time_45, dis, distance_time_45, "DISTANCIA VS TIEMPO", true)
        generatedGraphic(time_45, speed_45, speed_time_45, "VELOCIDAD VS TIEMPO", false)
        generatedGraphic(time_45, ac_45, ac_time_45, "ACELERACIÓN VS TIEMPO", false)
    }

    private fun getMin(list: ArrayList<Float>): Float{
        if (list.isEmpty()) {
            return 0f
        }
        var minimum = list[0]
        for (value in list) {
            if (value < minimum) {
                minimum = value
            }
        }
        return minimum
    }

    fun getMax(array: ArrayList<Float>): Float {
        if (array.isEmpty()) {
            return 0f
        }
        var max = array[0]
        for (num in array) {
            if (num > max) {
                max = num
            }
        }
        return max
    }

    private fun generatedGraphic(
        axisx: ArrayList<Float>,
        axisy: ArrayList<Float>,
        chart: BarChart,
        title: String,
        isDistanceTime: Boolean
    ) {
        val xaxis = chart.xAxis
        var barWidth = 0.4f / axisx.size
        xaxis.labelCount = 10
        if (isDistanceTime) {
            barWidth = 0.75f
        }else{
            xaxis.axisMinimum = getMin(axisx) - 0.2f
            xaxis.axisMaximum = getMax(axisx) + 0.2f
        }

        val left = chart.axisLeft
        val right = chart.axisRight

        xaxis.granularity = 0.1f
        right.setDrawLabels(false)

        left.setDrawAxisLine(true)
        left.setDrawGridLines(false)

        val entries = axisx.mapIndexed { index, number ->
            if (isDistanceTime) {
                BarEntry((index + 1).toFloat(), axisx.get(index))
            } else {
                BarEntry(number, axisy.get(index))
            }
        }

        val dataSet = BarDataSet(entries, "Tiempo")
        val data = BarData(dataSet)

        val xValueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return value.toInt().toString()
            }
        }

        data.barWidth = barWidth
        chart.description.text = title
        chart.data = data
        xaxis.position = XAxis.XAxisPosition.BOTTOM
        //xaxis.valueFormatter = xValueFormatter

        chart.setDrawValueAboveBar(false)
        chart.setDrawGridBackground(false)
        chart.animateY(2000)
        chart.invalidate()
    }

}