package com.example.arithmeticgame

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.arithmeticgame.databinding.ActivityMainBinding
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var currentPlayer = 1
    private var player1Score = 0
    private var player2Score = 0
    private var jackpot = 5
    private var correctAnswer = 0
    private var doublePoints = false
    private var tryingForJackpot = false
    private var pointsAmount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updateUI()

        binding.btnRollDie.setOnClickListener(){
            rollDie()
        }

        binding.btnGuess.setOnClickListener(){
            checkAnswer()
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    private fun rollDie(){
        binding.btnRollDie.isEnabled = false

        val diceValue = Random.nextInt(1,7)
        val imageName = "@drawable/dice$diceValue"

        val resource = resources.getIdentifier(imageName, "drawable", packageName)

        binding.ivDie.setImageResource(resource)


        doublePoints = false
        tryingForJackpot = false
        pointsAmount = 0

        when (diceValue) {
            1 -> {generateProblem("+")
                pointsAmount = 1
            }
            2 -> {
                generateProblem("-")
                pointsAmount = 2
            }

            3 -> {
                generateProblem("*")
                pointsAmount = 3
            }
            4 -> {
                doublePoints = true
                rollDie() // Roll again for double points
            }
            5 -> {
                switchPlayer()
                binding.arithmetic.text = "Lose turn, roll the die"
                binding.btnRollDie.isEnabled = true
            }
            6 -> {
                tryingForJackpot = true
                generateProblem("*") // Multiplication for jackpot attempt
            }
        }

    }
    private fun generateProblem(operator : String){
        val num1 = if (operator == "*") Random.nextInt(0, 21) else Random.nextInt(0,100)
        val num2 = if (operator == "*") Random.nextInt(0, 21) else Random.nextInt(0, 100)

        correctAnswer = when (operator){
            "+" -> num1 + num2
            "-" -> num1 - num2
            "*" -> num1 * num2
            else -> 0
        }
        binding.arithmetic.text = "$num1 $operator $num2 = ?"
    }
    private fun checkAnswer() {
        val userInput = binding.userInput.text.toString().trim()
        val userAnswer = userInput.toIntOrNull()

        println("User Answer: $userAnswer, Correct Answer: $correctAnswer")

        if (userAnswer == correctAnswer) {
            val pointsEarned = if (tryingForJackpot) jackpot else if (doublePoints) 2 else pointsAmount

            if (currentPlayer == 1) {
                player1Score += pointsEarned
                println("Player1 Score: $player1Score")

            } else {
                player2Score += pointsEarned
                println("Player2 Score: $player2Score")
            }


            // Reset jackpot after winning it
            if (tryingForJackpot) {
                jackpot = 5
            }

            binding.arithmetic.text = "Correct, roll the die"
        } else {
            binding.arithmetic.text = "Incorrect, roll the die"
            jackpot += pointsAmount

        }

        binding.userInput.text.clear()

        // Update UI without resetting the problem
        updateScores()


        // Check for a winner before switching players
        if (player1Score >= 20) {
            binding.arithmetic.text = "Player 1 Wins!"
            binding.btnRollDie.text = "Press to Play again"
            binding.btnRollDie.setOnClickListener() {
                resetGame()
            }
            return
        } else if (player2Score >= 20) {
            binding.arithmetic.text = "Player 2 Wins!"
            binding.btnRollDie.text = "Press to Play again"
            binding.btnRollDie.setOnClickListener() {
                resetGame()
            }
            return
        }

        switchPlayer()
        binding.btnRollDie.isEnabled = true
    }


    private fun switchPlayer() {
        currentPlayer = if (currentPlayer == 1) 2 else 1
        binding.currPlayer.text = "Player $currentPlayer's Turn"
    }

    private fun updateUI() {
        binding.currPlayer.text = "Player $currentPlayer's Turn"
        updateScores()
        binding.arithmetic.text = "Roll the die to start!"
        binding.userInput.text.clear()
    }

    private fun resetGame() {
        player1Score = 0
        player2Score = 0
        jackpot = 5
        currentPlayer = 1
        updateUI()
        binding.btnRollDie.text = "Roll"
    }
    private fun updateScores() {
        println("Updating UI: Player1 Score: $player1Score, Player2 Score: $player2Score, Jackpot: $jackpot") // Debugging
        binding.play1Total.text = "Player 1: $player1Score"
        binding.play2Total.text = "Player 2: $player2Score"
        binding.currJackpot.text = "Jackpot: $jackpot"
        println("After Update -> Player 1: ${binding.play1Total.text}, Player 2: ${binding.play2Total.text}")
    }

}