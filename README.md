# Gambling

A dice gambling app which contains the following features:
* Singleplayer:
  * <b>GIF animations</b> to cover/uncover two dices with a dice cup and to shake this cup.
  * Shaking the phone is recognized with the internal <b>acceleromter sensor</b>.
  * While shaking, there is a fitting sound effect using <b>MediaPlayer</b>.
  * Gamification: If the user gets a double, he/she is allowed to play another round
    and the scores are added together.
  * The score is stored in a <b>Room database</b>. But only if it is better than the smallest 
    stored score, out of ten.
    
* Multiplayer:
  * Connecting to another phone is possible by using <b>Nearby Connections API</b>. At the moment 
    it is only possible to connect to one other device (the API allows more connections).
  * As soon as the two devices are connected, the DiceGameFragment is opened, but in the background 
    the DiceMultiplayerFragment is still running and handling the connection.
  * After receiving a score, DiceGameFragment is closed and DiceMultiplayerFragment is made visible again, 
    to show the score of the opponent and the user's score.
  * Communication between the two fragments is done by <b>Live Data</b>.

* Settings:
  * The user can change the background and the theme of the app.
  * Possible to choose between light and dark <b>theme</b>.
  * As the background, a color or an image can be selected. The image can be selected out of the 
    <b>gallery</b> or you can take a <b>new photo</b>.
  * The selected settings are stored in <b>shared preferences</b>.
  
Future objectives:
* Choosing the amount of dices.
* Multiplayer with more than two players.
* Include more dice games. Also in the multiplayer.
* Expand the High Score. One list for for each dice game and singleplayer/multiplayer.
* Improve animations. Considering using something else than GIFs, because they use a lot
  of memory space and their performance is not optimal.
* Improve the design of the app (buttons, text style,...).
