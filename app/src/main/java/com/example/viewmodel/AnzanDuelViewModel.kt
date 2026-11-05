package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class DuelState {
    LOBBY,
    MATCHMAKING,
    MATCH_FOUND,
    COUNTDOWN,
    IN_PROGRESS,
    ROUND_TRANSITION,
    GAME_OVER
}

data class OpponentPlayer(
    val name: String,
    val className: String,
    val tier: String,
    val avatarInitials: String,
    val avgLatencyMs: Int
)

data class DuelRound(
    val roundNumber: Int,
    val num1: Int,
    val num2: Int,
    val operator: String = "+",
    val correctAnswer: Int,
    val expressionDisplay: String = "$num1 $operator $num2"
)

class AnzanDuelViewModel : ViewModel() {

    private val _duelState = MutableStateFlow(DuelState.LOBBY)
    val duelState: StateFlow<DuelState> = _duelState.asStateFlow()

    private val _opponent = MutableStateFlow<OpponentPlayer?>(null)
    val opponent: StateFlow<OpponentPlayer?> = _opponent.asStateFlow()

    private val _currentRound = MutableStateFlow(1)
    val currentRound: StateFlow<Int> = _currentRound.asStateFlow()

    private val _totalRounds = MutableStateFlow(5)
    val totalRounds: StateFlow<Int> = _totalRounds.asStateFlow()

    private val _roundData = MutableStateFlow<DuelRound?>(null)
    val roundData: StateFlow<DuelRound?> = _roundData.asStateFlow()

    private val _playerScore = MutableStateFlow(0)
    val playerScore: StateFlow<Int> = _playerScore.asStateFlow()

    private val _opponentScore = MutableStateFlow(0)
    val opponentScore: StateFlow<Int> = _opponentScore.asStateFlow()

    private val _userAnswer = MutableStateFlow("")
    val userAnswer: StateFlow<String> = _userAnswer.asStateFlow()

    // Real-time countdown tracking
    val totalRoundDurationMs: Long = 10000L

    private val _roundTimeRemainingMs = MutableStateFlow(10000L)
    val roundTimeRemainingMs: StateFlow<Long> = _roundTimeRemainingMs.asStateFlow()

    private val _roundTimeFraction = MutableStateFlow(1.0f)
    val roundTimeFraction: StateFlow<Float> = _roundTimeFraction.asStateFlow()

    private val _roundTimeLeft = MutableStateFlow(10)
    val roundTimeLeft: StateFlow<Int> = _roundTimeLeft.asStateFlow()

    private val _countdownTimer = MutableStateFlow(3)
    val countdownTimer: StateFlow<Int> = _countdownTimer.asStateFlow()

    private val _comboMultiplier = MutableStateFlow(1)
    val comboMultiplier: StateFlow<Int> = _comboMultiplier.asStateFlow()

    private val _maxCombo = MutableStateFlow(1)
    val maxCombo: StateFlow<Int> = _maxCombo.asStateFlow()

    private val _correctAnswersCount = MutableStateFlow(0)
    val correctAnswersCount: StateFlow<Int> = _correctAnswersCount.asStateFlow()

    private val _opponentStatusText = MutableStateFlow("Waiting...")
    val opponentStatusText: StateFlow<String> = _opponentStatusText.asStateFlow()

    private val _playerLatencyMs = MutableStateFlow(0L)
    val playerLatencyMs: StateFlow<Long> = _playerLatencyMs.asStateFlow()

    private val _isWrongAnswerShake = MutableStateFlow(false)
    val isWrongAnswerShake: StateFlow<Boolean> = _isWrongAnswerShake.asStateFlow()

    private var roundStartTime = 0L
    private var gameLoopJob: Job? = null
    private var opponentSimulationJob: Job? = null

    private val arenaChallengers = listOf(
        OpponentPlayer("Aarav Sharma", "Class VI-A", "Diamond", "AS", 215),
        OpponentPlayer("Tanvi Patil", "Class VI-A", "Diamond", "TP", 205),
        OpponentPlayer("Rohan Kulkarni", "Class VI-A", "Platinum", "RK", 280),
        OpponentPlayer("Ananya Deshmukh", "Class VI-A", "Platinum", "AD", 260),
        OpponentPlayer("Devansh Nair", "Class VI-A", "Gold", "DN", 340)
    )

    fun startMatchmaking() {
        _duelState.value = DuelState.MATCHMAKING
        _playerScore.value = 0
        _opponentScore.value = 0
        _currentRound.value = 1
        _comboMultiplier.value = 1
        _maxCombo.value = 1
        _correctAnswersCount.value = 0
        _userAnswer.value = ""

        viewModelScope.launch {
            // Simulate live search across Kopri East Arena
            delay(1400)
            val matched = arenaChallengers.random()
            _opponent.value = matched
            _duelState.value = DuelState.MATCH_FOUND
            delay(1000)

            // Start countdown 3, 2, 1
            _duelState.value = DuelState.COUNTDOWN
            for (cd in 3 downTo 1) {
                _countdownTimer.value = cd
                delay(750)
            }
            startRound(1)
        }
    }

