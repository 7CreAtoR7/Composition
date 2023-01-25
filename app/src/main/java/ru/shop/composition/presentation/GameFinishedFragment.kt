package ru.shop.composition.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ru.shop.composition.databinding.FragmentGameFinishedBinding

class GameFinishedFragment : Fragment() {

    private val args by navArgs<GameFinishedFragmentArgs>()


    private var _binding: FragmentGameFinishedBinding? = null
    private val binding: FragmentGameFinishedBinding
        get() = _binding ?: throw RuntimeException("FragmentGameFinishedBinding == null")


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameFinishedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        binding.gameResult = args.gameResult
    }

    private fun setupClickListeners() {
        /*
        // добавляем слушатель на кнопку назад. Сработает данный callback
        // и после вызова retryGame() возвращаемся на экран выбора уровня
        // передаем viewLifecycleOwner, чтобы при удалении фрагмента удалялся
        // и слушатель во избежании утечки памяти
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                retryGame()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        // убрал слушатель, т.к. мы настроили его в jetpack navigation
        */
        // слушатель на кнопку попробовать снова
        binding.buttonRetry.setOnClickListener {
            retryGame()
        }
    }

    /*
    // перенес в BindingAdapters
    private fun getPercentOfRightAnswers() = with(args.gameResult) {
        if (countOfQuestions == 0) {
            0
        } else {
            ((countOfRightAnswers / countOfQuestions.toDouble()) * 100).toInt()
        }
    }


    // перенес в BindingAdapters
    private fun getSmileResId(): Int {
        return if (args.gameResult.winner) {
            R.drawable.ic_smile
        } else {
            R.drawable.ic_sad
        }
    }
    */


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    // когда находимся на фрагменте с окончанием уровня, кнопка НАЗАД перебрасывает на фрагмент с выбором уровня
    private fun retryGame() {
        findNavController().popBackStack()
    }
}