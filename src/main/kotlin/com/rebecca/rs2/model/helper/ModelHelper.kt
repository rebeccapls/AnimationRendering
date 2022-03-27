package com.rebecca.rs2.model.helper

import com.rebecca.rs2.anim.FrameBase
import com.rebecca.rs2.anim.AnimationFrames
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import jagex.model.Model


@Component
abstract class ModelHelper : Model() {

    @Autowired
    lateinit var anim: AnimationFrames

    fun Model.animate(frame: Int) {
        if (labelVertices == null) {
            return
        }
        if (frame == -1) {
            return
        }
        val animation: AnimationFrames? = anim.frames[frame]
        val base = animation?.getBase()

        transformX = 0
        transformY = 0
        transformZ = 0

        for (n in 0 until animation!!.getTransformationCount()) {
            val group = animation.getTransformationIndex(n)
            transform(base!!.getTransformationType(group), base.getGroups(group)!!, animation.getTransformX(n), animation.getTransformY(n), animation.getTransformZ(n))
        }
    }

    fun Model.animate(primary: Int, secondary: Int, labels: IntArray?) {
        if (primary == -1) {
            return
        } else if (labels == null || secondary == -1) {
            animate(primary)
            return
        }

        val primaryAnim: AnimationFrames = anim.frames[primary] ?: return
        val secondaryAnim: AnimationFrames = anim.frames[secondary] ?: return

        val frameBase: FrameBase = primaryAnim.getBase()!!
        var index = 0

        var next: Int = labels.get(index++)
        for (transformation in 0 until primaryAnim.getTransformationCount()) {
            val group: Int = primaryAnim.getTransformationIndex(transformation)
            while (group > next) {
                next = labels.get(index++)
            }
            if (group != next || frameBase.getTransformationType(group) == 0) {
                transform(frameBase.getTransformationType(group), frameBase.getGroups(group), primaryAnim.getTransformX(transformation), primaryAnim.getTransformY(transformation), primaryAnim.getTransformZ(transformation)
                )
            }
        }

        index = 0
        next = labels.get(index++)

        for (transformation in 0 until secondaryAnim.getTransformationCount()) {
            var group: Int
            group = secondaryAnim.getTransformationIndex(transformation)
            while (group > next) {
                next = labels.get(index++)
            }
            if (group == next || frameBase.getTransformationType(group) == 0) {
                transform(
                    frameBase.getTransformationType(group), frameBase.getGroups(group), secondaryAnim.getTransformX(transformation),
                    secondaryAnim.getTransformY(transformation), secondaryAnim.getTransformZ(transformation)
                )
            }
        }
    }


    fun Model.transform(type: Int, labels: IntArray?, x: Int, y: Int, z: Int) {
        val count = labels!!.size
        if (type == 0) {
            var counter = 0
            transformX = 0
            transformY = 0
            transformZ = 0
            for (n in 0 until count) {
                val label = labels[n]
                if (label < labelVertices.size) {
                    val vertices: IntArray = labelVertices.get(label)
                    for (v in vertices.indices) {
                        val index = vertices[v]
                        transformX += vertexX.get(index)
                        transformY += vertexY.get(index)
                        transformZ += vertexZ.get(index)
                        counter++
                    }
                }
            }
            if (counter > 0) {
                transformX = transformX / counter + x
                transformY = transformY / counter + y
                transformZ = transformZ / counter + z
            } else {
                transformX = x
                transformY = y
                transformZ = z
            }
        } else if (type == 1) {
            for (n in 0 until count) {
                val label = labels[n]
                if (label < labelVertices.size) {
                    val vertices: IntArray = labelVertices.get(label)
                    for (v in vertices.indices) {
                        val index = vertices[v]
                        vertexX[index] += x
                        vertexY[index] += y
                        vertexZ[index] += z
                    }
                }
            }
        } else if (type == 2) {
            for (i in 0 until count) {
                val label = labels[i]
                if (label < labelVertices.size) {
                    val vertices: IntArray = labelVertices.get(label)
                    for (v in vertices.indices) {
                        val index = vertices[v]
                        vertexX[index] -= transformX
                        vertexY[index] -= transformY
                        vertexZ[index] -= transformZ
                        val pitch = (x and 0xff) * 8
                        val yaw = (y and 0xff) * 8
                        val roll = (z and 0xff) * 8
                        if (roll != 0) {
                            val s = sin[roll]
                            val c = cos[roll]
                            val x0: Int = vertexY.get(index) * s + vertexX.get(index) * c shr 16
                            vertexY[index] = vertexY.get(index) * c - vertexX.get(index) * s shr 16
                            vertexX[index] = x0
                        }
                        if (pitch != 0) {
                            val s = sin[pitch]
                            val c = cos[pitch]
                            val y0: Int = vertexY.get(index) * c - vertexZ.get(index) * s shr 16
                            vertexZ[index] = vertexY.get(index) * s + vertexZ.get(index) * c shr 16
                            vertexY[index] = y0
                        }
                        if (yaw != 0) {
                            val s = sin[yaw]
                            val c = cos[yaw]
                            val z0: Int = vertexZ.get(index) * s + vertexX.get(index) * c shr 16
                            vertexZ[index] = vertexZ.get(index) * c - vertexX.get(index) * s shr 16
                            vertexX[index] = z0
                        }
                        vertexX[index] += transformX
                        vertexY[index] += transformY
                        vertexZ[index] += transformZ
                    }
                }
            }
        } else if (type == 3) {
            for (i in 0 until count) {
                val label = labels[i]
                if (label < labelVertices.size) {
                    val vertices: IntArray = labelVertices.get(label)
                    for (v in vertices.indices) {
                        val index = vertices[v]
                        vertexX[index] -= transformX
                        vertexY[index] -= transformY
                        vertexZ[index] -= transformZ
                        vertexX[index] = vertexX.get(index) * x / 128
                        vertexY[index] = vertexY.get(index) * y / 128
                        vertexZ[index] = vertexZ.get(index) * z / 128
                        vertexX[index] += transformX
                        vertexY[index] += transformY
                        vertexZ[index] += transformZ
                    }
                }
            }
        } else if (type == 5 && skinTriangle != null && triangleAlpha != null) {
            for (i in 0 until count) {
                val label = labels[i]
                if (label < skinTriangle.size) {
                    val triangles: IntArray = skinTriangle.get(label)
                    for (t in triangles.indices) {
                        val index = triangles[t]
                        triangleAlpha[index] += x * 8
                        if (triangleAlpha[index] < 0) {
                            triangleAlpha[index] = 0
                        } else if (triangleAlpha.get(index) > 255) {
                            triangleAlpha[index] = 255
                        }
                    }
                }
            }
        }
    }
}



