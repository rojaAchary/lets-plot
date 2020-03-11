/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import jetbrains.datalore.plot.parsePlotSpec
import jetbrains.datalore.plotDemo.model.PlotConfigDemoBase

class BarCoordFlip : PlotConfigDemoBase() {
    fun plotSpecList(): List<Map<String, Any>> {
        return listOf(
            point(),
            point_flip(),
            x_category(),
            y_category_coord_flip(),
            y_category(),
            x_category_coord_flip(),
            x_category_coord_flip_stat(),
            y_category_coord_flip_stat()
        )
    }


    companion object {
        fun point(): MutableMap<String, Any> {
            return parsePlotSpec("""
                |{
                |  "kind": "plot",
                |  "layers": [
                |    {
                |      "geom": "point",
                |      "data": {
                |        "labels": ["A", "B"],
                |        "values": [3, 12]
                |      },
                |      "mapping": {
                |        "x": "labels",
                |        "y": "values",
                |        "color": "labels"
                |      }
                |    }
                |  ],
                |  "ggtitle": {"text": "x=\"labels\", y=\"values\", identity"}
                |}""".trimMargin()
            )
        }
        fun point_flip(): MutableMap<String, Any> {
            return parsePlotSpec("""
                |{
                |  "coord": {
                |    "name": "flip",
                |    "ratio": 1.0
                |  },
                |  "kind": "plot",
                |  "layers": [
                |    {
                |      "geom": "point",
                |      "data": {
                |        "labels": ["A", "B"],
                |        "values": [3, 12]
                |      },
                |      "mapping": {
                |        "x": "labels",
                |        "y": "values",
                |        "color": "labels"
                |      }
                |    }
                |  ],
                |  "ggtitle": {"text": "x=\"labels\", y=\"values\", identity + flip"}
                |}""".trimMargin()
            )
        }
        fun x_category(): MutableMap<String, Any> {
            return parsePlotSpec("""
                |{
                |  "kind": "plot",
                |  "layers": [
                |    {
                |      "geom": "bar",
                |      "stat": "identity",
                |      "data": {
                |        "labels": ["A", "B"],
                |        "values": [3, 12]
                |      },
                |      "mapping": {
                |        "x": "labels",
                |        "y": "values",
                |        "fill": "labels"
                |      }
                |    }
                |  ],
                |  "ggtitle": {"text": "x=\"labels\", y=\"values\", identity"}
                |}""".trimMargin()
            )
        }

        fun y_category(): MutableMap<String, Any> {
            return parsePlotSpec("""
                |{
                |  "kind": "plot",
                |  "layers": [
                |    {
                |      "geom": "bar",
                |      "stat": "identity",
                |      "data": {
                |        "labels": ["A", "B"],
                |        "values": [3, 12]
                |      },
                |      "mapping": {
                |        "x": "values",
                |        "y": "labels",
                |        "fill": "labels"
                |      }
                |    }
                |  ],
                |  "ggtitle": {"text": "x=\"values\", y=\"labels\", identity"}
                |}
            """.trimMargin())
        }

        fun x_category_coord_flip(): MutableMap<String, Any> {
            return parsePlotSpec("""
                |{
                |  "coord": {
                |    "name": "flip",
                |    "ratio": 1.0
                |  },
                |  "kind": "plot",
                |  "layers": [
                |    {
                |      "geom": "bar",
                |      "stat": "identity",
                |      "data": {
                |        "labels": ["A", "B"],
                |        "values": [3, 12]
                |      },
                |      "mapping": {
                |        "x": "labels",
                |        "y": "values",
                |        "fill": "labels"
                |      }
                |    }
                |  ],
                |  "ggtitle": {"text": "x=\"labels\", y=\"values\", identity + flip"}
                |}
            """.trimMargin())
        }

        fun y_category_coord_flip(): MutableMap<String, Any> {
            return parsePlotSpec("""
                |{
                |  "coord": {
                |    "name": "flip",
                |    "ratio": 1.0
                |  },
                |  "kind": "plot",
                |  "layers": [
                |    {
                |      "geom": "bar",
                |      "stat": "identity",
                |      "data": {
                |        "labels": ["A", "B"],
                |        "values": [3, 12]
                |      },
                |      "mapping": {
                |        "x": "values",
                |        "y": "labels",
                |        "fill": "labels"
                |      }
                |    }
                |  ],
                |  "ggtitle": {"text": "x=\"values\", y=\"labels\", identity + flip"}
                |}
            """.trimMargin())
        }

        fun x_category_coord_flip_stat(): MutableMap<String, Any> {
            return parsePlotSpec("""
                |{
                |  "coord": {
                |    "name": "flip",
                |    "ratio": 1.0
                |  },
                |  "kind": "plot",
                |  "layers": [
                |    {
                |      "geom": "bar",
                |      "data": {
                |        "time": ["Lunch", "Lunch", "Dinner", "Dinner", "Dinner" ]
                |      },
                |      "mapping": {"x": "time"}
                |    }
                |  ],
                |  "ggtitle": {"text": "x=\"labels\" + flip"}
                |}
            """.trimMargin())
        }
        fun y_category_coord_flip_stat(): MutableMap<String, Any> {
            return parsePlotSpec("""
                |{
                |  "coord": {
                |    "name": "flip",
                |    "ratio": 1.0
                |  },
                |  "kind": "plot",
                |  "layers": [
                |    {
                |      "geom": "bar",
                |      "data": {
                |        "time": ["Lunch", "Lunch", "Dinner", "Dinner", "Dinner" ]
                |      },
                |      "mapping": {"y": "time"}
                |    }
                |  ],
                |  "ggtitle": {"text": "y=\"labels\" + flip"}
                |}
            """.trimMargin())
        }
    }
}
