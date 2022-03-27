package com.rebecca.rs2.anim

import org.springframework.stereotype.Service
import jagex.io.Buffer
import java.io.InputStream

@Service
class AnimationFrames {

    var frames: Array<AnimationFrames?> = arrayOf()

    private val TRANSFORM_X = 1
    private val TRANSFORM_Y = 2
    private val TRANSFORM_Z = 4

    private lateinit var opaque: BooleanArray

    fun clearFrames() {
        frames = emptyArray()
    }

    fun init(size: Int) {
        frames = arrayOfNulls(size)
        opaque = BooleanArray(size)
        for (index in 0 until size) {
            opaque[index] = true
        }
    }

    fun read(stream: InputStream) {
        val seq = stream.readBytes()
        stream.close()
        return load(seq)
    }

    fun isInvalid(frame: Int): Boolean {
        return frame == -1
    }

    fun load(data: ByteArray) {

        val buffer = Buffer(data)
        buffer.position = data.size - 8

        val attributesOffset = buffer.readUShort()
        val translationsOffset = buffer.readUShort()
        val durationsOffset = buffer.readUShort()
        val baseOffset = buffer.readUShort()

        var offset = 0
        val head = Buffer(data)
        head.position = offset

        offset += attributesOffset + 2
        val attributes = Buffer(data)
        attributes.position = offset

        offset += translationsOffset
        val translations = Buffer(data)
        translations.position = offset

        offset += durationsOffset
        val durations = Buffer(data)
        durations.position = offset

        offset += baseOffset
        val bases = Buffer(data)
        bases.position = offset

        val base = FrameBase(bases)
        val frameCount = head.readUShort()

        val translationIndices = IntArray(500)
        val transformX = IntArray(500)
        val transformY = IntArray(500)
        val transformZ = IntArray(500)

        for (frameIndex in 0 until frameCount) {
            val id = head.readUShort()
            val frame: AnimationFrames = AnimationFrames().also { frames[id] = it }
            frame.duration = durations.readUnsignedByte()
            frame.base = base
            val transformations: Int = head.readUnsignedByte()
            var lastIndex = -1
            var transformation = 0
            for (index in 0 until transformations) {
                val attribute: Int = attributes.readUnsignedByte()
                if (attribute > 0) {

                    if (base.getTransformationType(index) != CENTROID_TRANSFORMATION) {
                        for (next in index - 1 downTo lastIndex + 1) {
                            if (base.getTransformationType(next) != CENTROID_TRANSFORMATION) {
                                continue
                            }
                            translationIndices[transformation] = next
                            transformX[transformation] = 0
                            transformY[transformation] = 0
                            transformZ[transformation] = 0
                            transformation++
                            break
                        }
                    }
                    translationIndices[transformation] = index
                    val standard =
                        if (base.getTransformationType(index) == SCALE_TRANSFORMATION) 128 else 0
                    transformX[transformation] =
                        if (attribute and TRANSFORM_X != 0) translations.readSmart() else standard
                    transformY[transformation] =
                        if (attribute and TRANSFORM_Y != 0) translations.readSmart() else standard
                    transformZ[transformation] =
                        if (attribute and TRANSFORM_Z != 0) translations.readSmart() else standard
                    lastIndex = index
                    transformation++
                    if (base.getTransformationType(index) == ALPHA_TRANSFORMATION) {
                        opaque[id] = false
                    }
                }
            }
            frame.transformationCount = transformation
            frame.transformationIndices = IntArray(transformation)
            frame.transformX = IntArray(transformation)
            frame.transformY = IntArray(transformation)
            frame.transformZ = IntArray(transformation)
            for (index in 0 until transformation) {
                frame.transformationIndices[index] = translationIndices[index]
                frame.transformX[index] = transformX[index]
                frame.transformY[index] = transformY[index]
                frame.transformZ[index] = transformZ[index]
            }
        }
    }

    fun lookup(index: Int): AnimationFrames? {
        return if (frames == null) null else frames[index]
    }

    private var base: FrameBase? = null
    private var duration = 0
    private var transformationCount = 0
    private lateinit var transformX: IntArray
    private lateinit var transformY: IntArray
    private lateinit var transformZ: IntArray
    private lateinit var transformationIndices: IntArray

    /**
     * Gets the [FrameBase] of this Frame.
     *
     * @return The FrameBase.
     */
    fun getBase(): FrameBase? {
        return base
    }

    /**
     * Gets the duration this Frame lasts.
     *
     * @return The duration.
     */
    fun getDuration(): Int {
        return duration
    }

    /**
     * Gets the amount of transformations in this Frame.
     *
     * @return The amount of transformations.
     */
    fun getTransformationCount(): Int {
        return transformationCount
    }

    fun getTransformX(transformation: Int): Int {
        return transformX[transformation]
    }

    fun getTransformY(transformation: Int): Int {
        return transformY[transformation]
    }

    fun getTransformZ(transformation: Int): Int {
        return transformZ[transformation]
    }

    fun getTransformationIndex(index: Int): Int {
        return transformationIndices[index]
    }


    val CENTROID_TRANSFORMATION = 0
    val POSITION_TRANSFORMATION = 1
    val ROTATION_TRANSFORMATION = 2
    val SCALE_TRANSFORMATION = 3
    val ALPHA_TRANSFORMATION = 5

}