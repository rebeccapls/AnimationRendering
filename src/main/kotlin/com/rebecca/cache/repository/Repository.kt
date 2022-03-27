package com.rebecca.cache.repository

interface Repository<T> {
    fun findById(id: Int): T?
    fun getCount(): Int
}
