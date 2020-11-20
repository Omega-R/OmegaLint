package com.example.lint.checks.detector.code_guidelines.kotlin_style.name.abbreviation

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UField
import org.jetbrains.uast.UMethod

@Suppress("UnstableApiUsage")
class AbbreviationDetector : Detector(), Detector.UastScanner {
	companion object {
		/** Issue describing the problem and pointing to the detector implementation */
		@JvmField
		val ISSUE: Issue = Issue.create(
			id = "OMEGA_ABBREVIATION_AS_WORD",
			briefDescription = "Use this abbreviation does not match the coding convention",
			explanation = """
                  Don't use abbreviations.
                  http://wiki.omega-r.club/dev-android-code#rec228153340
                    """,
			category = Category.CORRECTNESS,
			severity = Severity.WARNING,
			implementation = Implementation(
				AbbreviationDetector::class.java,
				Scope.JAVA_FILE_SCOPE
			)
		)

		private val ABBREVIATION_REGEX = Regex("""[A-Z][A-Z]""")
		private val COMPANION_OBJECT_NAME_REGEX = Regex("""^Companion""")
	}

	override fun getApplicableUastTypes(): List<Class<out UElement?>>? {
		return listOf(
			UClass::class.java
		)
	}

	override fun createUastHandler(context: JavaContext): UElementHandler? {
		return object : UElementHandler() {

			override fun visitClass(node: UClass) {
				val companion = node.innerClasses.firstOrNull() ?: return
				node.methods.forEach { method ->
					if (method.name.contains(ABBREVIATION_REGEX)) {
						context.report(
							ISSUE,
							method,
							context.getNameLocation(method),
							method.name + " " + method.parent.text
						)
					}
				}

				node.fields.forEach { field ->
					if (field.name.contains(ABBREVIATION_REGEX)) {
						context.report(
							ISSUE,
							field,
							context.getNameLocation(field),
							field.name + " " + field.parent.text
						)
					}
				}
/*				val name = node.name ?: return
				val text = node.text ?: return*/
/*
				val methods = node.methods
				methods.forEach { method ->
					if (method.name.contains(ABBREVIATION_REGEX) && !isComponentMethod(method)) {
						context.report(
							ISSUE,
							node,
							context.getNameLocation(method),
							method.name
						)
					}
				}

				val fields = node.fields
				fields.forEach { field ->
					if (field.name.contains(ABBREVIATION_REGEX) && !isComponentField(field)) {
						context.report(
							ISSUE,
							node,
							context.getNameLocation(field),
							field.name
						)
					}
				}*/
			}

			/*	private fun getCompanionObject(node: UClass): UClass? {
					val companionObjectClass = node.innerClasses.firstOrNull() ?: return null
					val name = companionObjectClass.name ?: return null
					return if (name.contains(COMPANION_OBJECT_NAME_REGEX)) {
						companionObjectClass
					} else {
						null
					}
				}

				private fun isComponentMethod(currentMethod: UMethod): Boolean {
					val methods = companion?.methods ?: return false

					methods.forEach {
						context.report(
							ISSUE,
							currentMethod,
							context.getNameLocation(currentMethod),
							it.name
						)
					}

					return false
				}

				private fun isComponentField(currentField: UField): Boolean {
					val fields = companion?.fields ?: return false

					fields.forEach {
						context.report(
							ISSUE,
							currentField,
							context.getNameLocation(currentField),
							it.name
						)
					}

					return false
				}*/
		}
	}
}
