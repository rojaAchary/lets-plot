/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder

import jetbrains.datalore.plot.base.Aes

abstract class AestheticAxis(
    val aes: Aes<Double>,
    private val positions: Set<Aes<Double>>,
    private val intercepts: Set<Aes<Double>>
) {
    operator fun contains(v: Aes<*>) = v == aes || v in positions || v in intercepts
    fun isAffectingScale(aes: Aes<*>) = aes == this.aes || aes in positions
}

object XAestheticAxis : AestheticAxis(
    aes = Aes.X,
    positions = setOf(Aes.XEND, Aes.XMAX, Aes.XMIN),
    intercepts = setOf(Aes.XINTERCEPT)
)

object YAestheticAxis : AestheticAxis (
    aes = Aes.Y,
    positions = setOf(Aes.YMIN, Aes.YMAX, Aes.LOWER, Aes.MIDDLE, Aes.UPPER, Aes.YEND),
    intercepts = setOf(Aes.INTERCEPT, Aes.YINTERCEPT)
)
