package com.rebecca.factory

import com.rebecca.rs2.anim.SkeletonService
import jagex.model.Model
import org.springframework.stereotype.Component

@Component
class ModelFactory(private val skeletonService: SkeletonService) {

    fun newModel(models: Array<Model?>, count: Int): Model {
        return Model(skeletonService, models, count)
    }

    fun newModel(): Model {
        return Model(skeletonService)
    }
}