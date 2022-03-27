package com.rebecca.helper

import com.rebecca.rs2.npc.Npc
import jagex.graphic.Draw2D
import jagex.graphic.Draw3D
import jagex.graphic.DrawArea
import jagex.model.Model
import java.awt.Transparency
import java.awt.color.ColorSpace
import java.awt.image.*

const val width: Int = 400
const val height: Int = 540

fun prepareViewport(w: Int? = null, h: Int? = null): DrawArea {
    val viewport = DrawArea(w ?: width, h ?: height).also { it.setup() }
    return viewport
}

fun DrawArea.setup() {
    bind()
    Draw3D.prepareOffsets()
    Draw3D.createPalette(0.7)
}

fun clearViewportBuffer() {
    Draw2D.clear()
    Draw3D.clear()
}

fun DrawArea.toImage(): BufferedImage {
    val raw = ByteArray(pixels.size * 4)
    var offset = 0
    for (rgb in pixels) {
        raw[offset++] = (rgb shr 16).toByte()
        raw[offset++] = (rgb shr 8).toByte()
        raw[offset++] = rgb.toByte()

        if (rgb shr 24 == 0x7F) {
            raw[offset++] = 0.toByte()
        } else {
            raw[offset++] = 0xFF.toByte()
        }
    }
    val buffer: DataBuffer = DataBufferByte(raw, raw.size)
    val samplesPerPixel = 4
    val bandOffsets = intArrayOf(0, 1, 2, 3)
    val colorModel: ColorModel = ComponentColorModel( ColorSpace.getInstance(ColorSpace.CS_sRGB),true,false, Transparency.TRANSLUCENT, DataBuffer.TYPE_BYTE)
    val raster = Raster.createInterleavedRaster(buffer, Draw2D.width,
        Draw2D.height,samplesPerPixel * Draw2D.width, samplesPerPixel, bandOffsets,null)
    return BufferedImage(colorModel, raster, colorModel.isAlphaPremultiplied, null)
}
