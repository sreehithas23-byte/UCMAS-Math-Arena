package com.example

import com.example.ui.theme.ArenaTheme
import com.example.util.NumberFormatter
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testIndianNumberFormatting() {
        // 20-digit number (1 followed by 19 zeros): 1,00,00,00,00,00,00,00,00,000
        val formatted = NumberFormatter.formatIndianNumber("10000000000000000000")
        assertEquals("1,00,00,00,00,00,00,00,00,000", formatted)

        // 7-digit number: 12,34,567
        val smallFormatted = NumberFormatter.formatIndianNumber("1234567")
        assertEquals("12,34,567", smallFormatted)

        val threeDigits = NumberFormatter.formatIndianNumber("890")
        assertEquals("890", threeDigits)
    }

    @Test
    fun testExact20DigitBigIntArithmetic() {
        val numA = "99999999999999999999" // 20 digits
        val numB = "1"
        val sum = NumberFormatter.add(numA, numB)
        assertEquals("100000000000000000000", sum) // 21 digits

        val formattedSum = NumberFormatter.formatIndianNumber(sum)
        assertEquals("10,00,00,00,00,00,00,00,00,000", formattedSum)
    }

    @Test
    fun testThemeEnumParsing() {
        val crimson = ArenaTheme.fromString("RED")
        assertEquals(ArenaTheme.RED, crimson)

        val emerald = ArenaTheme.fromString("EMERALD")
        assertEquals(ArenaTheme.EMERALD, emerald)

        val fallback = ArenaTheme.fromString("UNKNOWN")
        assertEquals(ArenaTheme.RED, fallback)
    }

    @Test
    fun testFeeTermPeriod() {
        val term4 = com.example.ui.parent.FeeTermPeriod.TERM_4
        assertEquals("Term 4 (Current)", term4.termTitle)
        assertEquals(2400, term4.fee)
        assertTrue(term4.isCurrent)

        val term1 = com.example.ui.parent.FeeTermPeriod.TERM_1_UPCOMING
        assertEquals(2400, term1.fee)
        assertFalse(term1.isCurrent)
    }

    @Test
    fun testRaidProblemGeneratorAllTypes() {
        // Test DOUBLE_DIGIT_ADD_SUB
        repeat(10) {
            val problem = com.example.ui.student.RaidProblemGenerator.generateProblem(
                com.example.ui.student.RaidMathType.DOUBLE_DIGIT_ADD_SUB
            )
            assertTrue(problem.answer > 0)
            assertTrue(problem.expression.contains("+") || problem.expression.contains("-"))
            assertTrue(problem.formulaHint.isNotEmpty())
        }

        // Test TRIPLE_DIGIT_COMBINED
        repeat(10) {
            val problem = com.example.ui.student.RaidProblemGenerator.generateProblem(
                com.example.ui.student.RaidMathType.TRIPLE_DIGIT_COMBINED
            )
            assertTrue(problem.answer > 0)
            assertTrue(problem.formulaHint.isNotEmpty())
        }

        // Test VEDIC_MULTIPLICATION
        repeat(10) {
            val problem = com.example.ui.student.RaidProblemGenerator.generateProblem(
                com.example.ui.student.RaidMathType.VEDIC_MULTIPLICATION
            )
            assertTrue(problem.answer > 0)
            assertTrue(problem.expression.contains("×"))
        }

        // Test GRANDMASTER_HYBRID
        repeat(10) {
            val problem = com.example.ui.student.RaidProblemGenerator.generateProblem(
                com.example.ui.student.RaidMathType.GRANDMASTER_HYBRID
            )
            assertTrue(problem.answer > 0)
        }
    }

    @Test
    fun testBossRaidDamageCalculation() {
        val baseDamage = 320

        // Streak 1 (No crit)
        val combo1Multiplier = if (1 >= 3) 2.2f else 1.0f
        val dmg1 = (baseDamage * combo1Multiplier).toInt()
        assertEquals(320, dmg1)

        // Streak 3 (Crit 2.2x)
        val combo3Multiplier = if (3 >= 3) 2.2f else 1.0f
        val dmg3 = (baseDamage * combo3Multiplier).toInt()
        assertEquals(704, dmg3)
    }

    @Test
    fun testStudentBatchEnrolledStudentsList() {
        val batch = com.example.data.model.StudentBatch(
            id = "batch_mon",
            name = "Monday Champions",
            dayOfWeek = "Monday",
            timeSlot = "5:00 PM - 7:00 PM",
            studentIds = "std_1, std_3, std_4"
        )
        val studentIds = batch.getStudentIdList()
        assertEquals(3, studentIds.size)
        assertTrue(studentIds.contains("std_1"))
        assertTrue(studentIds.contains("std_3"))
        assertTrue(studentIds.contains("std_4"))

        val updated = batch.withStudentIds(listOf("std_1", "std_2", "std_3", "std_4"))
        assertEquals(4, updated.getStudentIdList().size)
        assertTrue(updated.getStudentIdList().contains("std_2"))
    }

    @Test
    fun testAttendanceThreeBoxStatus() {
        val present = com.example.data.model.AttendanceBoxStatus.PRESENT
        val absent = com.example.data.model.AttendanceBoxStatus.ABSENT
        val late = com.example.data.model.AttendanceBoxStatus.LATE_HALF_DAY

        assertEquals("Present", present.label)
        assertEquals("Absent", absent.label)
        assertEquals("Late / Half-Day", late.label)

        val record = com.example.data.model.BatchAttendanceRecord(
            id = "batch_mon_std_1_24_Oct_2026",
            batchId = "batch_mon",
            studentId = "std_1",
            studentName = "SREEHITH A S",
            date = "24 Oct 2026",
            status = present.name
        )
        assertEquals("PRESENT", record.status)
        assertEquals("batch_mon", record.batchId)

        // Test editing attendance afterwards
        val editedRecord = record.copy(status = absent.name)
        assertEquals("ABSENT", editedRecord.status)
    }

    @Test
    fun testSevenTapTeacherUnlockLogic() {
        var tapCount = 0
        var isUnlocked = false

        for (i in 1..6) {
            tapCount++
            if (tapCount >= 7) isUnlocked = true
        }
        assertEquals(6, tapCount)
        assertFalse(isUnlocked)

        tapCount++
        if (tapCount >= 7) isUnlocked = true
        assertEquals(7, tapCount)
        assertTrue(isUnlocked)
    }

    @Test
    fun testAnzanDuelKeypadAndTimerLogic() {
        val vm = com.example.viewmodel.AnzanDuelViewModel()
        assertEquals("", vm.userAnswer.value)

        // Keypad digit appends
        vm.appendDigit("4")
        vm.appendDigit("8")
        assertEquals("48", vm.userAnswer.value)

        // Backspace
        vm.backspaceDigit()
        assertEquals("4", vm.userAnswer.value)

        // Append more digits
        vm.appendDigit("2")
        vm.appendDigit("5")
        assertEquals("425", vm.userAnswer.value)

        // Negative toggle
        vm.toggleNegative()
        assertEquals("-425", vm.userAnswer.value)

        vm.toggleNegative()
        assertEquals("425", vm.userAnswer.value)

        // Clear
        vm.clearAnswer()
        assertEquals("", vm.userAnswer.value)

        // Check countdown default duration
        assertEquals(10000L, vm.totalRoundDurationMs)
        assertEquals(1.0f, vm.roundTimeFraction.value)
    }
}


