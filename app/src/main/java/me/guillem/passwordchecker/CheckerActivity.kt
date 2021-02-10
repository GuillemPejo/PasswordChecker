package me.guillem.passwordchecker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import me.guillem.passwordchecker.databinding.ActivityCheckerBinding

class CheckerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckerBinding
    private var color: Int = R.color.weak

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckerBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val passwordStrengthCalculator = PasswordStrengthCalculator()
        binding.passwordInput.addTextChangedListener(passwordStrengthCalculator)

        // Observers
        passwordStrengthCalculator.strengthLevel.observe(this, Observer {strengthLevel ->
            displayStrengthLevel(strengthLevel)
        })
        passwordStrengthCalculator.strengthColor.observe(this, Observer {strengthColor ->
            color = strengthColor
        })

        passwordStrengthCalculator.lowerCase.observe(this, Observer {value ->
            displayPasswordSuggestions(value, binding.lowerCaseImg, binding.lowerCaseTxt)
        })
        passwordStrengthCalculator.upperCase.observe(this, Observer {value ->
            displayPasswordSuggestions(value, binding.upperCaseImg, binding.upperCaseTxt)
        })
        passwordStrengthCalculator.digit.observe(this, Observer {value ->
            displayPasswordSuggestions(value, binding.digitImg, binding.digitTxt)
        })
        passwordStrengthCalculator.specialChar.observe(this, Observer {value ->
            displayPasswordSuggestions(value, binding.specialCharImg, binding.specialCharTxt)
        })
    }

    private fun displayPasswordSuggestions(value: Int, imageView: ImageView, textView: TextView) {
        if(value == 1){
            imageView.setColorFilter(ContextCompat.getColor(this, R.color.bulletproof))
            textView.setTextColor(ContextCompat.getColor(this, R.color.bulletproof))
        }else{
            imageView.setColorFilter(ContextCompat.getColor(this, R.color.grey))
            textView.setTextColor(ContextCompat.getColor(this, R.color.grey))
        }
    }

    private fun displayStrengthLevel(strengthMode: StrengthModes) {
        binding.button.isEnabled = strengthMode == StrengthModes.BULLETPROOF

        binding.strengthLevelTxt.text = strengthMode.name
        binding.strengthLevelTxt.setTextColor(ContextCompat.getColor(this, color))
        binding.strengthLevelIndicator.setBackgroundColor(ContextCompat.getColor(this, color))
    }
}