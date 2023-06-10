package com.pizzashi.cremer

import com.google.appinventor.components.annotations.SimpleFunction
import com.google.appinventor.components.runtime.AndroidNonvisibleComponent
import com.google.appinventor.components.runtime.ComponentContainer
import com.google.appinventor.components.runtime.util.YailList

class Cremer(container: ComponentContainer) : AndroidNonvisibleComponent(container.`$form`()) {

    val math = Math()
    val cvbr = ConstantVolumeBatchReactor()

    @SimpleFunction(description = "Returns the sum of the given list of integers.")
    fun SumAll(integers: YailList): Int {
        val integersString = mutableListOf<String>()
        for (item in integers) {
            integersString.add(item.toString()) ?: 0
        }

        return integersString.get(1).toIntOrNull() ?: 0
        // return integers.sumOf {
        //   it.toString().toIntOrNull() ?: 0
        // }
    }

    @SimpleFunction(description = "LETS FUCKING GOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO")
    fun HelloWord(): String {
        val tools = Tools()

        return math.ln(45.45).toString()
    }

    @SimpleFunction(description = "Test Function")
    fun TestRegs(inputx: YailList, inputy: YailList): YailList {
        val tools = Tools()
        
        val result = tools.LinearRegression(tools.YailListToDouble(inputx), tools.YailListToDouble(inputy))

        return YailList.makeList(result)
    }

    @SimpleFunction()
    fun GetLength(inpuuttt: YailList): Int {
        return inpuuttt.get(0).toString().toIntOrNull() ?: 0
    }


    @SimpleFunction(description = "Solves for 1st order irreversible reaction.")
    fun FirstOrderIrreversible(x: YailList, y: YailList, type: String): YailList = cvbr.FirstOrderIrreversible(x, y, type)

    @SimpleFunction(description = "Solves for 2nd order irreversible reaction in the form of 2A -> R.")
    fun SecondOrderIrreversible_2A(x: YailList, y: YailList, type: String, Cao: Double): YailList = cvbr.SecondOrderBimolecularIrreversible_2A(x, y, type, Cao)

    @SimpleFunction(description = "Solves for 2nd order irreversible reaction in the form of A + B -> R.")
    fun SecondOrderIrreversible_AB(x: YailList, y: YailList, type: String, Cao: Double, Cbo: Double): YailList = cvbr.SecondOrderBimolecularIrreversible_AB(x, y, type, Cao, Cbo)
}
