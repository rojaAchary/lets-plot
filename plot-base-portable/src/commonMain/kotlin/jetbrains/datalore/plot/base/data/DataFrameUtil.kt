/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.data

import jetbrains.datalore.base.gcommon.collect.Ordering
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.base.stat.Stats
import kotlin.jvm.JvmOverloads

object DataFrameUtil {
    fun transformVarFor(aes: Aes<*>): DataFrame.Variable {
        return TransformVar.forAes(aes)
    }

    fun applyTransform(data: DataFrame, `var`: DataFrame.Variable, aes: Aes<*>, scale: Scale<*>): DataFrame {
        val transformVar = transformVarFor(aes)
        return applyTransform(data, `var`, transformVar, scale)
    }

    private fun applyTransform(
        data: DataFrame,
        variable: DataFrame.Variable,
        transformVar: DataFrame.Variable,
        scale: Scale<*>
    ): DataFrame {
//        val transformSource = getTransformSource(data, variable, scale)
//        val transformResult = scale.transform.apply(transformSource)

        var transformed = scale.applyTransform(
            data[variable],
            checkLimits = true
        )
        return data.builder()
            .putNumeric(transformVar, transformed)
            .build()
    }

//    private fun getTransformSource(data: DataFrame, variable: DataFrame.Variable, scale: Scale<*>): List<*> {
//        var transformSource = data[variable]
//        return ScaleUtil.cleanUpTransformSource(transformSource, scale)
//    }

    fun hasVariable(data: DataFrame, varName: String): Boolean {
        for (`var` in data.variables()) {
            if (varName == `var`.name) {
                return true
            }
        }
        return false
    }

    fun findVariableOrFail(data: DataFrame, varName: String): DataFrame.Variable {
        for (variable in data.variables()) {
            if (varName == variable.name) {
                return variable
            }
        }
        throw IllegalArgumentException(
            "Variable not found: '$varName'. Variables in data frame: ${
                data.variables().map { "'${it.name}'" }
            }"
        )
    }

    fun isNumeric(data: DataFrame, varName: String): Boolean {
        return data.isNumeric(findVariableOrFail(data, varName))
    }

    fun sortedCopy(variables: Iterable<DataFrame.Variable>): List<DataFrame.Variable> {
        val ordering = Ordering.from(Comparator<DataFrame.Variable> { o1, o2 -> o1.name.compareTo(o2.name) })
        return ordering.sortedCopy(variables)
    }

    fun variables(df: DataFrame): Map<String, DataFrame.Variable> {
        return df.variables().associateBy(DataFrame.Variable::name)
    }

    fun appendReplace(df0: DataFrame, df1: DataFrame): DataFrame {
        fun DataFrame.Builder.put(destVars: Collection<DataFrame.Variable>, df: DataFrame) = apply {
            destVars.forEach { destVar ->
                val srcVar = findVariableOrFail(df, destVar.name)
                when (df.isNumeric(srcVar)) {
                    true -> putNumeric(destVar, df.getNumeric(srcVar))
                    false -> putDiscrete(destVar, df[srcVar])
                }
            }
        }

        return DataFrame.Builder()
            .put(df0.variables().filter { it.name !in variables(df1) }, df0) // df0 - df1, keep vars from df0
            .put(df0.variables().filter { it.name in variables(df1) }, df1)  // df0 & df1, keep vars from df0
            .put(df1.variables().filter { it.name !in variables(df0) }, df1) // df1 - df0, new vars from df1
            .build()
    }

    fun toMap(df: DataFrame): Map<String, List<*>> {
        val result = HashMap<String, List<*>>()
        val variables = df.variables()
        for (`var` in variables) {
            result[`var`.name] = df[`var`]
        }
        return result
    }

    fun fromMap(map: Map<*, *>): DataFrame {
        val frameBuilder = DataFrame.Builder()
        for ((key, value) in map) {
            require(key is String) {
                "Map to data-frame: key expected a String but was " + key!!::class.simpleName + " : " + key
            }
            require(value is List<*>) {
                "Map to data-frame: value expected a List but was " + value!!::class.simpleName + " : " + value
            }
            frameBuilder.put(createVariable(key), value)
        }
        return frameBuilder.build()
    }

    @JvmOverloads
    fun createVariable(name: String, label: String = name): DataFrame.Variable {
        return when {
            TransformVar.isTransformVar(name) -> TransformVar[name]
            Stats.isStatVar(name) -> Stats.statVar(name)
            Dummies.isDummyVar(name) -> Dummies.newDummy(name)
            else -> DataFrame.Variable(name, DataFrame.Variable.Source.ORIGIN, label)
        }
    }

    fun getSummaryText(df: DataFrame): String {
        val sb = StringBuilder()
        for (variable in df.variables()) {
            sb.append(variable.toSummaryString())
                .append(" numeric: " + df.isNumeric(variable))
                .append(" size: " + df[variable].size)
                .append('\n')
        }
        return sb.toString()
    }

    fun removeAllExcept(df: DataFrame, keepNames: Set<String>): DataFrame {
        val b = df.builder()
        for (variable in df.variables()) {
            if (!keepNames.contains(variable.name)) {
                b.remove(variable)
            }
        }
        return b.build()
    }
}
