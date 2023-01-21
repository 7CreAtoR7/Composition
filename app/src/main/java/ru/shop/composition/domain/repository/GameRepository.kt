package ru.shop.composition.domain.repository

import ru.shop.composition.domain.entity.GameSettings
import ru.shop.composition.domain.entity.Level
import ru.shop.composition.domain.entity.Question

interface GameRepository {

    fun generateQuestion(
        /*для генерации вопроса нужно знать:
         1.число, которое нужно получить
         2.кол-во вариантов ответа
         */
        maxSumValue: Int,
        countOfOptions: Int
    ): Question

    fun getGameSettings(level: Level): GameSettings
}