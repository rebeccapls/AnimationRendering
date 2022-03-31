package com.rebecca.util

inline fun <reified R : Any> R.logger() {
    "log: running ${({R::class}.javaClass.enclosingMethod.name)}()"
}

fun String.clean(): String {
    return Regex("[^A-Za-z0-9 ]").replace(this.lowercase(), "").replace(" ", "_")
}




