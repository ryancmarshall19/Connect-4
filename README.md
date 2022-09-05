# Connect-4
A connect 4 application, complete with an adjustable computer to play against.

okay this is the second time I'm writing a readme -- I think I understand it a little more now (the minesweeper readme was a disaster).

So the actual game of connect 4 is very simple -- there are two players who take turns dropping their respective tokens into one of seven slots.
The tokens stack up on top of one another (up to a maximum of 6 in a column), and the goal is to make a line of four with your pieces. 
This line can be vertical (4 tokens stacked on top of each other), horizontal (4 tokens all in adjacent columns, all in the same row), or diagonal.
The first player to make one of these wins. If you stil don't understand, play one game against the computer at level 0 (you will lose), and you should understand.

The computer uses minimax, and the different levels just refer to how many moves ahead it actually looks. I'm not very good at coding, so even the highest level doesn't see the entire game -- it implements some optimizations that just evaluate the position and assign a score that way. And tbh I'm fine with it, because the level 0 computer (the suggested level) is undefeated except for me :) ... give it a try! (Yes, I'm the best connect 4 player I know. Yes, that's something I will brag about). I don't think I could eat the level 3 computer (the highest level), but it takes a while to calculate, so I think I've only ever played a couple games against it. Few others have played against the level 3 computer.

And, before you don't ask because you really don't care, I have pitted it against a connect-4 solver I found online that does actually compute the entire game. As expected, mine lost. But, it did bring the game to a point where the winning move was the last possible move on the board, so I think it did pretty well. 

Anyways, have fun!
