package com.rebecca.util

import cc.ekblad.toml.TomlValue
import cc.ekblad.toml.serialization.from
import cc.ekblad.toml.transcoding.get
import com.rebecca.util.`object`.npcData
import com.rebecca.rs2.npc.impl.NpcService
import com.rebecca.tasks.DrawNpcTask
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import java.nio.file.Path

@Component
class TomlParser(val npcs: NpcService) {

    @Autowired
    lateinit var draw: DrawNpcTask

    @EventListener(ApplicationStartedEvent::class)
    fun ParseAnimationToml() {
        val animations = TomlValue.from(Path.of(tomlDirectory))
        repeat(npcs.getCount()) { id ->
            val anims = animations.get<List<Int>>("$id", "anims")?.toIntArray()
            npcData.put(id, anims)
        }
        draw.draw()
    }
}