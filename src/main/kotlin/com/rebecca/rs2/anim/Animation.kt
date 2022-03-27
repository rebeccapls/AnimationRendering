package com.rebecca.rs2.anim

data class Animation(
    val frameCount: Int,
    val primaryAnimations: IntArray?,
    val secondaryAnimations: IntArray?,
    val delays: IntArray?,
    val loopOffset: Int,
    val labels: IntArray?,
    val priority: Int,
    val stretches: Boolean,
    val playerOffhand: Int,
    val playerMainhand: Int,
    val maximumLoops: Int,
    val animatingPrecedence: Int,
    val walkingPrecedence: Int,
    val replayMode: Int
)

