/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.interact.TooltipLineSpec
import jetbrains.datalore.plot.builder.GeomLayer
import jetbrains.datalore.plot.config.Option.Scale.AES
import jetbrains.datalore.plot.config.Option.Scale.BREAKS
import jetbrains.datalore.plot.config.Option.Scale.DISCRETE_DOMAIN
import jetbrains.datalore.plot.config.Option.Scale.FORMAT
import jetbrains.datalore.plot.config.Option.Scale.LABELS
import jetbrains.datalore.plot.config.ScaleConfigLabelsTest.Companion.getScaleLabels
import jetbrains.datalore.plot.config.TestUtil.buildPointLayer
import kotlin.test.Test
import kotlin.test.assertEquals


class ScaleTickLabelsAndTooltipTest {
    /// Tests to check content of the axis's tooltip and labels on ticks

    private val myData = mapOf("x" to listOf(0))
    private val myMapping = mapOf(Aes.X.name to "x", Aes.Y.name to "x")
    private val myBreaks = listOf(0.0, 1.0, 2.0)
    private val myLabels = listOf("00", "01", "02")
    private val myFormat = "{.1f}%"

    private fun buildGeomLayerWithScaleParameters(
        withBreaks: Boolean = false,
        withFormat: Boolean = false,
        withLabels: Boolean = false,
        isDiscrete: Boolean = false,
    ): GeomLayer {
        return buildPointLayer(
            myData,
            myMapping,
            scales = listOf(
                mapOf(
                    AES to Aes.X.name,
                    BREAKS to if (withBreaks) myBreaks else null,
                    FORMAT to if (withFormat) myFormat else null,
                    LABELS to if (withLabels) myLabels else null,
                    DISCRETE_DOMAIN to isDiscrete
                )
            )
        )
    }

    // discrete scale

    @Test
    fun `discrete scale`() {
        val geomLayer = buildGeomLayerWithScaleParameters(isDiscrete = true)

        assertTickLabels(listOf("0.0"), geomLayer)
        assertAxisTooltip("0.0", geomLayer)
    }

    @Test
    fun `discrete scale with specified 'breaks' and 'format' - tick labels and tooltip will get formatted values from the 'breaks'`() {
        val geomLayer = buildGeomLayerWithScaleParameters(
            isDiscrete = true,
            withBreaks = true,
            withFormat = true
        )

        assertTickLabels(expected = listOf("0.0%", "1.0%", "2.0%"), geomLayer)
        assertAxisTooltip(expected = "0.0%", geomLayer)
    }

    @Test
    fun `discrete scale with specified 'breaks' and 'labels' - tick labels and tooltip will get values from the 'labels'`() {
        val geomLayer = buildGeomLayerWithScaleParameters(
            isDiscrete = true,
            withBreaks = true,
            withLabels = true
        )

        assertTickLabels(expected = listOf("00", "01", "02"), geomLayer)
        assertAxisTooltip(expected = "00", geomLayer)
    }

    @Test
    fun `discrete scale with specified 'format' and 'labels' - tick labels and tooltip will get values from the 'labels'`() {
        val geomLayer = buildGeomLayerWithScaleParameters(
            isDiscrete = true,
            withFormat = true,
            withLabels = true
        )

        assertTickLabels(expected = listOf("00"), geomLayer)
        assertAxisTooltip("00", geomLayer)
    }

    @Test
    fun `discrete scale with specified 'breaks', 'format', 'labels' - tick labels and tooltip will get values from the 'labels'`() {
        val geomLayer = buildGeomLayerWithScaleParameters(
            isDiscrete = true,
            withBreaks = true,
            withFormat = true,
            withLabels = true
        )

        assertTickLabels(listOf("00", "01", "02"), geomLayer)
        assertAxisTooltip("00", geomLayer)
    }

    // Continuous scale

    @Test
    fun `continuous scale`() {
        val geomLayer = buildGeomLayerWithScaleParameters()

        assertTickLabels(expected = listOf("-0.4", "-0.2", "0.0", "0.2", "0.4"), geomLayer)
        assertAxisTooltip(expected = "0.00", geomLayer)
    }

    @Test
    fun `continuous scale with specified 'breaks' and 'format' - tick labels and tooltip will get formatted values from the 'breaks'`() {
        val geomLayer = buildGeomLayerWithScaleParameters(withBreaks = true, withFormat = true)

        assertTickLabels(listOf("0.0%", "1.0%", "2.0%"), geomLayer)
        assertAxisTooltip("0.0%", geomLayer)
    }

    @Test
    fun `continuous scale with specified 'breaks' and 'labels' - ticks labels from the 'labels' and default tooltip`() {
        val geomLayer = buildGeomLayerWithScaleParameters(withBreaks = true, withLabels = true)

        assertTickLabels(expected = listOf("00", "01", "02"), geomLayer)

        // todo Now tooltip has a 'breaks' value with default formatting.
        //  Should tooltip have value from the 'labels'?
        assertAxisTooltip(expected = "0.00", geomLayer)
    }

    @Test
    fun `continuous scale with specified 'format' and 'labels' - default tick labels and default tooltip`() {
        val geomLayer = buildGeomLayerWithScaleParameters(withFormat = true, withLabels = true)

        // 'labels' are ignored.
        // todo Should use the specified formatting?
        assertTickLabels(expected = listOf("-0.4", "-0.2", "0.0", "0.2", "0.4"), geomLayer)

        // todo Should use the specified formatting?
        assertAxisTooltip(expected = "0.00", geomLayer)
    }

    @Test
    fun `continuous scale with specified 'breaks', 'format', 'labels' - tick labels from the 'labels' and default tooltip`() {
        val geomLayer = buildGeomLayerWithScaleParameters(withBreaks = true, withFormat = true, withLabels = true)

        assertTickLabels(expected = listOf("00", "01", "02"), geomLayer)

        // todo Now tooltip has a 'breaks' value with default formatting.
        //  Should tooltip have value from the 'labels'? Or should use the specified formatting to value from 'breaks'?
        assertAxisTooltip(expected = "0.00", geomLayer)
    }

    companion object {

        fun assertTickLabels(expected: List<String>, geomLayer: GeomLayer, aes: Aes<Double> = Aes.X) {
            assertEquals(
                expected,
                getScaleLabels(geomLayer.scaleMap[aes])
            )
        }

        fun assertAxisTooltip(expected: String, geomLayer: GeomLayer, aes: Aes<Double> = Aes.X) {
            val dataPoints = geomLayer.contextualMapping.getDataPoints(index = 0)
            val tooltipValue = dataPoints.filter { it.aes == aes }.map(TooltipLineSpec.DataPoint::value)
            assertEquals(listOf(expected), tooltipValue)
        }
    }
}