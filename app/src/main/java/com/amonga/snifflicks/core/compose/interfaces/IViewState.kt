package com.amonga.snifflicks.core.compose.interfaces

/**
 * It contains all the relevant information that the view needs to display, such as data, loading status, error messages, etc.
 * ViewState is immutable and can only be changed by the model layer, which reacts to user intents and performs business logic.
 * The UI layer(View, Activity, Fragment etc.) observes the ViewState and renders it accordingly
 */
interface IViewState
