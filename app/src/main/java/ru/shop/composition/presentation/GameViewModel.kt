package ru.shop.composition.presentation

import android.app.Application
import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.shop.composition.R
import ru.shop.composition.data.GameRepositoryImpl
import ru.shop.composition.domain.entity.GameResult
import ru.shop.composition.domain.entity.GameSettings
import ru.shop.composition.domain.entity.Level
import ru.shop.composition.domain.entity.Question
import ru.shop.composition.domain.usecases.GenerateQuestionUseCase
import ru.shop.composition.domain.usecases.GetGameSettingsUseCase

class GameViewModel(
    private val application: Application,
    private val level: Level
) : ViewModel() {
    // нам пригодится контекст для строковых ресурсов

    private lateinit var gameSettings: GameSettings
    private val repository = GameRepositoryImpl

    private val generateQuestionUseCase = GenerateQuestionUseCase(repository)
    private val getGameSettingsUseCase = GetGameSettingsUseCase(repository)

    private var timer: CountDownTimer? = null

    // отображение времени на вопрос
    private val _formattedTime = MutableLiveData<String>()
    val formattedTime: LiveData<String>
        get() = _formattedTime

    // вопрос
    private val _question = MutableLiveData<Question>()
    val question: LiveData<Question>
        get() = _question

    // % правильных ответов для progress bar
    private val _percentOfRightAnswers = MutableLiveData<Int>()
    val percentOfRightAnswers: LiveData<Int>
        get() = _percentOfRightAnswers

    // для textview с кол-вом ответов + какое мин кол-во ответов должно быть
    private val _progressAnswers = MutableLiveData<String>()
    val progressAnswers: LiveData<String>
        get() = _progressAnswers

    // для состояния цвета textview
    private val _enoughCountOfRightAnswers = MutableLiveData<Boolean>()
    val enoughCountOfRightAnswers: LiveData<Boolean>
        get() = _enoughCountOfRightAnswers

    // для состояния цвета progress bar
    private val _enoughPercentOfRightAnswers = MutableLiveData<Boolean>()
    val enoughPercentOfRightAnswers: LiveData<Boolean>
        get() = _enoughPercentOfRightAnswers

    // для secondary progress bar
    private val _minPercent = MutableLiveData<Int>()
    val minPercent: LiveData<Int>
        get() = _minPercent

    // во фрагменте подписываемся и когда он прилетает, то открывается экран завершения игры
    private val _gameResult = MutableLiveData<GameResult>()
    val gameResult: LiveData<GameResult>
        get() = _gameResult

    private var countOfRightAnswers = 0
    private var countOfQuestions = 0

    init {
        startGame()
    }

    // получим настройки уровня исходя из параметра level в GameFragment
    private fun startGame() {
        getGameSettings()
        startTimer()
        generateQuestion()
        updateProgress()
    }

    fun chooseAnswer(number: Int) {
        checkAnswer(number)
        updateProgress()
        generateQuestion()
    }

    private fun updateProgress() {
        val percent = calculatePercentOfRightAnswers()
        _percentOfRightAnswers.value = percent
        _progressAnswers.value = String.format(
            application.resources.getString(R.string.progress_answers),
            countOfRightAnswers,
            gameSettings.minCountOfRightAnswers
        )
        _enoughCountOfRightAnswers.value =
            countOfRightAnswers >= gameSettings.minCountOfRightAnswers
        _enoughPercentOfRightAnswers.value = percent >= gameSettings.minPercentOfRightAnswers

    }

    private fun calculatePercentOfRightAnswers(): Int {
        if (countOfQuestions == 0)
            return 0 // при генерации первого вопроса устанавливаем значение правильных ответов (0)
        return ((countOfRightAnswers / countOfQuestions.toDouble()) * 100).toInt()
    }

    private fun checkAnswer(number: Int) {
        val rightAnswer = question.value?.rightAnswer
        if (number == rightAnswer) {
            countOfRightAnswers++
        }
        countOfQuestions++
    }

    private fun getGameSettings() {
        this.gameSettings = getGameSettingsUseCase(level) // переопределили invoke
        _minPercent.value = gameSettings.minPercentOfRightAnswers
    }

    private fun generateQuestion() {
        _question.value = generateQuestionUseCase(gameSettings.maxSumValue)
    }

    private fun startTimer() {
        timer = object : CountDownTimer(
            gameSettings.gameTimeInSeconds * MILLIS_IN_SECONDS,
            MILLIS_IN_SECONDS
        ) {
            override fun onTick(millisUntilFinished: Long) {
                _formattedTime.value = formatTime(millisUntilFinished)
            }

            override fun onFinish() {
                finishGame()
            }
        }
        timer?.start()
    }

    // в viewmodel форматируем время в String, а фрагмент подписывается,
    // получая уже отформатированное время в String
    private fun formatTime(millisUntilFinished: Long): String {
        val seconds = millisUntilFinished / MILLIS_IN_SECONDS
        val minutes = seconds / SECONDS_IN_MINUTES
        val leftSeconds = seconds - minutes * SECONDS_IN_MINUTES
        return String.format("%02d:%02d", minutes, leftSeconds) // 00:00
    }

    private fun finishGame() {
        _gameResult.value = GameResult(
            winner = enoughCountOfRightAnswers.value == true &&
                    enoughPercentOfRightAnswers.value == true,
            countOfRightAnswers = countOfRightAnswers,
            countOfQuestions = countOfQuestions,
            gameSettings = gameSettings
        )
    }

    override fun onCleared() {
        super.onCleared()
        timer?.cancel()
    }

    companion object {

        private const val MILLIS_IN_SECONDS = 1000L
        private const val SECONDS_IN_MINUTES = 60
    }
}