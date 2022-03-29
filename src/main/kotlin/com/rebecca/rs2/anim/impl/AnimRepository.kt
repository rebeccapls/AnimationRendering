package com.rebecca.rs2.anim.impl

import com.displee.cache.CacheLibrary
import com.rebecca.cache.repository.Repository
import com.rebecca.rs2.anim.Animation
import com.rebecca.rs2.anim.SkeletonService
import jagex.io.Buffer
import org.springframework.stereotype.Component
import java.io.ByteArrayInputStream

interface AnimRepository : Repository<Animation>


@Component
class CacheAnimRepository(private val frame: SkeletonService, private val animReader: AnimReader, private val cache: CacheLibrary) :
    AnimRepository {

    private val anims: List<Animation>

    init {
        val dataBuffer = Buffer(cache.data(0, 2, "seq.dat"))
        val count = dataBuffer.readUShort()
        anims = (0 until count).map { id ->
            animReader.decode(dataBuffer)
        }.toList()

        frame.init(18000)

        val archives = cache.index(2).archives().size
        (0..archives).forEach { archive ->
            cache.data(2, archive)?.let { file ->
                frame.read(ByteArrayInputStream(file)) }
        }
    }

    override fun findById(id: Int): Animation? {
        return anims[id]
    }

    override fun getCount(): Int {
        return anims.size
    }
}
