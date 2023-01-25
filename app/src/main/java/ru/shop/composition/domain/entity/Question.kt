package ru.shop.composition.domain.entity

data class Question(
    val sum: Int,
    val visibleNumber: Int,
    val options: List<Int> // варианты ответов
) {

    val rightAnswer: Int
        get() = sum - visibleNumber
}
