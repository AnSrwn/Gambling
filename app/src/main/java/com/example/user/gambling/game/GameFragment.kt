package com.example.user.gambling.game

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import com.example.user.gambling.R
import com.example.user.gambling.database.databases.ScoreDB
import com.example.user.gambling.database.entities.Score
import com.example.user.gambling.game.score.DiceScore
import com.example.user.gambling.models.ScoreViewModel
import kotlinx.android.synthetic.main.fragment_dice_singleplayer.*
import org.jetbrains.anko.doAsync
import pl.droidsonroids.gif.GifDrawable
import pl.droidsonroids.gif.GifImageView
import android.widget.Toast

/**
 * This fragment contains the main dice game. It is used for the Singleplayer and the Multiplayer.
 * To differentiate the two cases, the isMultiplayer variable is used.
 */
class GameFragment : android.support.v4.app.Fragment() {

    companion object {
        private const val MAX_DATABASE_ENTRIES = 10
    }

    private var shakeListener: ShakeListener? = null
    private var gifImageViewDiceCup: GifImageView? = null
    private var gifDrawable: GifDrawable? = null
    private var imageViewDice1: ImageView? = null
    private var imageViewDice2: ImageView? = null

    private var btnRestart: Button? = null

    private val diceScore = DiceScore(2)
    private var diceScorePrevRound = 0

