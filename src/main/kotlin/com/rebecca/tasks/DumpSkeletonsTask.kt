package com.rebecca.tasks

import com.rebecca.util.`object`.NpcToml
import com.rebecca.rs2.anim.SkeletonService
import com.rebecca.rs2.anim.impl.AnimService
import com.rebecca.rs2.npc.impl.NpcService
import org.springframework.stereotype.Component
import java.io.FileWriter

@Component
class DumpSkeletonsTask(val npcs: NpcService, val anims: AnimService, val skeletons: SkeletonService) {

    var toml: MutableSet<NpcToml> = mutableSetOf()

    fun skeleton() {
        val time = System.nanoTime()
        toml = mutableSetOf()
        (0 until anims.getCount()).toList().parallelStream().forEachOrdered { id ->
            val npc_ = npcs.getNpc(id)
            val primaries: IntArray = anims.getAnimation(npc_.standAnimation!!).primaryAnimations ?: intArrayOf(0)
            val testGroup: Array<IntArray?>? = skeletons.lookup(primaries[0])?.getFrameMap()?.groups
            val linked: MutableSet<Int> = mutableSetOf()
            (0 until anims.getCount()).forEach { _anim ->
                val animGroups = skeletons.lookup(anims.getAnimation(_anim).primaryAnimations!![0])?.getFrameMap()?.groups
                if (animGroups contentDeepEquals testGroup) {
                    linked.add(_anim)
                }
            }
            toml.add(NpcToml(npc_.id, npc_.name, npc_.walkAnimation, npc_.standAnimation, npc_.modelIds?.sorted()?.joinToString(","), linked.distinct().sorted().joinToString(",")))
        }
        generate()
        println("Took ${((System.nanoTime() - time)) / 1000000}ms to complete ${toml.toList().size} npcs.")
    }

    fun generate() {
        toml.toList().sortedBy { it.id }.map { npc ->
            FileWriter("anims.toml", true).use {
                it.appendLine("[${npc.id}]")
                it.appendLine("walkanim=${npc.walkanim}")
                it.appendLine("standanim=${npc.standanim}")
                it.appendLine("models=[${npc.models ?: ""}]")
                it.appendLine("anims=[${npc.anims}]\n")
                it.close()
            }
        }
    }
}