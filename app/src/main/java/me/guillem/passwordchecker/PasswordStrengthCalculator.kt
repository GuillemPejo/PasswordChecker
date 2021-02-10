package me.guillem.passwordchecker

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.lifecycle.MutableLiveData
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.math.*

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
    var charCount: MutableLiveData<Int> = MutableLiveData()
    var crackingTime: MutableLiveData<String> = MutableLiveData()


    override fun afterTextChanged(p0: Editable?) {

    }


    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}


    override fun onTextChanged(char: CharSequence?, p1: Int, p2: Int, p3: Int) {
        char?.let {
            lowerCase.value = if (char.hasLowerCase()) {
                1
            } else {
                0
            }
            upperCase.value = if (char.hasUpperCase()) {
                1
            } else {
                0
            }
            digit.value = if (char.hasDigit()) {
                1
            } else {
                0
            }
            specialChar.value = if (char.hasSpecialChar()) {
                1
            } else {
                0
            }
            charCount.value = char.length
            crackingTime.value = calculateCrackingTime(char)
            calculateStrength(char)

        }
    }

    private fun calculateCrackingTime(char: CharSequence?): String {
        var lwCounter = 0;
        var upCounter = 0;
        var dgCounter = 0;
        var scCounter = 0;

        val password = char.toString()

        for (k in password.indices) {

            when {
                // Check for uppercase letters.
                password[k].toString().hasUpperCase() -> upCounter++

                // Check for lowercase letters.
                password[k].toString().hasLowerCase() -> lwCounter++

                // Check for digits.
                password[k].toString().hasDigit() -> dgCounter++

                // Check for special characters
                password[k].toString().hasSpecialChar() -> scCounter++

            }

        }

        return calculateProbabilities(lwCounter, upCounter, dgCounter, scCounter)
    }

    private fun calculateProbabilities(lwCounter: Int, upCounter: Int, dgCounter: Int, scCounter: Int): String {

        //Calculate probabilities

        val total_lower = Constants.NUM_LETTERS_LOWER.toDouble().pow(lwCounter)
        val total_upper = Constants.NUM_LETTERS_UPPER.toDouble().pow(upCounter)
        val total_digits = Constants.NUM_DIGITS.toDouble().pow(dgCounter).toInt()
        val total_symbols = Constants.NUM_SYMBOLS.toDouble().pow(scCounter).toInt()

        //Reduce Keyspace Search by Law of Averages
        val numMulti = ((total_lower * total_upper * total_digits * total_symbols) / 2)

        val hours = (numMulti / Constants.COMPUTER_CALCULATION_HOUR)
        val millennium =  hours * Constants.MILLENNIUM_TO_HOUR
        val century =  hours * Constants.CENTURY_TO_HOUR
        val years =  hours * Constants.YEAR_TO_HOUR
        val months = hours * Constants.MONTH_TO_HOUR
        val weeks =  hours * Constants.WEEK_TO_HOUR
        val days =  hours * Constants.DAY_TO_HOUR
        val minutes = hours * Constants.MINUTE_TO_HOUR
        val seconds = hours * Constants.SECOND_TO_HOUR
        val milli = hours * Constants.MILISECOND_TO_HOUR

        when{
            hours<2.77777778e-7-> return "Instantanly"
            hours<0.00027777777 && hours>=2.77777778e-7-> return "${milli} miliseconds "
            hours<0.01666666666 && hours>=0.00027777777-> return "${seconds} seconds "
            hours<1 && hours>=0.01666666666->return "${minutes} minutes "
            hours<24 && hours>=1 -> return "${hours} hours "
            hours<168 && hours>=24-> return "${days} days "
            hours<5113.5 && hours>=168-> return "${weeks} weeks "
            hours<8766 && hours>= 5113.5 -> return "${months} months "
            hours<87660 && hours>=8766-> return "${years} years "
            hours<8766000&& hours>=87660-> return "${century} century "
            else -> return "A lot ofk time"
        }
    }

    private fun calculateStrength(password: CharSequence) {
        if (password.length in 0..7) {
            strengthColor.value = R.color.weak
            strengthLevel.value = StrengthModes.WEAK
        } else if (password.length in 8..10) {
            if (lowerCase.value == 1 || upperCase.value == 1 || digit.value == 1 || specialChar.value == 1) {
                strengthColor.value = R.color.medium
                strengthLevel.value = StrengthModes.MEDIUM
            }
        } else if (password.length in 11..16) {
            if (lowerCase.value == 1 || upperCase.value == 1 || digit.value == 1 || specialChar.value == 1) {
                if (lowerCase.value == 1 && upperCase.value == 1) {
                    strengthColor.value = R.color.strong
                    strengthLevel.value = StrengthModes.STRONG
                }
            }
        } else if (password.length > 16) {
            if (lowerCase.value == 1 && upperCase.value == 1 && digit.value == 1 && specialChar.value == 1) {
                strengthColor.value = R.color.bulletproof
                strengthLevel.value = StrengthModes.BULLETPROOF
            }
        }
    }

    private fun CharSequence.hasLowerCase(): Boolean {
        val pattern: Pattern = Pattern.compile("[a-z]")
        val hasLowerCase: Matcher = pattern.matcher(this)

        return hasLowerCase.find()
    }

    private fun CharSequence.hasUpperCase(): Boolean {
        val pattern: Pattern = Pattern.compile("[A-Z]")
        val hasUpperCase: Matcher = pattern.matcher(this)
        return hasUpperCase.find()
    }

    private fun CharSequence.hasDigit(): Boolean {
        val pattern: Pattern = Pattern.compile("[0-9]")
        val hasDigit: Matcher = pattern.matcher(this)
        return hasDigit.find()
    }

    private fun CharSequence.hasSpecialChar(): Boolean {
        val pattern: Pattern = Pattern.compile("[!@#$%^&*()_=+{}/.<>|\\[\\]~-]")
        val hasSpecialChar: Matcher = pattern.matcher(this)
        return hasSpecialChar.find()
    }

}