package ru.shop.composition.domain.usecases

import ru.shop.composition.domain.entity.GameSettings
import ru.shop.composition.domain.entity.Level
import ru.shop.composition.domain.repository.GameRepository

class GetGameSettingsUseCase(
    private val repository: GameRepository
) {

    operator fun invoke(level: Level): GameSettings {
        return repository.getGameSettings(level)
    }
}