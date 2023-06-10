package com.pizzashi.cremer

import com.google.appinventor.components.runtime.util.YailList

class Tools {

    /**
     *  This function parses a YailList into a List<Double>.
     *  Non-doubles will be treated as zeroes.
     */
    fun YailListToDouble(yailList: YailList): List<Double> {
        val result = mutableListOf<Double>()
        var index = 1 // YailList index starts at 1
        while (index <= yailList.size) {
            result.add(yailList.get(index).toString().toDoubleOrNull() ?: 0.0)
            index++
        }
        return result
    }

    /**
     *  This function parses a YailList into a List<Int>.
     *  Non-integers will be treated as zeroes.
     */
    fun YailListToInt(yailList: YailList): List<Int> {
        val result = mutableListOf<Int>()
        var index = 1 // YailList index starts at 1
        while (index <= yailList.size) {
            result.add(yailList.get(index).toString().toIntOrNull() ?: 0)
            index++
        }
        return result
    }

    /**
     *  This function applies a linear least-squares regression for the given x and y data points.
     *  This will return a 3-item array where: [slope, y-intercept, RSQ]
     *  In the case of an error, RSQ is set to -1.
     */
    fun LinearRegression(x: List<Double>, y: List<Double>): List<Double> {
        // For an uneven number of x and y values, return an error.
        if (x.size != y.size) {
            return listOf(0.0, 0.0, -1.0)
        }

        // Load Math library
        val math = Math()

        // Set up necessary least squares sums
        val n = x.size
        val sumY: Double    = y.sumOf{it}
        val sumX: Double    = x.sumOf{it}
        val meanY: Double   = sumY/n
        val meanX: Double   = sumX/n
        val sumSqX: Double  = x.sumOf{it * it}
        val sumSqY: Double  = y.sumOf{it * it}
        var sumXY: Double   = 0.0
        for (index in x.indices) {
            sumXY += x[index] * y[index]
        }

        // Solving for the slope:
        val slope = (n*sumXY - sumX*sumY)/(n*sumSqX - (sumX*sumX))

        // Solving for the intercept:
        val intercept = meanY - slope*meanX

        // Solving for the RSQ or the coefficient of determination:
        val r = (n*sumXY - sumX*sumY)/( kotlin.math.sqrt(n*sumSqX - (sumX*sumX))*kotlin.math.sqrt(n*sumSqY - (sumY*sumY)) )
		val rsq = r * r

        return listOf(slope, intercept, rsq)
    }
}