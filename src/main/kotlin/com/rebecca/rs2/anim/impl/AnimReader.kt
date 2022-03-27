package com.rebecca.rs2.anim.impl

import com.rebecca.rs2.anim.Animation
import org.springframework.stereotype.Component
import jagex.io.Buffer

@Component
class AnimReader {

    fun decode(buffer: Buffer): Animation {
        var frameCount: Int = -1
        var loopOffset: Int = -1
        var primaryFrames: IntArray? = null
        var secondaryFrames: IntArray? = null
        var delays: IntArray? = null
        var interLeaveOrder: IntArray? = null
        var priority: Int = 5
        var stretches: Boolean = false
        var playerOffhand: Int = -1
        var playerMainhand: Int = -1
        var maximumLoops: Int = 99
        var animatingPrecedence: Int = -1
        var walkingPrecedence: Int = -1
        var replayMode: Int = 2
        do when(buffer.readUnsignedByte()) {
            0 -> return Animation(frameCount, primaryFrames, secondaryFrames, delays, loopOffset, interLeaveOrder, priority, stretches, playerOffhand, playerMainhand, maximumLoops, animatingPrecedence, walkingPrecedence, replayMode)
            1 -> {
                frameCount = buffer.readUnsignedByte()
                primaryFrames = IntArray(frameCount )
                secondaryFrames = IntArray(frameCount )
                delays = IntArray(frameCount )
                for(i in 0 until frameCount) {
                    primaryFrames[i] = buffer.readUShort()
                    secondaryFrames[i] = buffer.readUShort()
                    if(secondaryFrames[i] == 65535) {
                        secondaryFrames[i] = -1
                    }
                    delays[i] = buffer.readUShort()
                }
            }
            2 -> loopOffset = buffer.readUShort()
            3 -> {
                val count: Int = buffer.readUnsignedByte()
                interLeaveOrder = IntArray(count)
                for(i in 0 until count ) {
                    interLeaveOrder[i] = buffer.readUnsignedByte()
                }
                interLeaveOrder[count - 1] = 9999999
            }
            4 -> stretches = true
            5 -> priority = buffer.readUnsignedByte()
            6 -> playerOffhand = buffer.readUShort()
            7 -> playerMainhand = buffer.readUShort()
            8 -> maximumLoops = buffer.readUnsignedByte()
            9 -> animatingPrecedence = buffer.readUnsignedByte()
            10 -> walkingPrecedence = buffer.readUnsignedByte()
            11 -> replayMode = buffer.readUnsignedByte()
            12 -> buffer.readInt()
            else -> print("Error.")
        }
        while (true)
    }
}
