package me.guillem.passwordchecker

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.lifecycle.MutableLiveData
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.roundToLong
import kotlin.math.sign

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
        var lwCounter: Int = 0;
        var upCounter: Int = 0;
        var dgCounter: Int = 0;
        var scCounter: Int = 0;

        val n = 0;
        Log.e("Aqui", 26.toDouble().toString())
        Log.e("Aqui", 26.toDouble().pow(lwCounter).toString())

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

        //Calculate probabilities

        val total_lower = Constants.NUM_LETTERS_LOWER.toDouble().pow(lwCounter)
        val total_upper = Constants.NUM_LETTERS_UPPER.toDouble().pow(upCounter)
        val total_digits = Constants.NUM_DIGITS.toDouble().pow(dgCounter).toInt()
        val total_symbols = Constants.NUM_SYMBOLS.toDouble().pow(scCounter).toInt()

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

/*
        if (minutes.roundToInt() > 0.001)

            Log.e("SI","Minutes: ${minutes.roundToInt()}")

        if (seconds.roundToInt() > 0.001)
            if (milli.roundToInt() > 0.001){
                Log.e("SI","Milliseconds: ${milli}")
            }else{
                Log.e("SI","INSTANTANI")
            }
            Log.e("SI","Seconds: ${seconds.roundToInt()}")
*/

        when{
            hours<Constants.MILISECOND_TO_HOUR-> Log.e("SI","INSTANTANI")
            hours<Constants.SECOND_TO_HOUR && hours>Constants.MILISECOND_TO_HOUR-> Log.e("SI","Milisegons: $milli")
            hours<Constants.MINUTE_TO_HOUR && hours>Constants.SECOND_TO_HOUR-> Log.e("SI","Segons: $seconds")
            hours<Constants.HOUR_TO_HOUR && hours>Constants.MINUTE_TO_HOUR-> Log.e("SI","Minuts: $minutes")
            hours<Constants.DAY_TO_HOUR && hours>Constants.HOUR_TO_HOUR-> Log.e("SI","Hores: $hours")
            hours<Constants.WEEK_TO_HOUR && hours>Constants.DAY_TO_HOUR-> Log.e("SI","Dies: $days")
            hours<Constants.MONTH_TO_HOUR && hours>Constants.WEEK_TO_HOUR-> Log.e("SI","Setmanes: $weeks")
            hours<Constants.YEAR_TO_HOUR && hours>Constants.MONTH_TO_HOUR-> Log.e("SI","Mesos: $months")
            hours<Constants.CENTURY_TO_HOUR && hours>Constants.YEAR_TO_HOUR-> Log.e("SI","Anys: $years")
            hours<Constants.MILLENNIUM_TO_HOUR && hours>Constants.CENTURY_TO_HOUR-> Log.e("SI","Segles: $years")
            hours>Constants.MILLENNIUM_TO_HOUR-> Log.e("SI","Infinite time")

        }


        //return "num Multi: $numMulti, n1: $num1,  n2: $num2, n3: $num3, n4: $num4,"

         return "Hours: $hours"




        //result in minutes $resultinminutes, result in seconds $resultinseconds,"


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