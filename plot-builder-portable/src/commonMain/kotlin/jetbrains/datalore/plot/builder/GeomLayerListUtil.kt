/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder

import jetbrains.datalore.plot.base.Scale


object GeomLayerListUtil {

    internal fun anyBound(tiles: List<List<GeomLayer>>, axis: AestheticAxis): Scale<Double>? {
        tiles.first().forEach { layer ->
            (listOf(axis.aes) + layer.renderedAes())
                .filter { aes -> aes in axis }
                .firstOrNull { aes -> layer.hasBinding(aes) }
                ?.let {
                    @Suppress("UNCHECKED_CAST")
                    return layer.getBinding(it).scale as Scale<Double>?
                }
            }
        return null
    }
}
