package com.rebecca.rs2.npc.impl

import com.displee.cache.CacheLibrary
import com.rebecca.cache.repository.Repository
import com.rebecca.rs2.npc.Npc
import org.springframework.stereotype.Component
import jagex.io.Buffer

interface NpcRepository : Repository<Npc>

@Component
class CacheNpcRepository(private val reader: NpcReader, private val cache: CacheLibrary) : NpcRepository {

    private val npcs: List<Npc>

    init {
        val idxBuffer = Buffer(cache.data(0, 2, "npc.idx")!!)
        val dataBuffer = Buffer(cache.data(0, 2, "npc.dat")!!)

        npcs = (0 until idxBuffer.readUShort()).map { id ->
            reader.read(dataBuffer, id)
        }.toList()

    }

    override fun findById(id: Int): Npc? {
        return npcs.getOrNull(id)
    }

    override fun getCount(): Int {
        return npcs.size
    }
}