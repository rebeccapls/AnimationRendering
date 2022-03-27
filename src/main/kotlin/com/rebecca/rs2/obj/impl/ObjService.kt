package com.rebecca.rs2.obj.impl

import com.rebecca.rs2.model.impl.ModelService
import com.rebecca.rs2.obj.Obj
import jagex.graphic.Draw2D
import jagex.graphic.Draw3D
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import jagex.sprite.Sprite

@Service
class ObjService(private val repository: ObjRepository, private val modelService: ModelService) {

    fun getCount(): Int {
        return repository.getCount()
    }

    fun getObj(id: Int): Obj? {
        return repository.findById(id)
    }

   @Cacheable("objsprite")
    fun getObjSprite(obj: Obj): Sprite? {
        val model = modelService.getModel(obj.model) ?: return null
        Draw3D.createPalette(0.7)

        if ((obj.scaleX != 128) || (obj.scaleZ != 128) || (obj.scaleY != 128)) {
            model.scale(obj.scaleX, obj.scaleZ, obj.scaleY)
        }

        if (obj.originalColors != null) {
            for (i in 0 until obj.originalColors!!.size) {
                model.recolor(obj.originalColors!![i], obj.replacementColors!![i])
            }
        }
        model.applyLighting(64 + obj.ambient, 768 + obj.attenuation, -50, -10, -50, true)

        val rendered = Sprite(320, 320)
        val centreX: Int = Draw3D.centerX
        val centreY: Int = Draw3D.centerY
        val scanOffsets: IntArray = Draw3D.offsets
        val raster: IntArray = Draw2D.dest
        val width: Int = Draw2D.width
        val height: Int = Draw2D.height
        val clipLeft: Int = Draw2D.left
        val clipRight: Int = Draw2D.right
        val clipBottom: Int = Draw2D.bottom
        val clipTop: Int = Draw2D.top

        Draw2D.prepare(rendered.pixels, 320, 320)
        Draw2D.fillRect(0, 0, 320, 320, 0)
        Draw3D.prepareOffsets()

        val sinPitch: Int = (Draw3D.sin[obj.pitch] * (obj.zoom / 10)) shr 16
        val cosPitch: Int = (Draw3D.cos[obj.pitch] * (obj.zoom / 10)) shr 16
        model.draw(0, obj.yaw ,obj.roll, obj.pitch, obj.translateX, sinPitch + (model.maxBoundY / 2) + obj.translateY, cosPitch + obj.translateY)

        for (x in 31 downTo 0) {
            for (y in 31 downTo 0) {
                if (rendered.pixels[x + (y * 320)] == 0) {
                    if (x > 0 && rendered.pixels[(x - 1) + (y * 32)] > 1) {
                        rendered.pixels[x + (y * 320)] = 1
                    } else if (y > 0 && rendered.pixels[x + ((y - 1) * 32)] > 1) {
                        rendered.pixels[x + (y * 320)] = 1
                    } else if (x < 31 && rendered.pixels[x + 1 + (y * 32)] > 1) {
                        rendered.pixels[x + (y * 320)] = 1
                    } else if (y < 31 && rendered.pixels[x + ((y + 1) * 32)] > 1) {
                        rendered.pixels[x + (y * 320)] = 1
                    }
                }
            }
        }
        for (x in 319 downTo 0) {
            for (y in 319 downTo 0) {
                if (rendered.pixels[x + (y * 320)] == 0 && (x > 0) && (y > 0) && rendered.pixels[x - 1 + ((y - 1) * 320)] > 0) {
                    rendered.pixels[x + (y * 320)] = 0x302020
                }
            }
        }
        Draw2D.prepare(raster, width, height)
        Draw2D.setBounds(clipLeft, clipTop, clipRight, clipBottom)
        Draw3D.centerX = centreX
       Draw3D.centerY = centreY

        return rendered
    }

    fun getObjSprite(id: Int): Sprite? {
        return getObj(id)?.let { getObjSprite(it) }
    }
}
