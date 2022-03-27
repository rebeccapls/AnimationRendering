package com.rebecca.rs2.model.impl

import com.displee.cache.CacheLibrary
import org.springframework.stereotype.Component
import com.rebecca.cache.repository.Repository
import jagex.model.Model
import java.io.ByteArrayInputStream

interface ModelRepository : Repository<Model>

@Component
class CacheModelRepository(private val reader: ModelReader, private val cache: CacheLibrary) : ModelRepository {

    override fun findById(id: Int): Model? {
        return cache.data(1, id)?.let { reader.read(ByteArrayInputStream(it)) }
    }

    override fun getCount(): Int {
        return cache.index(1).archives().size
    }
}
