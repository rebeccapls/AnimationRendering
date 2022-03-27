package com.rebecca.rs2.model.impl

import jagex.model.Model
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class ModelService(private val repository: ModelRepository) {

    @Cacheable("models")
    fun getModel(id: Int): Model? {
        return repository.findById(id)
    }

    fun getCount(): Int { return repository.getCount()}
}