    private var isMultiplayer = false
    private var currentPlayerName : String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_dice_singleplayer, container, false)

        isMultiplayer = arguments!!.getBoolean("isMultiplayer")
        currentPlayerName = arguments!!.getString("playerName")

        registerForScoreUpdates()

        setImageResources(view)

        btnRestart = view.findViewById(R.id.buttonRestart)
        btnRestart!!.setOnClickListener {
            restartGameFragment()
        }
        btnRestart!!.visibility = View.GONE

        shakeListener = ShakeListener(activity!!.applicationContext)
        startGifAnimations()

        return view
    }

    override fun onResume() {
        shakeListener!!.resume()
        super.onResume()
    }

    override fun onPause() {
        shakeListener!!.pause()
        super.onPause()
    }

    /**
     * Method to start the shaking GIF animations and to start the ShakeListener
     * It should be called after the setCup GIF started, so that after 400ms the dices become
     * invisible and only the cup is shown.
     * After 800ms the ShakeLister is started and the user is informed, that now shakes are
     * detected.
     */
    private fun startGifAnimations() {
        Handler().postDelayed({
            setDicesVisibilty(false)
        }, 400)

        Handler().postDelayed({
            Toast.makeText(
                    activity,
                    getString(R.string.dice_single_toast_start_shaking),
                    Toast.LENGTH_SHORT).show()

            startShakeListener()
        }, 800)
    }

    /**
     * This method starts the ShakeListener and defines what to do when the device is shaken or the
     * the shake stopped.
     *
     * While shaking the shaking GIF and a corresponding sound is played.
     * OnShakeStop the shaking GIF is stopped and the setCup GIF with corresponding sound is played.
     *
     * After that, it is checked if the game belongs to a Multiplayer game. If yes, the user returns
     * to the Multiplayer fragment and the score is transferred by using Live Data.
     * If it is a Singleplayer game, it is checked if the scores are a double. If yes, the user can
     * play another round, otherwise the score is displayed and depending on the score, it is saved
     * in the High Score.
     *
     * Every time the ShakeListener is paused.
     */
    private fun startShakeListener() {
        val audioPlayer = AudioPlayer(context!!)

        shakeListener?.setOnShakeListener(object : ShakeListener.OnShakeListener {

            var pickupAnimationFinished = false
            var setAnimationRunning = false
            var shakingAnimationFinished = false
            var shakingAnimationRunning = false
            var shakingSoundRunning = false

            override fun onShake() {
                if(!shakingSoundRunning) {
                    audioPlayer.startDiceShakeSound()
                    shakingSoundRunning = true
                }
                if(!shakingAnimationRunning) {
                    gifImageViewDiceCup!!.setImageResource(R.drawable.gif_dice_shake)
                    gifDrawable!!.start()
                    shakingAnimationRunning = true

                    Handler().postDelayed({
                        shakingAnimationRunning = false
                        shakingAnimationFinished = true
                    }, 1000)
                }
            }

            override fun onShakeStop() {
                if(!pickupAnimationFinished && shakingAnimationFinished) {
                    pickupAnimationFinished = true

                    audioPlayer.stopDiceShakeSound()

                    diceScore.generateNewScores()
                    changeDice(imageViewDice1!!, diceScore.scoresOfDices[0])
                    changeDice(imageViewDice2!!, diceScore.scoresOfDices[1])
                    setDicesVisibilty(true)

                    audioPlayer.playDiceRollSound()

                    gifImageViewDiceCup!!.setImageResource(R.drawable.gif_pickup_cup)
                    gifDrawable!!.start()

                    Handler().postDelayed({

                        if(isMultiplayer) {
                            updateDiceScore(diceScore.sumOfScores)
                            returnToMultiplayerFragment()

                        } else {
                            val newScore = diceScore.sumOfScores + diceScorePrevRound
                            textViewScore.text = getString(R.string.dice_single_score, newScore)

                            //check if there is a double
                            if(diceScore.scoresOfDices[0] == diceScore.scoresOfDices[1]) {
                                updateDiceScore(newScore)

                                Toast.makeText(
                                        activity,
                                        getString(R.string.dice_single_extra_round),
                                        Toast.LENGTH_SHORT).show()

                                Handler().postDelayed({
                                    restartGameFragment()
                                }, 1500)

                            } else {
                                btnRestart!!.visibility = View.VISIBLE
                                updateDiceScore(0)

                                //Save score only for single player
                                insertScoreinDB(currentPlayerName!!, newScore)
                            }
                        }

                        gifDrawable!!.stop()
                        gifImageViewDiceCup!!.visibility = View.GONE

                        shakeListener?.pause()
                    }, 800)
                }
            }

            override fun onResume() {
                pickupAnimationFinished = false
                setAnimationRunning = false
                shakingAnimationFinished = false
                shakingAnimationRunning = false
            }
        })
    }

    /**
     * Method to find all needed views and starts the setCup GIF animation.
     */
    private fun setImageResources(view: View) {
        imageViewDice1 = view.findViewById<View>(R.id.imageViewFirstDice) as ImageView
        imageViewDice1!!.setImageResource(R.drawable.dice1)

        imageViewDice2 = view.findViewById<View>(R.id.imageViewSecondDice) as ImageView
        imageViewDice2!!.setImageResource(R.drawable.dice1)

        gifImageViewDiceCup = view.findViewById<View>(R.id.gifRollingDices) as GifImageView
        gifImageViewDiceCup!!.setImageResource(R.drawable.gif_set_cup)
        gifDrawable = gifImageViewDiceCup!!.drawable as GifDrawable
    }

    /**
     * Method to remove the gameFragment from the stack and making the multiplayerFragment
     * visible again.
     */
    private fun returnToMultiplayerFragment() {
        val gameFragment = fragmentManager!!.findFragmentByTag("gameFragment")
        fragmentManager!!.beginTransaction().remove(gameFragment!!).commit()

        val multiplayerFragment = fragmentManager!!.findFragmentByTag("multiplayerFragment")
        fragmentManager!!.beginTransaction().show(multiplayerFragment!!).commit()
    }

    /**
     * Restarts the gameFragment. Should be called after a Singleplayer game by pressing the
     * "New Game" button.
     */
    private fun restartGameFragment() {
        val diceSingleplayerFragment = fragmentManager!!.findFragmentByTag("singleplayerFragment")

        fragmentManager!!.beginTransaction()
                .detach(diceSingleplayerFragment!!)
                .attach(diceSingleplayerFragment)
                .commit()
    }

    /**
     * Sets the visibility of the dice images.
     * Is needed during the different cup GIF animations.
     * @param setVisible true to make dices visible and false to make them gone.
     */
    private fun setDicesVisibilty(setVisible: Boolean) {
        if(setVisible) {
            imageViewDice1!!.visibility = View.VISIBLE
            imageViewDice2!!.visibility = View.VISIBLE

        } else {
            imageViewDice1!!.visibility = View.GONE
            imageViewDice2!!.visibility = View.GONE
        }
    }

    /**
     * Sets the imageView of the dices, depending on the score.
     * For each score (1-6) there is a specific drawable.
     * @param view imageView of the dice
     * @param score between 1 and 6
     */
    private fun changeDice(view: ImageView?, score: Int) {
        when (score) {
            1 -> view!!.setImageResource(R.drawable.dice1)
            2 -> view!!.setImageResource(R.drawable.dice2)
            3 -> view!!.setImageResource(R.drawable.dice3)
            4 -> view!!.setImageResource(R.drawable.dice4)
            5 -> view!!.setImageResource(R.drawable.dice5)
            6 -> view!!.setImageResource(R.drawable.dice6)
        }
    }

    /**
     * Score will be inserted in Database, up to MAX_DATABASE_ENTRIES.
     * If MAX_DATABASE_ENTRIES lowest score will be replaced if rolled score is higher than the lowest.
     * @param playerName Players name.
     * @param rolledScore of the player.
     */
    private fun insertScoreinDB(playerName : String, rolledScore: Int) {
        val db = ScoreDB.getInstance(context!!)!!.scoreDB()
        doAsync {
            val curSize = db.getAllNotLive().size
            if(curSize >= MAX_DATABASE_ENTRIES ){
                val minScoreInDB = db.getEntryWithMinScore()
                if(rolledScore > minScoreInDB.score) {
                    db.update(Score(minScoreInDB.id, playerName, rolledScore))
                }else{
                    //Nothing happens
                }
            }else {
                db.insert(Score(curSize + 1, playerName, rolledScore))
            }
            Log.d("DBG", "$rolledScore in DB inserted")
        }
    }

    /**
     * Is needed in the Singleplayer mode, if the user got a double and can play another round.
     * This method gets the score of the previous round(s).
     */
    private fun registerForScoreUpdates() {
        activity?.let { fragmentActivity ->
            val sharedViewModel = ViewModelProviders.of(fragmentActivity).get(ScoreViewModel::class.java)
            sharedViewModel.myScore.observe(this, Observer { i ->
                i?.let {
                    diceScorePrevRound = it
                }
            })
        }
    }

    /**
     * Saves the score in a Live Data model.
     * In Singleplayer mode: If user gets a double, the score is saved, before restarting the
     * gameFragment.
     * In Multiplayer mode: Score is saved, so that the multiplayerFragment can get it.
     */
    private fun updateDiceScore(score: Int) {
        activity?.let {
            val scoreViewModel = ViewModelProviders.of(it).get(ScoreViewModel::class.java)
            scoreViewModel.myScore.postValue(score)
        }
    }
}