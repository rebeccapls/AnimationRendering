package com.rebecca.helper

import com.rebecca.rs2.anim.AnimationFrames
import com.rebecca.rs2.anim.impl.AnimService
import com.rebecca.rs2.model.impl.ModelService
import com.rebecca.rs2.npc.impl.NpcService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class NpcSkeletonHelper {

    @Autowired
    lateinit var npcs: NpcService

    @Autowired
    lateinit var npcd: NpcDrawingHelper

    @Autowired
    lateinit var models: ModelService

    @Autowired
    lateinit var anims: AnimService

    @Autowired
    lateinit var skeleton: AnimationFrames


}

fun Array<*>.intersects(other: Array<*>) = any { it in other }



