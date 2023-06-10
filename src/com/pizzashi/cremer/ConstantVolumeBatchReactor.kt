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
        type: String,
        Cao: Double = 1.0,
        Cbo: Double = 1.0,
        Cdo: Double = 1.0): YailList {

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
        z: YailList,
        type: String,
        Cao: Double = 1.0,
        Cbo: Double = 1.0): YailList {
                    
        val timeValues = tools.YailListToDouble(x)
        val aVal = tools.YailListToDouble(y)
        val bVal = tools.YailListToDouble(z)
        val M = Cbo/Cao

        var kVal: Double = 0.0
        var rsq: Double = -1.0

        val listCa = mutableListOf<Double>()
        val listCb = mutableListOf<Double>()
        if (type == "Conversion") {
            for (i in aVal.indices) {
                listCa.add(Cao*(1 - aVal[i]))
                listCb.add(Cbo*(1 - bVal[i]))
            }
        }
        else if (type == "Concentration") {
            val listCa = aVal
            val listCb = bVal
        }

        val correctedY = mutableListOf<Double>()
        if (M == 2.0) {    
            for (concentration in listCa) {
                correctedY.add(1/(concentration*concentration))
            }

            val regressionResults = tools.LinearRegression(timeValues, correctedY)
            kVal = regressionResults[0]/8
            rsq = regressionResults[2]
        }
        else {
            val firstConst = (2*Cao - Cbo)/Cbo
            for (i in listCa.indices) {
                correctedY.add((firstConst*(Cbo - listCb[i])/listCb[i]) + math.ln((Cao*listCb[i])/(Cbo*listCa[i])))
            }

            val regressionResults = tools.LinearRegression(timeValues, correctedY)
            kVal = regressionResults[0]/((2*Cao - Cbo )*(2*Cao - Cbo))
            rsq = regressionResults[2]
        }

        return YailList.makeList(listOf(kVal, rsq))
    }

    /**
     *  x, y, z => time, Ca, Cb
     *  The function returns a YailList where [k, rsq]
     */
    fun ThirdOrderTrimolecularIrreversible_AB(
        x: YailList,
        y: YailList,
        z: YailList,
        type: String,
        Cao: Double = 1.0,
        Cbo: Double = 1.0): YailList {
        
        val timeValues = tools.YailListToDouble(x)
        val aVal = tools.YailListToDouble(y)
        val bVal = tools.YailListToDouble(z)
        val M = Cbo/Cao

        var kVal: Double = 0.0
        var rsq: Double = -1.0

        val listCa = mutableListOf<Double>()
        val listCb = mutableListOf<Double>()
        if (type == "Conversion") {
            for (i in aVal.indices) {
                listCa.add(Cao*(1 - aVal[i]))
                listCb.add(Cbo*(1 - bVal[i]))
            }
        }
        else if (type == "Concentration") {
            val listCa = aVal
            val listCb = bVal
        }

        val correctedY = mutableListOf<Double>()
        if (M == 1.0) {    
            for (concentration in listCa) {
                correctedY.add(1/(concentration*concentration))
            }

            val regressionResults = tools.LinearRegression(timeValues, correctedY)
            kVal = regressionResults[0]/2
            rsq = regressionResults[2]
        }
        else {
            val firstConst = (Cao - Cbo)/Cbo
            for (i in listCa.indices) {
                correctedY.add((firstConst*(Cbo - listCb[i])/listCb[i]) + math.ln((Cao*listCb[i])/(Cbo*listCa[i])))
            }

            val regressionResults = tools.LinearRegression(timeValues, correctedY)
            kVal = regressionResults[0]/((Cao - Cbo)*(Cao - Cbo))
            rsq = regressionResults[2]
        }

        return YailList.makeList(listOf(kVal, rsq))
    }

    /**
     *  x, y => time, Ca
     *  This function iterates over a large number of points from -1 to 3, as orders beyond that are uncommon.
     *  The function returns a YailList where [n, rsq, k]
     */
    fun NthOrderIrreversible(x: YailList, y: YailList, type: String, Cao: Double = 1.0): YailList {
        val timeValues = tools.YailListToDouble(x)
        val aVal = tools.YailListToDouble(y)

        var kVal: Double = 0.0
        var rsq: Double = -1.0

        val listCa = mutableListOf<Double>()
        if (type == "Conversion") {
            for (conversion in aVal) {
                listCa.add(Cao*(1 - conversion))
            }
        }
        else if (type == "Concentration") {
            val listCa = aVal
        }

        // Take note that for this rate form, n must not be equal to 1
        var highestRSQ: Double = 0.0
        var bestOrder: Double = 0.0
        var bestK: Double = 1.0

        // Start from zero because why not, may be able to set this in settings...
        var n = 0.0
        val correctedY = mutableListOf<Double>()

        while (n < 1.0) {
            for (c in listCa) {
                correctedY.add(math.pow(c, n))
            }

            var regressionResults = tools.LinearRegression(timeValues, correctedY)
            var rsq = regressionResults[2]
            var slope = regressionResults[0]

            if (rsq > 0.999) {
                highestRSQ = rsq
                bestOrder = n
                bestK = slope
                break
            }
            else if (rsq > highestRSQ) {
                highestRSQ = rsq
                bestOrder = n
                bestK = slope
            }

            n += 0.01
        }


        // Next from 1.01 to 3, feeling the heat now...
        n = 1.01
        while (n <= 3) {
            for (c in listCa) {
                correctedY.add(math.pow(c, n))
            }

            var regressionResults = tools.LinearRegression(timeValues, correctedY)
            var rsq = regressionResults[2]
            var slope = regressionResults[0]

            if (rsq > 0.999) {
                highestRSQ = rsq
                bestOrder = n
                bestK = slope
                break
            }
            else if (rsq > highestRSQ) {
                highestRSQ = rsq
                bestOrder = n
                bestK = slope
            }

            n += 0.01
        }

        // Lastly from -1 to -0.01
        n = -1.0
        while (n < 0) {
            for (c in listCa) {
                correctedY.add(math.pow(c, n))
            }

            var regressionResults = tools.LinearRegression(timeValues, correctedY)
            var rsq = regressionResults[2]
            var slope = regressionResults[0]

            if (rsq > 0.999) {
                highestRSQ = rsq
                bestOrder = n
                bestK = slope
                break
            }
            else if (rsq > highestRSQ) {
                highestRSQ = rsq
                bestOrder = n
                bestK = slope
            }

            n += 0.01
        }

        return YailList.makeList(listOf(bestOrder, highestRSQ, bestK))
    }

    // fun ZeroOrderIrreversible()

    // fun ParallelReactions()

    // fun HomogenousCatalyzedReactions()

    // fun FirstOrderReversible()

    // fun SecondOrderReversible()

    // fun ShiftingOrderReactions()
}