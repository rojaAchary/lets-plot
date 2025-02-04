/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot

import jetbrains.datalore.base.annotation.IgnoreJs
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.testing.EXPECTED_BUNCH_SVG
import jetbrains.datalore.plot.testing.EXPECTED_SINGLE_PLOT_SVG
import kotlin.test.Test
import kotlin.test.assertEquals

internal class PlotSizeHelperTest {
    @Test
    @IgnoreJs
    fun svgSizeFromSinglePlotSvg() {
        val sizeFromSvg = PlotSizeHelper.fetchPlotSizeFromSvg(EXPECTED_SINGLE_PLOT_SVG)
        assertEquals(DoubleVector(400.0, 300.0), sizeFromSvg)
    }

    @Test
    @IgnoreJs
    fun svgSizeFromGGBunchSvg() {
        val sizeFromSvg = PlotSizeHelper.fetchPlotSizeFromSvg(EXPECTED_BUNCH_SVG)
        assertEquals(DoubleVector(300.0, 150.0), sizeFromSvg)
    }
}