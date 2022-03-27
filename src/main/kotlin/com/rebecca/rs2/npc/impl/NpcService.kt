package com.rebecca.rs2.npc.impl

import com.rebecca.rs2.npc.Npc
import org.springframework.stereotype.Service


@Service
class NpcService(private val repository: NpcRepository) {

    fun getCount(): Int {
        return repository.getCount()
    }

    fun getNpc(id: Int): Npc {
        return repository.findById(id)!!
    }

}