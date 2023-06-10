package com.pizzashi.cremer

import com.google.appinventor.components.runtime.util.YailList

class ConstantVolumeBatchReactor {
    val tools = Tools()
    val math = Math()

    /**
     *  x is time and y corresponds to either Ca or Xa
     *  The function returns a YailList where [k, rsq]
     */
    fun FirstOrderIrreversible(x: YailList, y: YailList, type: String): YailList {
        val timeValues = tools.YailListToDouble(x)
        val yValues = tools.YailListToDouble(y)
        var kVal: Double = 0.0
        var rsq: Double = -1.0

        if (type == "Conversion") {
            // correctedY refers to -ln(1 - Xa)
            val correctedY = mutableListOf<Double>()
            for (conversion in yValues) {
                correctedY.add(-math.ln(1 - conversion))
            }
            
            val regressionResults = tools.LinearRegression(timeValues, correctedY)
            kVal = regressionResults[0]
            rsq = regressionResults[2]
        }
        else if (type == "Concentration") {
            val correctedY = mutableListOf<Double>()
            for (concentration in yValues) {
                correctedY.add(-math.ln(concentration))
            }

            val regressionResults = tools.LinearRegression(timeValues, correctedY)
            kVal = regressionResults[0]
            rsq = regressionResults[2]
        }

        return YailList.makeList(listOf(kVal, rsq))
    }

    /**
     *  x is time and y corresponds to either Ca or Xa
     *  The function returns a YailList where [k, rsq]
     */
    fun SecondOrderBimolecularIrreversible_2A(x: YailList, y: YailList, type: String, Cao: Double = 1.0): YailList {
        val timeValues = tools.YailListToDouble(x)
        val yValues = tools.YailListToDouble(y)
        var kVal: Double = 0.0
        var rsq: Double = -1.0

        if (type == "Conversion") {
            // correctedY refers to Xa/(1-Xa)
            val correctedY = mutableListOf<Double>()
            for (conversion in yValues) {
                correctedY.add(conversion/(1 - conversion))
            }

            val regressionResults = tools.LinearRegression(timeValues, correctedY)
            kVal = regressionResults[0]/Cao
            rsq = regressionResults[2]
        }
        else if (type == "Concentration") {
            // correctedY refers to 1/Ca
            val correctedY = mutableListOf<Double>()
            for (concentration in yValues) {
                correctedY.add(1/concentration)
            }

            val regressionResults = tools.LinearRegression(timeValues, correctedY)
            kVal = regressionResults[0]
            rsq = regressionResults[2]
        }

        return YailList.makeList(listOf(kVal, rsq))
    }

    /**
     *  x is time and y corresponds to either Ca or Xa
     *  The function returns a YailList where [k, rsq]
     */
    fun SecondOrderBimolecularIrreversible_AB(x: YailList, y: YailList, type: String, Cao: Double = 1.0, Cbo: Double = 1.0): YailList {
        val M = Cbo/Cao
        val timeValues = tools.YailListToDouble(x)
        val yValues = tools.YailListToDouble(y)

        // correctedY refers to (M - Xa)/M*(1 - Xa)
        val correctedY = mutableListOf<Double>()

        if (type == "Conversion") {
            for (conv in yValues) {
                correctedY.add((M - conv)/(M * (1 - conv)))
            }
        } else if (type == "Concentration") {
            for (conc in yValues) {
                var conv = 1 - conc/Cao
                correctedY.add((M - conv)/(M * (1 - conv)))
            }
        }

        val regressionResults = tools.LinearRegression(timeValues, correctedY)

        return YailList.makeList(listOf(regressionResults[0]/(Cao * (M - 1)), regressionResults[2])) 
    }

    /**
     *  x, y, z , t => time, Ca, Cb, Cd
     *  The function returns a YailList where [k, rsq]
     */
    fun ThirdOrderTrimolecularIrreversible_ABD(
        x: YailList,
        y: YailList,
        z: YailList,
        t: YailList,
        type: String
        Cao: Double = 1.0,
        Cbo: Double = 1.0,
        Cdo: Double = 1.0) {

        val timeValues = tools.YailListToDouble(x)
        val aVal = tools.YailListToDouble(y)
        val bVal = tools.YailListToDouble(z)
        val dVal = tools.YailListToDouble(t)
        
        val firstConst = 1/((Cao - Cbo)*(Cao - Cdo))
        val secondConst = 1/((Cbo - Cdo)*(Cbo - Cao))
        val thirdConst = 1/((Cdo - Cao)*(Cdo - Cbo))

        var kVal: Double = 0.0
        var rsq: Double = -1.0

        if (type == "Concentration") {
            // correctedY refers to firstConst*lnCa + secondConst*lnCb + thirdConst*lnCc
            val correctedY = mutableListOf<Double>()

            var index = 0
            while (index < timeValues.size) {
                correctedY.add(firstConst*math.ln(aVal[index]) + secondConst*math.ln(bVal[index]) + thirdConst*math.ln(dVal[index]))
            }

            val regressionResults = tools.LinearRegression(timeValues, correctedY)
            kVal = -regressionResults[0]
            rsq = regressionResults[2]
        }
        else if (type == "Conversion") {
            // correctedY refers to firstConst*ln[Cao(1-Xa)] + secondConst*ln[Cbo(1-Xb)] + thirdConst*ln[Cco(1-Xc)]
            val correctedY = mutableListOf<Double>()
            
            var index = 0
            while (index < timeValues.size) {
                correctedY.add(firstConst*math.ln(Cao*(1 - aVal[index])) + secondConst*math.ln(Cbo*(1 - bVal[index])) + thirdConst*math.ln(Cdo*(1 - dVal[index])))
            }

            val regressionResults = tools.LinearRegression(timeValues, correctedY)
            kVal = -regressionResults[0]
            rsq = regressionResults[2]
        }

        return YailList.makeList(listOf(kVal, rsq))
    }

    /**
     *  x, y, z => time, Ca, Cb
     *  The function returns a YailList where [k, rsq]
     */
    fun ThirdOrderTrimolecularIrreversible_A2B(
        x: YailList,
        y: YailList,
        z: YailList
        type: String,
        Cao: Double = 1.0,
        Cbo: Double = 1.0) {
                    
        val timeValues = tools.YailListToDouble(x)
        val aVal = tools.YailListToDouble(y)
        val bVal = tools.YailListToDouble(z)
        val M = Cbo/Cao

        if (type == "Conversion") {
            for (i in aVal.indices) {
                val listCa = mutableListOf<Double>()
                val listCb = mutableListOf<Double>()
                listCa.add(aVal[i])
                listCb.add(bVal[i])
            }
        }
        else if (type == "Concentration") {
            val listCa = aVal
            val listCb = bVal
        }

        if (M == 2) {
            val correctedY = 
        }

    }

    // fun ThirdOrderTrimolecularIrreversible_AB()

    // fun NthOrderIrreversible()

    // fun ZeroOrderIrreversible()

    // fun ParallelReactions()

    // fun HomogenousCatalyzedReactions()

    // fun FirstOrderReversible()

    // fun SecondOrderReversible()

    // fun ShiftingOrderReactions()
}