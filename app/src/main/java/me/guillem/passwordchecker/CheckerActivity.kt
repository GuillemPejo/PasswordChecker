package me.guillem.passwordchecker

import android.graphics.Color.*
import android.os.Bundle
import android.text.InputType.*
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import me.guillem.passwordchecker.databinding.ActivityCheckerBinding
import java.math.RoundingMode
import java.text.DecimalFormat


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
        passwordStrengthCalculator.strengthLevel.observe(this, Observer { strengthLevel ->
            displayStrengthLevel(strengthLevel)
        })
        passwordStrengthCalculator.strengthColor.observe(this, Observer { strengthColor ->
            color = strengthColor
        })

        passwordStrengthCalculator.lowerCase.observe(this, Observer { value ->
            displayPasswordSuggestions(value, binding.lowerCaseImg, binding.lowerCaseTxt)
        })
        passwordStrengthCalculator.upperCase.observe(this, Observer { value ->
            displayPasswordSuggestions(value, binding.upperCaseImg, binding.upperCaseTxt)
        })
        passwordStrengthCalculator.digit.observe(this, Observer { value ->
            displayPasswordSuggestions(value, binding.digitImg, binding.digitTxt)
        })
        passwordStrengthCalculator.specialChar.observe(this, Observer { value ->
            displayPasswordSuggestions(value, binding.specialCharImg, binding.specialCharTxt)
        })

        passwordStrengthCalculator.charCount.observe(this, Observer { size ->
            binding.charactersContaining.text = "$size" + getString(R.string.characters_containing)

        })
        passwordStrengthCalculator.crackingTime.observe(this, Observer { time ->
            binding.crackingTimeEstimate.text =  time
        })

        passwordStrengthCalculator.strengthLevel.observe(this, Observer { strengthLevel ->
            when (strengthLevel){
                StrengthModes.NONE -> showReview("")
                StrengthModes.WEAK -> showReview(getString(R.string.weak))
                StrengthModes.MEDIUM -> showReview(getString(R.string.medium))
                StrengthModes.STRONG -> showReview(getString(R.string.strong))
                StrengthModes.BULLETPROOF -> showReview(getString(R.string.bulletprof))
            }
        })

        binding.hide.setOnClickListener {

            when(binding.passwordInput.inputType){
                //HIDEN TO VISIBLE
                129 -> {
                    binding.passwordInput.inputType = 144
                    binding.hide.setImageDrawable(getDrawable(R.drawable.ic_invisible))
                }
                //VISIBLE TO HIDE
                144 -> {
                    binding.passwordInput.inputType = 129
                    binding.hide.setImageDrawable(getDrawable(R.drawable.eye_ic))
                }

            }

        }



    }

    private fun showReview(string: String) {

        binding.review.text =  string
    }

    private fun displayPasswordSuggestions(value: Int, imageView: ImageView, textView: TextView) {
        if (value == 1) {
            imageView.setColorFilter(ContextCompat.getColor(this, R.color.bulletproof))
            textView.setTextColor(ContextCompat.getColor(this, R.color.bulletproof))
        } else {
            imageView.setColorFilter(ContextCompat.getColor(this, R.color.grey))
            textView.setTextColor(ContextCompat.getColor(this, R.color.grey))
        }
    }

    private fun displayStrengthLevel(strengthMode: StrengthModes) {
        binding.button.isEnabled = strengthMode == StrengthModes.BULLETPROOF

        if (strengthMode.name == "NONE") binding.strengthLevelTxt.text = ""
        else binding.strengthLevelTxt.text = strengthMode.name

        binding.strengthLevelTxt.setTextColor(ContextCompat.getColor(this, color))
        binding.strengthLevelIndicator.setBackgroundColor(ContextCompat.getColor(this, color))
    }

}


















