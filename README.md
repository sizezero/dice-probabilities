
# PACG Probabilities

This is a small android app that calculates the probabilities of multiple dice exceeding a specified number.  It is particularly designed for playing the game [Pathfinder the Card Game](http://boardgamegeek.com/boardgame/133038/pathfinder-adventure-card-game-rise-of-the-runelor)

<a href="https://play.google.com/store/apps/details?id=org.kleemann.diceprobabilities">
  <img alt="Android app on Google Play"
       src="https://developer.android.com/images/brand/en_app_rgb_wo_45.png" />
</a>

## Workflow

The app is a simple dashboard that lets you quickly choose a set of dice and a target value, copy the dice set to a second dice set, modify the second dice set and compare the two probabilities. The app is design for speed of use not ease of learning.

## Hints

The following are some of the more obscure features:

1. To remove a die from the dice set, press the die that is in the target that dice set. (just above the answer percentage)

2. To display a detailed graph of the results, press the background graph (any part of the display that is not a button)

3. Press the target ( >= 10 ) to cycle through several large target values.

## Tutorial

1. Press the "+d8" and the "+d6" button at the top of the screen.  This will add the two dice to your current dice set.  Pressing the top row of buttons will add to your set of dice.

2. Press the ">=10" button on the second row.  This will reduce the target from 10 to 9.  Pressing buttons on the second row will subtract from your set of dice.  The odds are displayed just below the dice set.

3. Copy the first dice set to the second by pressing the down arrow button in the middle of the screen

4. Modify the second dice set by pressing the "+6" button in the middle of the screen.

5. Toggle the button view and graph view by pressing the background (press anything that's not a button)

6. To compare more dice rolls press the "C" button to clear the dice roll.

## Known issues

### Performance

The app starts to bog down if you add more than a dozen dice to the dice set. This is outside my use case for the application so I haven't addressed the issue.

### Ugly Buttons

The colors of the buttons were chosen to match the color of the dice that my play group uses. I understand that they are not the most subtle colors.

## Development

The code is open source under a fairly free license.  Feel free to play around with it.

Start by downloading the the [android development tools](http://developer.android.com).

### Command Line

```bash
$ git clone http://github.com/sizezero/dice-probabilities.git DiceProbabilities
$ android update project -n DiceProbabilities -p DiceProbabilities
$ android update test-project -p DiceProbabilities/tests -m ..
$ cd DiceProbabilities
$ ant -p
$ ant debug
$ ant install
$ cd tests
$ ant test
```

### Eclipse

First from bash:

```bash
$ cd /tmp
$ git clone http://github.com/sizezero/dice-probabilities.git DiceProbabilities
$ cd DiceProbabilities
$ ./setup_eclipse.sh
```

(windows users will need to hand copy the files specified in setup_eclipse.sh)

Then from Eclipse (assuming workspace is **~/workspace** )

* File > Import > Android > Existing Android Code Into Workspace
  * Browse > **/tmp/DiceProbabilities**
  * Unselect the *test* project
  * Select the option *Copy projects into workspace*
  * Finish
* File > Import > Android > Existing Android Code Into Workspace
  * Browse > **~/workspace/DiceProbabilities/tests**
  * Leave unselected: *Copy projects into workspace*
  * Finish

## Background

The game has borrowed from the interfaces of two other apps:

1. [Dice Probabilties Distributions](https://play.google.com/store/apps/details?id=lwiklendt.dicepd) which has a limited input system and a wonderful output
2. [nDn Dice Roller](https://play.google.com/store/apps/details?id=com.nDnDiceRoller&hl=en) which has a good input system and a limited output

The main purpose of this project is:

* to provide a useful app to my game group
* to allow me to learn android development
* to try out github

## Acknowledgements

Thanks to Emma Vokurka for learning Inkscape and making me a cool icon.

Thanks to my Pathfinder playtesting group for their great feedback: Fraser Stanton, Grace Kim, Jonathan Sari, Matthew Baldwin
