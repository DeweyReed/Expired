package com.github.deweyreed.expired.domain.utils

import androidx.core.text.isDigitsOnly
import java.time.LocalDate
import java.time.Year
import java.time.YearMonth

fun String.cleanVoiceInput(): String {
    return removeSuffix(".")
        .removeSuffix("。")
        .trim()
}

fun convertChineseToLocalDate(input: String): LocalDate? {
    val cleanInput = input.cleanVoiceInput()

    if (cleanInput == "今天") {
        return LocalDate.now()
    }
    if (cleanInput == "明天") {
        return LocalDate.now().plusDays(1)
    }
    if (cleanInput == "后天") {
        return LocalDate.now().plusDays(2)
    }
    if (cleanInput == "大后天") {
        return LocalDate.now().plusDays(3)
    }

    return try {
        val monthIndex = cleanInput.indexOf("月")

        val monthValue =
            when (val monthValueInChinese = cleanInput.substring(0, monthIndex)) {
                "这个" -> YearMonth.now().monthValue
                "下个" -> YearMonth.now().plusMonths(1).monthValue
                else -> convertChineseCharacterToNumber(monthValueInChinese)
            }

        val dayValueInChinese = cleanInput.substring(monthIndex + 1)
            .removeSuffix("日")
            .removeSuffix("号")
        val dayValue = convertChineseCharacterToNumber(dayValueInChinese)

        var constructedDateTime = LocalDate.of(Year.now().value, monthValue, dayValue)
        if (constructedDateTime.isBefore(LocalDate.now())) {
            constructedDateTime = constructedDateTime.plusYears(1)
        }

        constructedDateTime
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

private fun convertChineseCharacterToNumber(character: String): Int {
    if (character.isDigitsOnly()) return character.toInt()

    return when (character) {
        "零" -> 0
        "一" -> 1
        "二" -> 2
        "三" -> 3
        "四" -> 4
        "五" -> 5
        "六" -> 6
        "七" -> 7
        "八" -> 8
        "九" -> 9
        "十" -> 10
        "十一" -> 11
        "十二", "腊" -> 12
        "十三" -> 13
        "十四" -> 14
        "十五" -> 15
        "十六" -> 16
        "十七" -> 17
        "十八" -> 18
        "十九" -> 19
        "二十" -> 20
        "二十一", "二一" -> 21
        "二十二", "二二" -> 22
        "二十三", "二三" -> 23
        "二十四", "二四" -> 24
        "二十五", "二五" -> 25
        "二十六", "二六" -> 26
        "二十七", "二七" -> 27
        "二十八", "二八" -> 28
        "二十九", "二九" -> 29
        "三十" -> 30
        "三十一", "三一" -> 31
        else -> throw IllegalArgumentException(character)
    }
}