    private fun startRound(roundNum: Int) {
        _currentRound.value = roundNum
        _userAnswer.value = ""
        _isWrongAnswerShake.value = false
        _opponentStatusText.value = "Calculating mental abacus..."
        _roundTimeRemainingMs.value = totalRoundDurationMs
        _roundTimeFraction.value = 1.0f
        _roundTimeLeft.value = 10
        roundStartTime = System.currentTimeMillis()

        // Generate dynamic UCMAS mental arithmetic problem
        val operator = if (roundNum % 2 == 0) "-" else "+"
        val n1: Int
        val n2: Int
        val answer: Int
        val display: String

        if (operator == "+") {
            n1 = Random.nextInt(15, 75)
            n2 = Random.nextInt(12, 68)
            answer = n1 + n2
            display = "$n1 + $n2"
        } else {
            // Subtraction: ensure positive or neat result
            n1 = Random.nextInt(35, 95)
            n2 = Random.nextInt(11, n1 - 5)
            answer = n1 - n2
            display = "$n1 - $n2"
        }

        _roundData.value = DuelRound(
            roundNumber = roundNum,
            num1 = n1,
            num2 = n2,
            operator = operator,
            correctAnswer = answer,
            expressionDisplay = display
        )
        _duelState.value = DuelState.IN_PROGRESS

        // Opponent answering behavior
        opponentSimulationJob?.cancel()
        opponentSimulationJob = viewModelScope.launch {
            val opp = _opponent.value ?: return@launch
            val delayMs = (opp.avgLatencyMs * 11 + Random.nextInt(1100, 3200)).toLong()
            delay(delayMs)
            if (_duelState.value == DuelState.IN_PROGRESS) {
                val isCorrect = Random.nextFloat() > 0.16f
                if (isCorrect) {
                    val oppPoints = 15 + Random.nextInt(0, 5)
                    _opponentScore.value += oppPoints
                    _opponentStatusText.value = "Answered Correctly! (+$oppPoints)"
                } else {
                    _opponentStatusText.value = "Calculation Error! (0)"
                }
            }
        }

        // High-frequency real-time round timer (ticks every 50ms for smooth 60fps countdown)
        gameLoopJob?.cancel()
        gameLoopJob = viewModelScope.launch {
            val tickIntervalMs = 50L
            while (_roundTimeRemainingMs.value > 0 && _duelState.value == DuelState.IN_PROGRESS) {
                delay(tickIntervalMs)
                val remaining = (_roundTimeRemainingMs.value - tickIntervalMs).coerceAtLeast(0L)
                _roundTimeRemainingMs.value = remaining
                _roundTimeFraction.value = remaining.toFloat() / totalRoundDurationMs
                _roundTimeLeft.value = ((remaining + 999) / 1000).toInt()
            }
            if (_roundTimeRemainingMs.value <= 0 && _duelState.value == DuelState.IN_PROGRESS) {
                advanceRound()
            }
        }
    }

    fun appendDigit(d: String) {
        if (_userAnswer.value.length < 6) {
            _userAnswer.value += d
        }
    }

    fun backspaceDigit() {
        if (_userAnswer.value.isNotEmpty()) {
            _userAnswer.value = _userAnswer.value.dropLast(1)
        }
    }

    fun clearAnswer() {
        _userAnswer.value = ""
    }

    fun toggleNegative() {
        val cur = _userAnswer.value
        _userAnswer.value = if (cur.startsWith("-")) {
            cur.drop(1)
        } else {
            "-$cur"
        }
    }

    fun submitAnswer(onCorrect: () -> Unit, onError: () -> Unit) {
        val current = _roundData.value ?: return
        if (_userAnswer.value.isEmpty() || _userAnswer.value == "-") return

        val ans = _userAnswer.value.toIntOrNull()
        val latency = System.currentTimeMillis() - roundStartTime
        _playerLatencyMs.value = latency

        if (ans == current.correctAnswer) {
            onCorrect()
            _correctAnswersCount.value += 1

            // Base 15 points + speed bonus for fast answers
            val remainingSec = _roundTimeLeft.value
            val speedBonus = (remainingSec * 2).coerceAtLeast(0)
            val points = (15 + speedBonus) * _comboMultiplier.value

            _playerScore.value += points
            val newCombo = _comboMultiplier.value + 1
            _comboMultiplier.value = newCombo.coerceAtMost(4)
            if (newCombo > _maxCombo.value) {
                _maxCombo.value = newCombo
            }
            advanceRound()
        } else {
            onError()
            _comboMultiplier.value = 1
            _isWrongAnswerShake.value = true
            viewModelScope.launch {
                delay(400)
                _isWrongAnswerShake.value = false
            }
            // Clear wrong answer so user can re-try if time permits
            _userAnswer.value = ""
        }
    }

    private fun advanceRound() {
        gameLoopJob?.cancel()
        opponentSimulationJob?.cancel()

        if (_currentRound.value >= _totalRounds.value) {
            _duelState.value = DuelState.GAME_OVER
        } else {
            _duelState.value = DuelState.ROUND_TRANSITION
            viewModelScope.launch {
                delay(900)
                startRound(_currentRound.value + 1)
            }
        }
    }

    fun resetToLobby() {
        gameLoopJob?.cancel()
        opponentSimulationJob?.cancel()
        _duelState.value = DuelState.LOBBY
    }

    override fun onCleared() {
        super.onCleared()
        gameLoopJob?.cancel()
        opponentSimulationJob?.cancel()
    }
}
