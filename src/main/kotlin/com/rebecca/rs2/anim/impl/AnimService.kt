package com.rebecca.rs2.anim.impl

import com.rebecca.rs2.anim.Animation
import org.springframework.stereotype.Service


@Service
class AnimService(private val repository: AnimRepository) {

    fun getCount(): Int {
        return repository.getCount()
    }

    fun getAnimation(id: Int): Animation {
        return repository.findById(id)!!
    }

}