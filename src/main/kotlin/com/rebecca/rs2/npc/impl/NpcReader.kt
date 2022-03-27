package com.rebecca.rs2.npc.impl

import com.rebecca.rs2.npc.Npc
import org.springframework.stereotype.Component
import jagex.io.Buffer

@Component
class NpcReader {

    fun read(buffer: Buffer, id: Int): Npc {
        var modelIds: IntArray? = null
        var originalColors: IntArray? = null
        var replacementColors: IntArray? = null
        var ambient = 0
        var attenuation = 0
        var scaleXY = 128
        var scaleZ = 128
        var size = 0
        var headModelIds: IntArray? = null
        var walkAnim = 0
        var standAnim = 0
        var name = ""
        do {
            val opcode = buffer.readUnsignedByte()
            when(opcode) {
                1 -> {
                    var count: Int = buffer.readUnsignedByte()
                    modelIds = IntArray(count)
                    for(i in 0 until count) {
                        modelIds[i] = buffer.readUShort()
                    }
                }
                2 -> name = buffer.readString()
                3 -> buffer.readStringBytes()
                12 -> size = buffer.readByte().toInt()
                13 -> standAnim = buffer.readUShort()
                14 -> walkAnim = buffer.readUShort()
                17 -> {
                    walkAnim = buffer.readUShort()
                    buffer.readUShort()
                    buffer.readUShort()
                    buffer.readUShort()
                }
                in 30 until 40 -> {
                    buffer.readString()
                }
                40 -> {
                    val count = buffer.readUnsignedByte()
                    originalColors = IntArray(count)
                    replacementColors = IntArray(count)
                    for(i in 0 until count) {
                        originalColors[i] = buffer.readUShort()
                        replacementColors[i] = buffer.readUShort()
                    }
                }
                60 -> {
                    var count = buffer.readUnsignedByte()
                    headModelIds = IntArray(count)
                    for(i in 0 until count) {
                        headModelIds[i] = buffer.readUShort()
                    }
                }
                90 -> buffer.readUShort()
                91 -> buffer.readUShort()
                92 -> buffer.readUShort()
                93 -> continue
                95 -> buffer.readUShort()
                97 -> scaleXY = buffer.readUShort()
                98 -> scaleZ = buffer.readUShort()
                99 -> continue
                100 -> ambient = buffer.readByte().toInt()
                101 -> attenuation = buffer.readByte() * 5
                102 -> buffer.readUShort()
                103 -> buffer.readUShort()
                106 -> {
                    var varbitId = buffer.readUShort()
                    var settingId = buffer.readUShort()
                    var childCount = buffer.readUnsignedByte()
                    for(i in 0 .. (childCount)) {
                        buffer.readUShort()
                    }
                }
                107 -> continue
                0 -> return Npc(id, name, modelIds, size, standAnim, walkAnim, originalColors, replacementColors, headModelIds, scaleXY, scaleZ, ambient, attenuation)
            }
        } while (true)
    }
}
