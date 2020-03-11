/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.assemble

import jetbrains.datalore.plot.builder.AestheticAxis
import jetbrains.datalore.plot.builder.XAestheticAxis
import jetbrains.datalore.plot.builder.YAestheticAxis
import jetbrains.datalore.plot.builder.coord.CoordProvider
import jetbrains.datalore.plot.builder.coord.FlippedCoordProvider
import jetbrains.datalore.plot.builder.layout.PlotLayout
import jetbrains.datalore.plot.builder.layout.TileLayout

abstract class PlotCoordReference(
    val horizontalAxis: AestheticAxis,
    val verticalAxis: AestheticAxis,
    val coordProvider: CoordProvider
) {
    abstract fun createPlotLayout(tileLayout: TileLayout, facetLayout: Boolean, facets: PlotFacets): PlotLayout
}

private class PlotFlippedCoordReference constructor(
    coordProvider: CoordProvider
) : PlotCoordReference(YAestheticAxis, XAestheticAxis, coordProvider) {
    override fun createPlotLayout(tileLayout: TileLayout, facetLayout: Boolean, facets: PlotFacets): PlotLayout {
        return PlotAssemblerUtil.createPlotLayout(tileLayout, facetLayout, facets)
    }
}

private class PlotOriginalCoordReference constructor(
    coordProvider: CoordProvider
) : PlotCoordReference(XAestheticAxis, YAestheticAxis, coordProvider) {
    override fun createPlotLayout(tileLayout: TileLayout, facetLayout: Boolean, facets: PlotFacets): PlotLayout {
        return PlotAssemblerUtil.createPlotLayout(tileLayout,facetLayout, facets)
    }
}

fun createPlotCoordReference(coordProvider: CoordProvider) = when (coordProvider) {
    is FlippedCoordProvider -> PlotFlippedCoordReference(coordProvider)
    else -> PlotOriginalCoordReference(coordProvider)
}
