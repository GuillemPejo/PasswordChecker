package me.guillem.passwordchecker

import android.text.Editable
import android.text.TextWatcher
import android.widget.TextSwitcher
import androidx.lifecycle.MutableLiveData
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 ** Created by Guillem on 10/02/21.
 */
class PasswordStrengthCalculator : TextWatcher {

    var strengthLevel: MutableLiveData<StrengthModes> = MutableLiveData()
    var strengthColor: MutableLiveData<Int> = MutableLiveData()

    var lowerCase: MutableLiveData<Int> = MutableLiveData()
    var upperCase: MutableLiveData<Int> = MutableLiveData()
    var digit: MutableLiveData<Int> = MutableLiveData()
    var specialChar: MutableLiveData<Int> = MutableLiveData()


    override fun afterTextChanged(p0: Editable?) {

    }


    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}


    override fun onTextChanged(char: CharSequence?, p1: Int, p2: Int, p3: Int) {
        char?.let{
            lowerCase.value = if (char.hasLowerCase()) { 1 } else { 0 }
            upperCase.value = if (char.hasUpperCase()) { 1 } else { 0 }
            digit.value = if (char.hasDigit()) { 1 } else { 0 }
            specialChar.value = if (char.hasSpecialChar()) { 1 } else { 0 }
            calculateStrength(char)
        }
    }

    private fun calculateStrength(password: CharSequence) {
        if(password.length in 0..7){
            strengthColor.value = R.color.weak
            strengthLevel.value = StrengthModes.WEAK
        }else if(password.length in 8..10){
            if(lowerCase.value == 1 || upperCase.value == 1 || digit.value == 1 || specialChar.value == 1){
                strengthColor.value = R.color.medium
                strengthLevel.value = StrengthModes.MEDIUM
            }
        }else if(password.length in 11..16){
            if(lowerCase.value == 1 || upperCase.value == 1 || digit.value == 1 || specialChar.value == 1){
                if(lowerCase.value == 1 && upperCase.value == 1){
                    strengthColor.value = R.color.strong
                    strengthLevel.value = StrengthModes.STRONG
                }
            }
        }else if(password.length > 16){
            if(lowerCase.value == 1 && upperCase.value == 1 && digit.value == 1 && specialChar.value == 1){
                strengthColor.value = R.color.bulletproof
                strengthLevel.value = StrengthModes.BULLETPROOF
            }
        }
    }

    private fun CharSequence.hasLowerCase(): Boolean{
        val pattern: Pattern = Pattern.compile("[a-z]")
        val hasLowerCase: Matcher = pattern.matcher(this)
        return hasLowerCase.find()
    }

    private fun CharSequence.hasUpperCase(): Boolean{
        val pattern: Pattern = Pattern.compile("[A-Z]")
        val hasUpperCase: Matcher = pattern.matcher(this)
        return hasUpperCase.find()
    }

    private fun CharSequence.hasDigit(): Boolean{
        val pattern: Pattern = Pattern.compile("[0-9]")
        val hasDigit: Matcher = pattern.matcher(this)
        return hasDigit.find()
    }

    private fun CharSequence.hasSpecialChar(): Boolean{
        val pattern: Pattern = Pattern.compile("[!@#$%^&*()_=+{}/.<>|\\[\\]~-]")
        val hasSpecialChar: Matcher = pattern.matcher(this)
        return hasSpecialChar.find()
    }

}