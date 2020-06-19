package com.secret.palpatine.ui.signup

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.secret.palpatine.R
import com.secret.palpatine.data.model.user.UserRepository
import com.secret.palpatine.databinding.ActivitySignupBinding
import com.secret.palpatine.ui.BaseActivity
import com.secret.palpatine.ui.login.LoginActivity
import com.secret.palpatine.ui.login.afterTextChanged

/**
 * Created by Florian Fuchs on 05.06.2020.
 */
class SignupActivity : BaseActivity(), View.OnClickListener {
    // [START declare_auth]
    private lateinit var auth: FirebaseAuth

    // [END declare_auth]
    private lateinit var viewModel: SignupViewModel

    private lateinit var binding: ActivitySignupBinding


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setProgressBar(binding.loading)


        //deep links
        Firebase.dynamicLinks
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData ->
                // Get deep link from result (may be null if no link is found)
                var deepLink: Uri? = null
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link
                }
                Log.d("Deep Link", deepLink.toString())

                // ...
            }
            .addOnFailureListener(this) { e -> Log.w(TAG, "getDynamicLink:onFailure", e) }

        // Buttons
        binding.signup.setOnClickListener(this)
        binding.login.setOnClickListener(this)

        // [START initialize_auth]
        // Initialize Firebase Auth
        auth = Firebase.auth

        viewModel = SignupViewModel(auth, UserRepository())

        viewModel.signupFormState.observe(this@SignupActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            binding.signup.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                binding.email.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                binding.password.error = getString(loginState.passwordError)
            }
            if (loginState.nameError != null) {
                binding.username.error = getString(loginState.nameError)
            }
        })

        viewModel.signupResult.observe(this@SignupActivity, Observer {
            val loginResult = it ?: return@Observer

            binding.loading.visibility = View.GONE
            if (loginResult.error != null) {
                showSignupFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                updateUI(loginResult.success)
            }
            setResult(Activity.RESULT_OK)

            //Complete and destroy login activity once successful
        })

        binding.username.afterTextChanged {
            viewModel.signUpDataChanged(
                binding.email.text.toString(),
                binding.password.text.toString(),
                binding.username.text.toString()
            )
        }
        binding.email.afterTextChanged {
            viewModel.signUpDataChanged(
                binding.email.text.toString(),
                binding.password.text.toString(),
                binding.username.text.toString()
            )
        }

        binding.password.apply {
            afterTextChanged {
                viewModel.signUpDataChanged(
                    binding.email.text.toString(),
                    binding.password.text.toString(),
                    binding.username.text.toString()
                )
            }


            // [END initialize_auth]
        }
    }

    private fun showSignupFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }

    // [START on_start_check_user]
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }
    // [END on_start_check_user]


    private fun updateUI(user: FirebaseUser?) {
        hideProgressBar()
        if (user != null) {
            val welcome = getString(R.string.welcome)
            val displayName = user?.displayName
            // TODO : initiate successful logged in experience
            Toast.makeText(
                applicationContext,
                "$welcome $displayName",
                Toast.LENGTH_SHORT
            ).show()

            if (user != null) {
                val intent = Intent(this, LoginActivity::class.java).apply {
                }
                startActivity(intent)
            }
        }


    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.signup -> {

                showProgressBar()
                viewModel.signup(
                    binding.email.text.toString(),
                    binding.password.text.toString(),
                    binding.username.text.toString()
                )

            }
            R.id.login -> {
                val intent = Intent(this, LoginActivity::class.java).apply {
                }
                startActivity(intent)
                finish()
            }
        }
    }

    companion object {
        private const val TAG = "EmailPassword"
    }
}