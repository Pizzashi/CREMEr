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
        var rsq: Double = 0.0

        val correctedY = mutableListOf<Double>()
        if (type == "Concentration") {
            // correctedY refers to firstConst*lnCa + secondConst*lnCb + thirdConst*lnCc
            for (i in timeValues.indices) {
                correctedY.add(firstConst*math.ln(aVal[i]) + secondConst*math.ln(bVal[i]) + thirdConst*math.ln(dVal[i]))
            }

            val regressionResults = tools.LinearRegression(timeValues, correctedY)
            kVal = -regressionResults[0]
            rsq = regressionResults[2]
        }
        else if (type == "Conversion") {
            // correctedY refers to firstConst*ln[Cao(1-Xa)] + secondConst*ln[Cbo(1-Xb)] + thirdConst*ln[Cco(1-Xc)]
            for (i in timeValues.indices) {
                correctedY.add(firstConst*math.ln(Cao*(1 - aVal[i])) + secondConst*math.ln(Cbo*(1 - bVal[i])) + thirdConst*math.ln(Cdo*(1 - dVal[i])))
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
            for (i in aVal.indices) {
                listCa.add(aVal[i])
                listCb.add(bVal[i])
            }
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
            for (i in aVal.indices) {
                listCa.add(aVal[i])
                listCb.add(bVal[i])
            }
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

        val listCa = mutableListOf<Double>()
        if (type == "Conversion") {
            for (conversion in aVal) {
                listCa.add(Cao*(1 - conversion))
            }
        }
        else if (type == "Concentration") {
            for (conc in aVal) {
                listCa.add(conc)
            }
        }

        // Take note that for this rate form, n must not be equal to 1
        var highestRSQ: Double = -1.0
        var bestOrder: Double = 0.0
        var bestK: Double = 0.0

        
        val correctedY = mutableListOf<Double>()

        // Start from zero because why not, may be able to set this in settings...
        var n = 0.0
        while (n < 1.0) {
            correctedY.clear()

            for (c in listCa) {
                correctedY.add(math.pow(c, (1 - n)))
            }

            var regressionResults = tools.LinearRegression(timeValues, correctedY)
            var rsq = regressionResults[2]
            var slope = regressionResults[0]
            var newK = slope/(n - 1)

            if (rsq > 0.999 && newK > 0) {
                highestRSQ = rsq
                bestOrder = n
                bestK = newK
                break
            }
            else if (rsq > highestRSQ && newK > 0) {
                highestRSQ = rsq
                bestOrder = n
                bestK = newK
            }

            n += 0.01
        }

        // Next from 1.01 to 3, feeling the heat now...
        n = 1.01
        while (n <= 3) {
            for (c in listCa) {
                correctedY.add(math.pow(c, (1 - n)))
            }

            var regressionResults = tools.LinearRegression(timeValues, correctedY)
            var rsq = regressionResults[2]
            var slope = regressionResults[0]
            var newK = slope/(n - 1)

            if (rsq > 0.999 && newK > 0) {
                highestRSQ = rsq
                bestOrder = n
                bestK = slope
                break
            }
            else if (rsq > highestRSQ && newK > 0) {
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
                correctedY.add(math.pow(c, (1 - n)))
            }

            var regressionResults = tools.LinearRegression(timeValues, correctedY)
            var rsq = regressionResults[2]
            var slope = regressionResults[0]
            var newK = slope/(n - 1)

            if (rsq > 0.999 && newK > 0) {
                highestRSQ = rsq
                bestOrder = n
                bestK = slope
                break
            }
            else if (rsq > highestRSQ && newK > 0) {
                highestRSQ = rsq
                bestOrder = n
                bestK = slope
            }

            n += 0.01
        }

        return YailList.makeList(listOf(bestOrder, highestRSQ, bestK))
    }

    /**
     *  x is time and y corresponds to either Ca or Xa
     *  The function returns a YailList where [k, rsq]
     */
    fun ZeroOrderIrreversible(x: YailList, y: YailList, type: String, Cao: Double = 1.0): YailList {
        val timeValues = tools.YailListToDouble(x)
        val yValues = tools.YailListToDouble(y)
        var kVal: Double = 0.0
        var rsq: Double = -1.0

        val correctedY = mutableListOf<Double>()
        if (type == "Conversion") {
            val regressionResults = tools.LinearRegression(timeValues, yValues)
            kVal = -regressionResults[0]/Cao
            rsq = regressionResults[2]
        }
        else if (type == "Concentration") {
            val regressionResults = tools.LinearRegression(timeValues, yValues)
            kVal = -regressionResults[0]
            rsq = regressionResults[2]
        }

        return YailList.makeList(listOf(kVal, rsq))
    }

    /**
     *  x, y, z, t => t, a, r, s
     *  The function returns a YailList where [k1, k2, rsq]
     */
    fun ParallelReactions(x: YailList, y: YailList, z: YailList, t: YailList, Cro: Double = 1.0, Cso: Double = 1.0): YailList {
        val timeVal = tools.YailListToDouble(x)
        val aVal = tools.YailListToDouble(y)
        val rVal = tools.YailListToDouble(z)
        val sVal = tools.YailListToDouble(t)
        var rsq: Double = -1.0

        val correctedY = mutableListOf<Double>()
        for (conc in aVal) {
            correctedY.add(-math.ln(conc))
        }
        val kSumRegressionResults = tools.LinearRegression(timeVal, correctedY)
        // (k1 + k2)
        val kSum = kSumRegressionResults[0]
        rsq = kSumRegressionResults[2]

        val kRatioRegressionResults = tools.LinearRegression(sVal, rVal)
        // k1/k2
        val kRatio = kRatioRegressionResults[0]

        val k2 = kSum/(kRatio + 1)
        val k1 = kRatio*k2

        return YailList.makeList(listOf(k1, k2, rsq))
    }

    /**
     *  x, y => t, a
     *  The function returns a YailList where [k, rsq]
     */
    fun AutocatalyticReactions(x: YailList, y: YailList, type: String, Cao: Double = 1.0, Cro: Double = 1.0): YailList {
        val timeVal = tools.YailListToDouble(x)
        val aVal = tools.YailListToDouble(y)
        val kVal: Double
        var rsq: Double = -1.0
        val M: Double = Cro/Cao

        val conversionList = mutableListOf<Double>()
        if (type == "Concentration") {
            for (conc in aVal) {
                conversionList.add(1 - conc/Cao)
            }
        } else if (type == "Conversion") {
            for (conv in aVal) {
                conversionList.add(conv)
            }
        }

        val correctedY = mutableListOf<Double>()
        for (conv in conversionList) {
            correctedY.add(math.ln((M + conv)/(M*(1 - conv))))
        }

        val regressionResults = tools.LinearRegression(timeVal, correctedY)
        kVal = regressionResults[0]/(Cao + Cro)
        rsq = regressionResults[2]

        return YailList.makeList(listOf(kVal, rsq))
    }

    /**
     *  x, y => t, a
     *  The function returns a YailList where [k1, k2, rsq]
     */
    fun ShiftingOrderReactions(x: YailList, y: YailList, type: String, Cao: Double = 1.0): YailList {
        val timeVal = tools.YailListToDouble(x)
        val aVal = tools.YailListToDouble(y)
        var rsq: Double = -1.0

        val concA = mutableListOf<Double>()
        if (type == "Conversion") {
            for (conv in aVal) {
                concA.add(Cao*(1 - conv))
            }
        } else if (type == "Concentration") {
            for (conc in aVal) {
                concA.add(conc)
            }
        }

        val correctedY = mutableListOf<Double>()
        val correctedX = mutableListOf<Double>()

        for (i in timeVal.indices) {
            correctedX.add(timeVal[i]/(Cao - concA[i]))
        }

        for (conc in concA) {
            correctedY.add((math.ln(Cao/conc))/(Cao - conc))
        }

        val regressionResults = tools.LinearRegression(correctedX, correctedY)
        val k1 = regressionResults[0]
        val k2 = -regressionResults[1]
        rsq = regressionResults[2]

        return YailList.makeList(listOf(k1, k2, rsq))
    }

    // fun FirstOrderReversible()

    // fun SecondOrderReversible()
}