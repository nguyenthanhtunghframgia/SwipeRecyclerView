package com.example.myapplication.controller

import com.example.myapplication.model.Project

interface SwipeController {
    fun actionEdit(position: Int)

    fun actionDelete(position: Int)
}
