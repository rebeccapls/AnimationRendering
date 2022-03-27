package com.rebecca.rs2.anim

import jagex.io.Buffer

class FrameBase {
    val bases: Buffer

    constructor(bases: Buffer) {
        this.bases = bases
        count = bases.readUnsignedByte()
        transformationType = IntArray(count)
        groups = arrayOfNulls(count)
        for (index in 0 until count) {
            transformationType[index] = bases.readUnsignedByte()
        }
        for (group in 0 until count) {
            val count: Int = bases.readUnsignedByte()
            groups[group] = IntArray(count)
            for (index in 0 until count) {
                groups[group]!![index] = bases.readUnsignedByte()
            }
        }
    }

    /**
     * The amount of transformations.
     */
    var count = 0
    lateinit var groups: Array<IntArray?>

    /**
     * The type of each transformation.
     */
    lateinit var transformationType: IntArray

    fun getGroups(group: Int): IntArray? {
        return groups[group]
    }

    /**
     * Gets the amount of transformations in this FrameBase.
     *
     * @return The amount of transformations.
     */
    fun getTransformationCount(): Int {
        return count
    }

    /**
     * Gets the transformation type of the transformation at the specified index.
     *
     * @param index The index.
     * @return The transformation type.
     */
    fun getTransformationType(index: Int): Int {
        return transformationType[index]
    }
}

