package me.guillem.passwordchecker

import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.MutableLiveData
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

    private fun calculateProbabilities(
        lwCounter: Int,
        upCounter: Int,
        dgCounter: Int,
        scCounter: Int
    ): String {

        //Calculate probabilities

        val total_lower = Constants.NUM_LETTERS_LOWER.toDouble().pow(lwCounter)
        val total_upper = Constants.NUM_LETTERS_UPPER.toDouble().pow(upCounter)
        val total_digits = Constants.NUM_DIGITS.toDouble().pow(dgCounter).toInt()
        val total_symbols = Constants.NUM_SYMBOLS.toDouble().pow(scCounter).toInt()

        //Reduce Keyspace Search by Law of Averages
        val numMulti = ((total_lower * total_upper * total_digits * total_symbols) / 2)

        val result = (numMulti / Constants.COMPUTER_CALCULATION_HOUR)
        val millennium = result * Constants.MILLENNIUM_TO_HOUR
        val century = result * Constants.CENTURY_TO_HOUR
        val years = result * Constants.YEAR_TO_HOUR
        val months = result * Constants.MONTH_TO_HOUR
        val weeks = result * Constants.WEEK_TO_HOUR
        val days = result * Constants.DAY_TO_HOUR
        val hours = result * Constants.HOUR_TO_HOUR
        val minutes = result * Constants.MINUTE_TO_HOUR
        val seconds = result * Constants.SECOND_TO_HOUR
        val milli = result * Constants.MILISECOND_TO_HOUR


        return when {
            result < 2.77777778e-7 -> "Instantanly"
            result < 0.00027777777 && result >= 2.77777778e-7 -> "${withTwoDecimals(milli)} miliseconds "
            result < 0.01666666666 && result >= 0.00027777777 -> "${withTwoDecimals(seconds)} seconds "
            result < 1 && result >= 0.01666666666 -> "${withTwoDecimals(minutes)} minutes "
            result < 24 && result >= 1 -> "${withTwoDecimals(hours)} hours "
            result < 168 && result >= 24 -> "${withTwoDecimals(days)} days "
            result < 5113.5 && result >= 168 -> "${withTwoDecimals(weeks)} weeks "
            result < 8766 && result >= 5113.5 -> "${withTwoDecimals(months)} months "
            result < 87660 && result >= 8766 -> "${withTwoDecimals(years)} years "
            result < 8766000 && result >= 87660 -> "${withTwoDecimals(century)} centuries "
            else -> "A lot of time"
        }
    }


    private fun withTwoDecimals(number: Double):String{
        val df = DecimalFormat("#.##")
        return df.format(number)
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