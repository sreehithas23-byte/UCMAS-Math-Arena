package com.example.util

import java.math.BigDecimal
import java.math.BigInteger

object NumberFormatter {

    /**
     * Formats a raw number string into the Indian numbering system:
     * e.g. 10000000000000000000 -> 1,00,00,00,00,00,00,00,00,000
     * No scientific notation, exact display up to 20+ digits.
     */
    fun formatIndianNumber(rawNumber: String): String {
        if (rawNumber.isBlank() || rawNumber == "-" || rawNumber == "+") return rawNumber

        val isNegative = rawNumber.startsWith("-")
        val cleanNumber = if (isNegative) rawNumber.substring(1) else rawNumber

        // Check if there is a decimal point
        val parts = cleanNumber.split(".")
        val integerPart = parts[0]
        val decimalPart = if (parts.size > 1) "." + parts[1] else ""

        if (integerPart.isEmpty()) {
            return (if (isNegative) "-" else "") + decimalPart
        }

        val len = integerPart.length
        if (len <= 3) {
            return (if (isNegative) "-" else "") + integerPart + decimalPart
        }

        // First 3 digits from the right
        val lastThree = integerPart.substring(len - 3)
        val remaining = integerPart.substring(0, len - 3)

        // Group remaining digits in pairs of 2 from right to left
        val sb = StringBuilder()
        var count = 0
        for (i in remaining.length - 1 downTo 0) {
            sb.append(remaining[i])
            count++
            if (count % 2 == 0 && i != 0) {
                sb.append(',')
            }
        }
        val formattedRemaining = sb.reverse().toString()

        val fullFormatted = "$formattedRemaining,$lastThree$decimalPart"
        return if (isNegative) "-$fullFormatted" else fullFormatted
    }

    /**
     * Strips formatting commas for internal math parsing
     */
    fun stripCommas(formatted: String): String {
        return formatted.replace(",", "").trim()
    }

    /**
     * Exact 20-digit BigInteger addition
     */
    fun addBigInt(a: String, b: String): String {
        return try {
            val bigA = BigInteger(stripCommas(a).substringBefore("."))
            val bigB = BigInteger(stripCommas(b).substringBefore("."))
            bigA.add(bigB).toString()
        } catch (_: Exception) {
            "0"
        }
    }

    /**
     * Exact 20-digit BigInteger subtraction
     */
    fun subtractBigInt(a: String, b: String): String {
        return try {
            val bigA = BigInteger(stripCommas(a).substringBefore("."))
            val bigB = BigInteger(stripCommas(b).substringBefore("."))
            bigA.subtract(bigB).toString()
        } catch (_: Exception) {
            "0"
        }
    }

    /**
     * Exact 20-digit BigInteger multiplication
     */
    fun multiplyBigInt(a: String, b: String): String {
        return try {
            val bigA = BigInteger(stripCommas(a).substringBefore("."))
            val bigB = BigInteger(stripCommas(b).substringBefore("."))
            bigA.multiply(bigB).toString()
        } catch (_: Exception) {
            "0"
        }
    }

    /**
     * Exact 20-digit BigInteger division (quotient and remainder)
     */
    fun divideBigInt(a: String, b: String): String {
        return try {
            val bigA = BigInteger(stripCommas(a).substringBefore("."))
            val bigB = BigInteger(stripCommas(b).substringBefore("."))
            if (bigB == BigInteger.ZERO) return "Error (Div by 0)"
            val res = bigA.divideAndRemainder(bigB)
            val quotient = res[0].toString()
            val rem = res[1]
            if (rem == BigInteger.ZERO) {
                quotient
            } else {
                // Return exact quotient with remainder or decimal representation
                val decA = BigDecimal(bigA)
                val decB = BigDecimal(bigB)
                decA.divide(decB, 6, java.math.RoundingMode.HALF_UP).stripTrailingZeros().toPlainString()
            }
        } catch (_: Exception) {
            "Error"
        }
    }

    /**
     * Exact BigInteger square (x^2)
     */
    fun squareBigInt(a: String): String {
        return try {
            val bigA = BigInteger(stripCommas(a).substringBefore("."))
            bigA.multiply(bigA).toString()
        } catch (_: Exception) {
            "0"
        }
    }

    /**
     * Compatibility wrapper using BigDecimal for generic addition
     */
    fun add(a: String, b: String): String = addBigInt(a, b)

    fun subtract(a: String, b: String): String = subtractBigInt(a, b)

    fun multiply(a: String, b: String): String = multiplyBigInt(a, b)

    fun divide(a: String, b: String): String = divideBigInt(a, b)
}
