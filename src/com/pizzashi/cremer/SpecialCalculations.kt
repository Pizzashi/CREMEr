package com.pizzashi.cremer

import com.google.appinventor.components.runtime.util.YailList

class SpecialCalculations {
    val tools = Tools()
    val math = Math()

    /**
     *  x is k and y corresponds to T
     *  The function returns a YailList where [E/R, rsq]
     */
    fun ArrheniusEquation(x: YailList, y: YailList): YailList {
        val kValues = tools.YailListToDouble(x)
        val tempValues = tools.YailListToDouble(y)
        var rsq: Double = -1.0

        val correctedX = mutableListOf<Double>()
        for (temp in tempValues) {
            correctedX.add(1/temp)
        }

        val correctedY = mutableListOf<Double>()
        for (k in kValues) {
            correctedY.add(math.ln(k))
        }

        val regressionResults = tools.LinearRegression(correctedX, correctedY)
        val activationEnergy = -regressionResults[0]
        rsq = regressionResults[2]
    
        return YailList.makeList(listOf(activationEnergy, rsq))
    }
}