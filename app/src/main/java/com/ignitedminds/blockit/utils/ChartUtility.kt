package com.ignitedminds.blockit.utils

import android.graphics.Typeface
import android.util.Log
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.ignitedminds.blockit.R
import com.ignitedminds.blockit.application.App.Companion.appContext

class ChartUtility {


    companion object {


        fun initializeChart(chart: BarChart, barColor: Int, usageData: HashMap<String, Long>){
            initChart(chart,usageData)
            updateChart(chart,barColor,usageData)
        }

       private fun initChart(chart: BarChart, usageData: HashMap<String, Long>) {

            val daysOfTheWeek = usageData.keys.toList()

            val xAxis = chart.xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)

                valueFormatter = object : ValueFormatter() {

                    override fun getFormattedValue(value: Float): String? {
                        Log.d("App-Limiter", "getFormattedValue: $value")
                        val x =
                            if (value.toInt() >= usageData.size) (usageData.size - 1) else value.toInt()
                        return daysOfTheWeek[x]
                    }
                }
                textSize = 14f
            }

            chart.apply {

                axisLeft.setDrawGridLines(false)
                axisRight.setDrawGridLines(false)
                description.text = ""
                axisLeft.isEnabled = false
                axisRight.isEnabled = false
                xAxis.axisLineWidth = 2f
                xAxis.axisLineColor = resources.getColor(R.color.black)
                legend.isEnabled = false
                setScaleEnabled(false)
                val customRenderer = CustomBarChartRender(this, animator, viewPortHandler)
                customRenderer.setRadius(8f)
                renderer = customRenderer
                extraBottomOffset = 5f
                animateY(500)
                axisLeft.axisMinimum = 0F
                axisRight.axisMinimum = 0F
                xAxis.granularity = 1f
                xAxis.isGranularityEnabled = true
                notifyDataSetChanged()
                invalidate()
                //setOnClickListener { clearSearchFocus(it) }
            }
        }


        private fun updateChart(chart: BarChart, barColor: Int, usageData: HashMap<String, Long>) {

            val durationList = usageData.values.toList()

            val entries = ArrayList<BarEntry>()
            for (i in 0 until usageData.size) {
                entries.add(BarEntry(i.toFloat(), durationList[i].toFloat()))
            }

            val barDataSet = BarDataSet(entries, "").apply {
                color = barColor
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return TimeUtility.millisecondsToTime(value.toLong())
                    }
                }
                valueTextSize = 12f
            }


            barDataSet.barBorderWidth = 0.2f
            barDataSet.barBorderColor = UIUtility.getColor(R.color.black)
            val barData = BarData(barDataSet)
            barData.barWidth = 0.45f
            barData.isHighlightEnabled = false
            val typeface = Typeface.createFromAsset(appContext.assets, "helvetica.ttf")
            barData.setValueTypeface(typeface)
            chart.apply {
                data = barData

                setVisibleXRangeMinimum(0f)
                setVisibleXRangeMaximum(7f)

                notifyDataSetChanged()
                invalidate()
            }
        }
    }
}