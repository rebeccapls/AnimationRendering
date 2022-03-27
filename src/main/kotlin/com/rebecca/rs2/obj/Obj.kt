package com.rebecca.rs2.obj


data class Obj(
    var id: Int,
    var name: String,
    var model: Int,
    var originalColors: IntArray? = null,
    var replacementColors: IntArray? = null,
    var pitch: Int,
    var yaw: Int,
    var roll: Int,
    var translateX: Int,
    var translateY: Int,
    var zoom: Int,
    var ambient: Int,
    var attenuation: Int,
    var scaleX: Int,
    var scaleY: Int,
    var scaleZ: Int
    )




