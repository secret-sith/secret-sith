package com.secret.palpatine.ui.login

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.secret.palpatine.ui.mainmenu.MainMenuActivity

import com.secret.palpatine.R
import com.secret.palpatine.databinding.ActivityLoginBinding
import com.secret.palpatine.ui.BaseActivity
import com.secret.palpatine.ui.signup.SignupActivity

class LoginActivity : BaseActivity(), View.OnClickListener {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setProgressBar(binding.loading)

        auth = Firebase.auth
        Log.w("com.secret.palpatine", "ADASFDASF")

        binding.login.setOnClickListener(this)
        binding.toSignUp.setOnClickListener(this)


        loginViewModel = ViewModelProviders.of(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)

        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            binding.login.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                binding.email.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                binding.password.error = getString(loginState.passwordError)
            }
        })

        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

            hideProgressBar()
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                updateUiWithUser(loginResult.success)
            }
            setResult(Activity.RESULT_OK)

        })

        binding.email.afterTextChanged {
            loginViewModel.loginDataChanged(
                binding.email.text.toString(),
                binding.password.text.toString()
            )
        }

        binding.password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    binding.email.text.toString(),
                    binding.password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        loginViewModel.login(
                            binding.email.text.toString(),
                            binding.password.text.toString()
                        )
                }
                false
            }
        }

    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUiWithUser(currentUser)
    }

    private fun updateUiWithUser(model: FirebaseUser?) {

        if (model != null) {
            val welcome = getString(R.string.welcome)
            val displayName = model!!.displayName
            // TODO : initiate successful logged in experience
            Toast.makeText(
                applicationContext,
                "$welcome $displayName",
                Toast.LENGTH_LONG
            ).show()

            val intent = Intent(this, MainMenuActivity::class.java).apply {
            }
            startActivity(intent)
            finish()
        }

    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.login -> {
                showProgressBar()
                loginViewModel.login(
                    binding.email.text.toString(),
                    binding.password.text.toString()
                )

            }
            R.id.toSignUp -> {

                val intent = Intent(this, SignupActivity::class.java).apply {
                }
                startActivity(intent)
                finish()
            }
        }
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}
