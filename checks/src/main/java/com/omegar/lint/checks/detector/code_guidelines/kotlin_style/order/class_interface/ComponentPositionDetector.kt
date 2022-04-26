package com.omegar.lint.checks.detector.code_guidelines.kotlin_style.order.class_interface

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.*
import java.lang.Integer.min

@Suppress("UnstableApiUsage")
class ComponentPositionDetector : Detector(), Detector.UastScanner {
	companion object {
		/** Issue describing the problem and pointing to the detector implementation */
		@JvmField
		val ISSUE: Issue = Issue.create(
			id = "OMEGA_USE_CLASS_COMPONENTS_IN_CORRECT_ORDER",
			briefDescription = "Place class members in correct order",
			explanation = """
                  Order warning.
                  http://wiki.omega-r.club/dev-android-code#rec228155171
                    """,
			category = Category.CORRECTNESS,
			priority = 7,
			severity = Severity.WARNING,
			implementation = Implementation(
				ComponentPositionDetector::class.java,
				Scope.JAVA_FILE_SCOPE
			)
		)

		private const val COMPANION_NAME = "Companion"
		private const val MODIFIERS_RANK_MESSAGE =
			"Class members with access modifiers should be positioned in the following order:\n" +
				"abstract, override, public, internal, protected, private.\n"

		// Access modifiers rank
		private val modifiers = arrayOf(
			Pair("abstract", 1),
			Pair("override", 2),
			Pair("public", 3),
			Pair("internal", 4),
			Pair("protected", 5),
			Pair("private", 6),
			Pair("", 3),
		)

		// 1. companion object members
		private const val COMPANION_OBJECT = "const val"
		private const val COMPANION_OBJECT_RANK = 1
		private const val COMPANION_OBJECT_MESSAGE = "Companion object should be the first"
		private val COMPANION_OBJECT_REGEX_LIST = makeRegexList(COMPANION_OBJECT)
		private val COMPANION_OBJECT_PARAMS =
			StaticParams(COMPANION_OBJECT_REGEX_LIST, COMPANION_OBJECT_RANK, modifiers.last().second,COMPANION_OBJECT_MESSAGE)

		// 2. val + var variables
		private const val VAL = "val"
		private const val VAR = "var"
		private const val VARIABLES_RANK = 2
		private const val VARIABLES_MESSAGE =
			"Variables should be positioned earlier than constructors, functions, enums, interfaces and classes"
		private val VAL_REGEX_LIST = makeRegexList(VAL)
		private val VAR_REGEX_LIST = makeRegexList(VAR)
		private val VAL_PARAMS = StaticParams(VAL_REGEX_LIST, VARIABLES_RANK, modifiers.last().second, VARIABLES_MESSAGE)
		private val VAR_PARAMS = StaticParams(VAR_REGEX_LIST, VARIABLES_RANK, modifiers.last().second, VARIABLES_MESSAGE)

		// 3. constructors and inits
		private const val CONSTRUCTOR = "constructor"
		private const val CONSTRUCTOR_RANK = 3
		private const val CONSTRUCTOR_MESSAGE = "Constructors should be positioned earlier than functions, enums, interfaces and classes"
		private val CONSTRUCTOR_REGEX_LIST = makeRegexList(CONSTRUCTOR)
		private val CONSTRUCTOR_PARAMS = StaticParams(CONSTRUCTOR_REGEX_LIST, CONSTRUCTOR_RANK, modifiers.last().second, CONSTRUCTOR_MESSAGE)

		// 4. functions
		private const val FUNCTION = "fun"
		private const val FUNCTION_RANK = 4
		private const val FUNCTION_MESSAGE = "Functions should be positioned earlier than  enums, interfaces and classes"
		private val FUNCTION_REGEX_LIST = makeRegexList(FUNCTION)
		private val FUNCTION_PARAMS = StaticParams(FUNCTION_REGEX_LIST, FUNCTION_RANK, modifiers.last().second, FUNCTION_MESSAGE)

		// 5. enums
		private const val ENUM = "enum"
		private const val ENUM_RANK = 5
		private const val ENUM_MESSAGE = "Enums should be positioned earlier than interfaces and classes"
		private val ENUM_REGEX_LIST = makeRegexList(ENUM)
		private val ENUM_PARAMS = StaticParams(ENUM_REGEX_LIST, ENUM_RANK, modifiers.last().second, ENUM_MESSAGE)

		// 6. interfaces
		private const val INTERFACE = "interface"
		private const val INTERFACE_RANK = 6
		private const val INTERFACE_MESSAGE = "Interfaces should be positioned earlier than classes"
		private val INTERFACE_REGEX_LIST = makeRegexList(INTERFACE)
		private val INTERFACE_PARAMS = StaticParams(INTERFACE_REGEX_LIST, INTERFACE_RANK, modifiers.last().second, INTERFACE_MESSAGE)

		// 7. classes
		private const val CLASS = "class"
		private const val CLASS_RANK = 7
		private val CLASS_REGEX_LIST = makeRegexList(CLASS)

		private fun makeRegexList(value: String): List<Regex> {
			return listOf(
				Regex("^${modifiers[0].first} $value"),
				Regex("^${modifiers[1].first} $value"),
				Regex("^${modifiers[2].first} $value"),
				Regex("^${modifiers[3].first} $value"),
				Regex("^${modifiers[4].first} $value"),
				Regex("^${modifiers[5].first} $value"),
				Regex("^${modifiers[6].first}${value}")
			)
		}

	}

