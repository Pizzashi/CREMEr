package com.pizzashi.cremer

import com.google.appinventor.components.annotations.SimpleFunction
import com.google.appinventor.components.runtime.AndroidNonvisibleComponent
import com.google.appinventor.components.runtime.ComponentContainer
import com.google.appinventor.components.runtime.util.YailList

class Cremer(container: ComponentContainer) : AndroidNonvisibleComponent(container.`$form`()) {

    val math = Math()
    val cvbr = ConstantVolumeBatchReactor()

    @SimpleFunction(description = "Solves for 1st order irreversible reaction.")
    fun FirstOrderIrreversible(time: YailList, a: YailList, type: String): YailList =
        cvbr.FirstOrderIrreversible(time, a, type)

    @SimpleFunction(description = "Solves for 2nd order irreversible reaction in the form of 2A -> R.")
    fun SecondOrderIrreversible_2A(time: YailList, a: YailList, type: String, Cao: Double): YailList =
        cvbr.SecondOrderBimolecularIrreversible_2A(time, a, type, Cao)

    @SimpleFunction(description = "Solves for 2nd order irreversible reaction in the form of A + B -> R.")
    fun SecondOrderIrreversible_AB(time: YailList, a: YailList, type: String, Cao: Double, Cbo: Double): YailList =
        cvbr.SecondOrderBimolecularIrreversible_AB(time, a, type, Cao, Cbo)

    @SimpleFunction(description = "Solves for 3rd order irreversible reaction in the form of A + B + D -> R.")
    fun ThirdOrderTrimolecularIrreversible_ABD(time: YailList, a: YailList, b: YailList, d: YailList, type: String, Cao: Double, Cbo: Double, Cdo: Double) =
        cvbr.ThirdOrderTrimolecularIrreversible_ABD(time, a, b, d, type, Cao, Cbo, Cdo)

    @SimpleFunction(description = "Solves for 3rd order irreversible reaction in the form of A + 2B -> R.")
    fun ThirdOrderTrimolecularIrreversible_A2B(time: YailList, a: YailList, b: YailList, type: String, Cao: Double, Cbo: Double) =
        cvbr.ThirdOrderTrimolecularIrreversible_A2B(time, a, b, type, Cao, Cbo)

    @SimpleFunction(description = "Solves for 3rd order irreversible reaction in the form of A + B -> R.")
    fun ThirdOrderTrimolecularIrreversible_AB(time: YailList, a: YailList, b: YailList, type: String, Cao: Double, Cbo: Double) =
        cvbr.ThirdOrderTrimolecularIrreversible_AB(time, a, b, type, Cao, Cbo)

    @SimpleFunction(description = "Determines the best order of a reaction.")
    fun NthOrderIrreversible(time: YailList, a: YailList, type: String, Cao: Double) =
        cvbr.NthOrderIrreversible(time, a, type, Cao)
}
