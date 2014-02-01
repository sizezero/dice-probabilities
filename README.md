
# Dice Probabilities

This is a small android app that calculates the probabilities of multiple dice exceeding a specified number.  It is particularly useful when playing the game [Pathfinder the card game](http://boardgamegeek.com/boardgame/133038/pathfinder-adventure-card-game-rise-of-the-runelor)

TODO: 

## Workflow

The app is a simple dashboard that lets you easily choose a set of dice and a target value, copy the dice set to a second dice set, modify the second dice set and compare the two probabilities.

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

### Ugly buttons

The colors of the buttons were chosen to match the color of the dice that my play group uses. I understand that they are not the most subtle colors.

## Background

The game has borrowed from the interfaces of two other apps:

1. [Dice Probabilties Distributions](https://play.google.com/store/apps/details?id=lwiklendt.dicepd) which has a limited input system and a wonderful output
2. [nDn Dice Roller](https://play.google.com/store/apps/details?id=com.nDnDiceRoller&hl=en) which has a good input system and a limited output

The main purpose of this project is:

* to provide a useful app to my game group
* to allow me to learn android development
* to try out github


