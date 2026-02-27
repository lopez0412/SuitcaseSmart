package com.loptech.suitcasesmart.usecases.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.loptech.suitcasesmart.R
import com.loptech.suitcasesmart.model.domain.SignInState
import com.loptech.suitcasesmart.ui.theme.AviationNavy
import com.loptech.suitcasesmart.ui.theme.GMAILCOLOR
import com.loptech.suitcasesmart.usecases.common.views.EventDialog
import com.loptech.suitcasesmart.usecases.common.controls.RoundedButton
import com.loptech.suitcasesmart.usecases.common.controls.SocialMediaButton
import com.loptech.suitcasesmart.usecases.common.controls.TransparentTextField

@Composable
fun LoginScreen(
    state: SignInState,
    onLogin: (String, String) -> Unit,
    onNavigateToRegister: () -> Unit,
    onDismissDialog: () -> Unit,
    onSignInClick: () -> Unit,
){
    //email password login variables
    val emailValue = rememberSaveable{ mutableStateOf("") }
    val passwordValue = rememberSaveable{ mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AviationNavy)
    ){
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.play_store_icon),
                contentDescription = "SuitcaseSmart",
                modifier = Modifier.size(180.dp)
            )
        }
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(540.dp),
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(
                    topStartPercent = 8,
                    topEndPercent = 8
                )
            ) {
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceEvenly){
                    Text(
                        text = "Bienvenid@!",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Text(
                        text = "Inicia sesión para continuar",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)){
                        //MARK: - TextField Telefono
                        TransparentTextField(
                            textFieldValue = emailValue,
                            textLabel = "Email",
                            keyboardType = KeyboardType.Phone,
                            keyboardActions = KeyboardActions(
                                onNext = {
                                    focusManager.moveFocus(FocusDirection.Down)
                                }
                            ),
                            imeAction = ImeAction.Next
                        )//: TextField Telefono

                        //MARK: - TextField Password
                        TransparentTextField(
                            textFieldValue = passwordValue,
                            textLabel = "Contraseña",
                            keyboardType = KeyboardType.Password,
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                    //onLogin(numTelValue.value, passwordValue.value)
                                }
                            ),
                            imeAction = ImeAction.Done,
                            trailingIcon = {
                                IconButton(onClick = {
                                    passwordVisibility = !passwordVisibility
                                }
                                ) {
                                    Icon(
                                        painter = if (passwordVisibility) {
                                            painterResource(id = R.drawable.ic_visibility)
                                        } else {
                                            painterResource(id = R.drawable.ic_visibility_off)
                                        },
                                        contentDescription = "Visibility icon"
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisibility) {
                                VisualTransformation.None
                            } else {
                                PasswordVisualTransformation()
                            }
                        ) //: TextField Password

                        //MARK: - Forgot Password
                        Text(
                            text = "Olvidaste tu contraseña?",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.align(Alignment.End)
                        )
                    }
                    //MARK: - Login Button
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        //Rounded Button
                        RoundedButton(
                            text = "Iniciar Sesión",
                            displayProgressBar = state.displayProgressBar,
                            onClick = {
                                onLogin(emailValue.value, passwordValue.value)
                            }
                        )//: Rounded Button

                        SocialMediaButton(
                            text = "Inicial Sesión con Google",
                            onClick =  onSignInClick ,
                            socialMediaColor = GMAILCOLOR
                        )

                        ClickableText(
                            text = buildAnnotatedString {
                                append("No tienes una cuenta? ")

                                withStyle(
                                    style = SpanStyle(
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                ){
                                    append("Registrate")
                                }
                            }
                        ){
                            onNavigateToRegister()
                        }
                    }


                }//: Column
            }//: Surface

        }//: Box

        if (state.signInError != null) {
            //Event Dialog
            EventDialog(errorMessage = state.signInError, onDismiss = onDismissDialog)
        }
    }

}

/*
Button(onClick = onSignInClick) {
            Text(text = "Sign in")
        }
 */
