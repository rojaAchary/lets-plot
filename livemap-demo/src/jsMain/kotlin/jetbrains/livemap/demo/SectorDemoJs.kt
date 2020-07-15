/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.demo

import jetbrains.datalore.base.geometry.Rectangle
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.vis.canvas.dom.DomCanvasControl
import org.w3c.dom.HTMLElement
import kotlin.browser.document

@Suppress("unused")
@JsName("sectorDemo")
fun sectorDemo() {
    val size = Vector(800, 600)
    val rootElement: HTMLElement = document.createElement("div") as HTMLElement
    val canvasControl = DomCanvasControl(
        rootElement,
        size,
        DomCanvasControl.DomEventPeer(rootElement, Rectangle(Vector.ZERO, size))
    )

    val canvas = canvasControl.createCanvas(size)
    SectorDemoModel(canvas)
    canvasControl.addChild(canvas)

    document.getElementById(DemoBaseJs.parentNodeId)
        ?.appendChild(rootElement)
        ?: error("Parent node '${DemoBaseJs.parentNodeId}' wasn't found")
}