	/**
	 * Sorting declarations by file's lines
	 * node.uastDeclarations gives elements in wrong order
	 * https://github.com/JetBrains/intellij-community/blob/master/uast/uast-java/src/org/jetbrains/uast/java/declarations/JavaUClass.kt
	 */

	override fun getApplicableUastTypes(): List<Class<out UElement?>> = listOf(UClass::class.java)

	override fun createUastHandler(context: JavaContext) = object: UElementHandler() {
		override fun visitClass(node: UClass) {
			val name = node.name ?: return
			val lines = node.text.lines().toMutableList().apply {
				removeFirst()
				removeLast()
				removeIf { it == "" }
			}
			val sortedDeclarationList = getSortedDeclarationList(lines, node.uastDeclarations)

			if (name != COMPANION_NAME) {
				var currentRank = Pair(0, 0)
				sortedDeclarationList.forEach { declaration ->
					val text = declaration.text ?: return
					val currentParams = CurrentParams(context, text, node, declaration)

					/** 1) It cannot find companion object*/
					currentRank = checkOrder(currentParams, COMPANION_OBJECT_PARAMS, currentRank)

					/** 2) Variables */
					currentRank = checkOrder(currentParams, VAL_PARAMS, currentRank)
					currentRank = checkOrder(currentParams, VAR_PARAMS, currentRank)

					/** 3) Constructors */
					currentRank = checkOrder(currentParams, CONSTRUCTOR_PARAMS, currentRank)

					/** 4 Functions */
					currentRank = checkOrder(currentParams, FUNCTION_PARAMS, currentRank)

					/** 5 Enums */
					currentRank = checkOrder(currentParams, ENUM_PARAMS, currentRank)

					/** 6) Interfaces */
					currentRank = checkOrder(currentParams, INTERFACE_PARAMS, currentRank)

					/** 7) Classes */
					val classRegex = CLASS_REGEX_LIST.firstOrNull { text.contains(it) }
					if (classRegex != null) {
						currentRank = Pair(CLASS_RANK, modifiers.last().second)
					}
				}
			}
		}
	}

	private fun getSortedDeclarationList(
		lines: List<String>,
		listUDeclaration: List<UDeclaration>,
	): List<UDeclaration> {
		val list = mutableListOf<UDeclaration>()
		lines.forEach { line ->
			listUDeclaration.forEach { declaration ->
				val text = declaration.text
				if (text.substring(0, min(text.length, line.length)).trim() == line.trim()) {
					list.add(declaration)
				}
			}
		}
		return list
	}

	private fun checkOrder(currentParams: CurrentParams, staticParams: StaticParams, currentRank: Pair<Int, Int>): Pair<Int, Int> {
		val isVariable = staticParams.regexList.firstOrNull { currentParams.text.contains(it) }
		if (isVariable != null) {
			staticParams.modifierRank = modifiers.first { currentParams.text.contains(it.first) }.second
			if (currentRank.first <= staticParams.rank && currentRank.second <= staticParams.modifierRank) {
				return Pair(staticParams.rank, staticParams.modifierRank)
			} else if (currentRank.second > staticParams.modifierRank) {
				makeContextReport(currentParams.context, currentParams.node, currentParams.declaration, MODIFIERS_RANK_MESSAGE)
			} else {
				makeContextReport(currentParams.context, currentParams.node, currentParams.declaration, staticParams.message)
			}
		}
		return currentRank
	}

	private fun makeContextReport(context: JavaContext, node: UClass, declaration: UDeclaration, message: String) {
		context.report(
			ISSUE,
			node,
			context.getNameLocation(declaration),
			"$message. ${ISSUE.getExplanation(TextFormat.TEXT)}"
		)
	}

	private class StaticParams(
		val regexList: List<Regex>,
		val rank: Int,
		var modifierRank: Int,
		val message: String
	)

	private class CurrentParams(
		val context: JavaContext,
		val text: String,
		val node: UClass,
		val declaration: UDeclaration
	)
}
