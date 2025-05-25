package com.amonga.snifflicks.module

import com.amonga.snifflicks.R
import android.os.Bundle
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.amonga.snifflicks.core.compose.activity.ComposeBaseActivity
import com.amonga.snifflicks.core.compose.interfaces.ISideEffect
import com.amonga.snifflicks.core.compose.viewmodel.BaseViewModel
import com.amonga.snifflicks.module.event.MainActivityEvent
import com.amonga.snifflicks.module.genre.GenreSelectionFragment
import com.amonga.snifflicks.module.movie.fragment.MovieDetailsFragment
import com.amonga.snifflicks.module.movie.fragment.MovieListFragment
import com.amonga.snifflicks.module.sideeffect.MainActivitySideEffect
import com.amonga.snifflicks.module.sideeffect.MainActivitySideEffect.OpenLanguageSelectionFragment
import com.amonga.snifflicks.module.viewmodel.MainActivityViewModel
import com.amonga.snifflicks.module.viewstate.MainActivityViewState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComposeBaseActivity<MainActivityViewState>() {

    private val vm by viewModels<MainActivityViewModel>()
    override fun getVMInstance(): BaseViewModel<*, *, MainActivityViewState> = vm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
    }

    @Composable
    override fun ActivityContent(initialState: State<MainActivityViewState>) {
        val state = initialState.value

        if (state.isPasswordDialogVisible) {
            PasswordVerificationDialog()
        }
    }

    override fun onSideEffectReceived(sideEffect: ISideEffect) {
        when (val sd = sideEffect as? MainActivitySideEffect) {
            OpenLanguageSelectionFragment -> {
                this.supportFragmentManager.beginTransaction()
                    .replace(R.id.fl_container, GenreSelectionFragment())
                    .commit()
            }

            is MainActivitySideEffect.OpenMovieListFragment -> {
                this.supportFragmentManager.beginTransaction()
                    .replace(R.id.fl_container, MovieListFragment.newInstance(sd.selectedGenreIds))
                    .commit()
            }

            is MainActivitySideEffect.OpenMovieDetailsFragment -> {
                this.supportFragmentManager.beginTransaction()
                    .replace(R.id.fl_container, MovieDetailsFragment.newInstance(sd.movieId))
                    .addToBackStack("MovieDetailsFragment")
                    .commit()
            }

            null -> {}
        }
    }

    @Composable
    private fun PasswordVerificationDialog() {
        var password by remember { mutableStateOf("") }

        Dialog(
            onDismissRequest = {
            },
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false,
                usePlatformDefaultWidth = false
            )
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(dimensionResource(R.dimen.spacing_medium)),
                colors = CardDefaults.cardColors(containerColor = Color.Black),
                shape = MaterialTheme.shapes.medium
            ) {
                Column(
                    modifier = Modifier
                        .padding(dimensionResource(R.dimen.spacing_large)),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_password_shield),
                        contentDescription = stringResource(R.string.password),
                        modifier = Modifier.size(dimensionResource(R.dimen.spacing_wide)),
                        colorFilter = ColorFilter.tint(Color.White)
                    )

                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_medium)))

                    Text(
                        text = stringResource(R.string.verify_password),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = stringResource(R.string.enter_password),
                        color = Color.LightGray,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = {
                            Text(
                                stringResource(R.string.password),
                                color = Color.LightGray
                            )
                        },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors().copy(
                            focusedLabelColor = Color.Red,
                            unfocusedLabelColor = Color.LightGray,
                            cursorColor = Color.Red,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            disabledTextColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            if (password.isNotEmpty()) {
                                vm.processEvent(MainActivityEvent.OnPasswordVerify(password))
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text(
                            text = stringResource(R.string.verify),
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}