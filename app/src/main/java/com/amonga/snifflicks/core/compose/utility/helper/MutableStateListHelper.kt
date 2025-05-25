package com.amonga.snifflicks.core.compose.utility.helper

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.runtime.toMutableStateList
import androidx.compose.runtime.toMutableStateMap

/**
 * This function, `rememberMutableStateListOf`, is a composable function that creates a [SnapshotStateList]
 * which is a mutable state holder for a list of elements of type `T`. It uses [rememberSaveable]
 * to ensure the state is remembered across recompositions and saved during the activity's lifecycle
 * (e.g., during configuration changes such as screen rotations and inside lazy layouts to remember when
 * the composable is bring back to the composition after scrolling).
 *
 * The function is defined as inline with a reified type parameter `T`, which allows the type `T`
 * to be used within the function. This enables the function to work with any type of elements.
 *
 * The `rememberSaveable` function takes a `saver` parameter which is a `listSaver` created with two lambdas:
 *
 * 1. `save`: This lambda takes the `stateList` and converts it to a list to be saved.
 *    Before converting, it checks if the list is not empty. If the list is not empty, it checks if the first
 *    element can be saved using the `canBeSaved` function. If the element cannot be saved, an
 *    `IllegalStateException` is thrown.
 *
 * 2. `restore`: This lambda takes the saved list and converts it back to a `SnapshotStateList` using the
 *    `toMutableStateList` extension function.
 *
 * The `rememberSaveable` block initializes the state list with the provided elements (defaulting to an empty array)
 * converted to a mutable state list using `toMutableStateList`.
 *
 * @param elements Vararg parameter of elements of type `T` to initialize the state list.
 * @return A `SnapshotStateList` of elements of type `T`.
 * @throws IllegalStateException If an element in the list cannot be saved.
 */
object MutableStateListHelper {

	@Composable
	inline fun <reified T: Any> rememberMutableStateListOf(vararg elements: T): SnapshotStateList<T> {
		return rememberSaveable(saver = listSaver(save = { stateList ->
			if (stateList.isNotEmpty()) {
				val first = stateList.first()
				if (!canBeSaved(first)) {
					throw IllegalStateException("${first::class} cannot be saved. By default only types which can be stored in the Bundle class can be saved.")
				}
			}
			stateList.toList()

		}, restore = { it.toMutableStateList() })) {
			elements.toList().toMutableStateList()
		}
	}

	@Composable
	inline fun <reified T: Any> rememberMutableStateListOf(list: List<T> = listOf()): SnapshotStateList<T> {
		return rememberSaveable(saver = listSaver(save = { stateList ->
			if (stateList.isNotEmpty()) {
				val first = stateList.first()
				if (!canBeSaved(first)) {
					throw IllegalStateException("${first::class} cannot be saved. By default only types which can be stored in the Bundle class can be saved.")
				}
			}
			stateList.toList()

		}, restore = { it.toMutableStateList() })) {
			list.toMutableStateList()
		}
	}
}

/**
 * This function, `rememberMutableStateMapOf`, is a composable function that creates a [SnapshotStateMap],
 * which is a mutable state holder for a map of key-value pairs where the key is a String and the value
 * is of type `T`. It uses [rememberSaveable] to ensure the state is remembered across recompositions and saved
 * during the activity's lifecycle (e.g., during configuration changes such as screen rotations and inside lazy layouts
 * to remember when the composable is bring back to the composition after scrolling).
 * The function is defined as inline with a reified type parameter `T`, which allows the type `T`
 * to be used within the function. This enables the function to work with any type of values.
 *
 * The `rememberSaveable` function takes a `saver` parameter which is a `mapSaver` created with two lambdas:
 *
 * 1. `save`: This lambda takes the `stateMap` and converts it to a map to be saved.
 *    Before converting, it checks if the map is not empty. If the map is not empty, it checks if the first
 *    value in the map can be saved using the `canBeSaved` function. If the value cannot be saved, an
 *    `IllegalStateException` is thrown.
 *
 * 2. `restore`: This lambda takes the saved map and converts it back to a `SnapshotStateMap` using
 *    the `mutableStateMapOf` function. It iterates over the map entries, casting the values to type `T`,
 *    and populates the mutable state map.
 *
 * The `rememberSaveable` block initializes the state map with the provided key-value pairs converted to
 * a mutable state map using `toMutableStateMap`.
 *
 * @param pairs Vararg parameter of key-value pairs where the key is a String and the value is of type `T`.
 * @return A `SnapshotStateMap` with keys as Strings and values of type `T`.
 * @throws IllegalStateException If a value in the map cannot be saved.
 */
object MutableStateMapHelper {

	@Composable
	inline fun <reified VALUE: Any> rememberMutableStateMapOf(vararg pairs: Pair<String, VALUE>): SnapshotStateMap<String, VALUE> {
		return rememberSaveable(saver = mapSaver(save = { stateMap ->
			if (stateMap.isNotEmpty()) {
				val firstValue = stateMap.values.first()
				if (!canBeSaved(firstValue)) {
					throw IllegalStateException("${firstValue::class} cannot be saved. By default only types which can be stored in the Bundle class can be saved.")
				}
			}
			stateMap.toMap()
		}, restore = { map ->
			val mutableStateMap = mutableStateMapOf<String, VALUE>()
			map.forEach { entry ->
				val key = entry.key
				val value = entry.value
				(value as? VALUE)?.let {
					mutableStateMap[key] = it
				}
			}
			mutableStateMap
		})) {
			pairs.toList().toMutableStateMap()
		}
	}

	// Currently only supporting string as key because map saver can only store string as key, will later add functionality to
	// store any data object as string by using serialisation and de-serialisation
	@Composable
	inline fun <reified VALUE: Any> rememberMutableStateMapOf(map: Map<String, VALUE> = mapOf()): SnapshotStateMap<String, VALUE> {
		return rememberSaveable(saver = mapSaver(save = { stateMap ->
			if (stateMap.isNotEmpty()) {
				val firstValue = stateMap.values.first()
				if (!canBeSaved(firstValue)) {
					throw IllegalStateException("${firstValue::class} cannot be saved. By default only types which can be stored in the Bundle class can be saved.")
				}
			}
			stateMap.toMap()
		}, restore = { storeMap ->
			val mutableStateMap = mutableStateMapOf<String, VALUE>()
			storeMap.forEach { entry ->
				val key = entry.key
				val value = entry.value
				(value as? VALUE)?.let {
					mutableStateMap[key] = it
				}
			}
			mutableStateMap
		})) {
			val mutableStateMap = mutableStateMapOf<String, VALUE>()
			map.forEach { entry ->
				val key = entry.key
				val value = entry.value
				(value as? VALUE)?.let {
					mutableStateMap[key] = it
				}
			}
			mutableStateMap
		}
	}
}
