package com.example.lint

import java.lang.Exception

class SomeProvider : BaseProvider() {
    fun some() {
        try {
            val s = 2
        } catch(e: Exception) {

		}
    }

}