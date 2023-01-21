package ru.shop.composition.domain.entity

data class GameResult(
    val winner: Boolean, // для отображения смайлика
    val countOfRightAnswers: Int, // на сколько вопросов было отвечено верно
    val countOfQuestions: Int,
    val gameSettings: GameSettings // получим мин-е кол-во ответов должно быть
)
