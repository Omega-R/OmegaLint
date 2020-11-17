package com.example.lint

import android.annotation.SuppressLint

class Object() {


	fun functionINObject() {
		when(2) {
			1 -> some()


			2 -> {
				val s = "RIGHT"
				some()
			}
		}
		val someStr = ""
	}


    class some () {
	}
}