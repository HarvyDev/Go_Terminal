package isel.tds.go.model

class Game (
    val board: Board = Board(),
    val turn: Piece = Piece.BLACK,
    val isFinished: Boolean = false,
    val whiteScore: Int = 0,
    val blackScore: Int = 0,
    val lastWasPast: Boolean = false
    )

fun Game.canPlay(pos:Position): Boolean =
    pos.row in 1..BOARD_SIZE && pos.col in 'A'..<'A' + BOARD_SIZE &&
            this.board.boardCells[pos] == null

fun Game.play(pos:Position): Game {
    require(pos.row in 1..BOARD_SIZE) { "Invalid position" }
    require(pos.col in 'A'..<'A' + BOARD_SIZE) { "Invalid position" }

    if (!this.canPlay(pos)) {
        println("Invalid play, position is occupied!")
        return this
    }

    val isSuicide = this.isSuicide(pos)
    val hasLibertiesAfterPlay = this.hasLibertiesAfterPlay(pos)

    if (isSuicide && !hasLibertiesAfterPlay) {
        println("Invalid play, suicide!")
        return this
    }

    else if (isSuicide) {
        val newBoardCells = this.board.boardCells.toMutableMap()
        newBoardCells[pos] = this.turn

        return Game (
            board = Board(newBoardCells),
            turn = turn.other,
            isFinished = false,
            whiteScore = whiteScore,
            blackScore = blackScore,
            lastWasPast = true
        ).clean(pos)
    }

    val newBoardCells = this.board.boardCells.toMutableMap()
    newBoardCells[pos] = this.turn

    return Game(
        board = Board(newBoardCells),
        turn = turn.other,
        isFinished = isFinished,
        whiteScore = whiteScore,
        blackScore = blackScore,
        lastWasPast = false
    ).clean(null)
}

fun Game.hasLibertiesAfterPlay(pos:Position): Boolean {
    val newBoardCells = this.board.boardCells.toMutableMap()
    newBoardCells[pos] = this.turn
    return Game(
        board = Board(newBoardCells),
        turn = turn,
        isFinished = isFinished,
        whiteScore = whiteScore,
        blackScore = blackScore,
        lastWasPast = lastWasPast
    ).clean(null).play(pos).exploreLiberties(pos,pos, mutableSetOf()) != 0
}

fun countLiberties(game:Game, pos:Position): Int {
    val visited = mutableSetOf<Position>()

    if (game.board.boardCells[pos] != null)
        return game.exploreLiberties(pos, pos, visited)

    return 0
}

private fun Game.exploreLiberties(initialPos: Position, currentPosition: Position, visited: MutableSet<Position>): Int {
    visited.add(currentPosition)

    val adjacentPositions = currentPosition.getAdjacentPositions()
    var liberties = 0

    for (adjacent in adjacentPositions) {
        if (!visited.contains(adjacent) && adjacent.isValidPosition()) {
            if (this.board.boardCells[adjacent] == null)
                liberties++
            else if (this.board.boardCells[adjacent] == this.board.boardCells[initialPos])
                liberties += exploreLiberties(initialPos, adjacent, visited)
        }
    }
    return liberties
}

fun Game.isSuicide(pos: Position): Boolean{
    if(pos.isValidPosition() && this.canPlay(pos)) {
        // Criamos uma nova board
        val newBoard = this.board.boardCells.toMutableMap()
        // Definimos a posição que estamos tentar jogar na nova board como o turn atual
        newBoard[pos] = this.turn
        // A partir desta nova board, exploramos as liberdades da peça inserida
        val liberties = Game (
            board = Board(newBoard),
            turn = turn,
            isFinished = isFinished,
            whiteScore = whiteScore,
            blackScore = blackScore,
            lastWasPast = lastWasPast
        ).exploreLiberties(pos,pos, mutableSetOf())
        // Caso esta não tenha liberdades, significará que posicionar uma peça nessa posição resulta em suicidio.
        return liberties == 0
    }
    return false
}

fun Game.clean(except: Position?): Game {
    val newBoardCells = board.boardCells.toMutableMap()
    var newBlackCaptures = blackScore
    var newWhiteCaptures = whiteScore
    for (r in 1..BOARD_SIZE) {
        for (c in 65..<65 + BOARD_SIZE) {
            if (countLiberties(this, Position(r, c.toChar())) == 0 && board.boardCells[Position(r, c.toChar())] != null && Position(r, c.toChar()) != except) {
                if (board.boardCells[Position(r, c.toChar())] == Piece.WHITE)
                    newBlackCaptures++
                else
                    newWhiteCaptures++

                newBoardCells[Position(r, c.toChar())] = null
            }
        }
    }
    return Game(
        board = Board(newBoardCells),
        turn = turn,
        isFinished = isFinished,
        whiteScore = newWhiteCaptures,
        blackScore = newBlackCaptures,
        lastWasPast = lastWasPast
    )
}

fun Game.end(): Game {
    return if (isFinished) {
        val (whiteScore, blackScore) = this.score()
        if (whiteScore > blackScore) {
            println("The winner is player 0 (White). Score = $whiteScore to $blackScore")
        } else if (blackScore > whiteScore) {
            println("The winner is player # (Black). Score = $blackScore to $whiteScore")
        }
        this
    }
    else this
}
fun Game.resign(): Game =
    Game(
        board = Board(board.boardCells),
        turn = turn.other,
        isFinished = true,
        whiteScore = whiteScore,
        blackScore = blackScore,
        lastWasPast = false
    )


fun Game.score(): Pair<Int, Double> {

    var whiteScore = whiteScore
    var blackScore = blackScore.toDouble()

    //Vamos precurer todas as peças do tabuleiro
    for (r in 1..BOARD_SIZE) {
        for (c in 65..<65 + BOARD_SIZE) {
            //Caso uma seja null iremos chamar a função isSurrounded para verificar se está rodeada
            if (board.boardCells[Position(r, c.toChar())] == null) {
                val x = Position(r, c.toChar()).isSurrounded(board)

                //Se esta estiver rodeada, iremos incrementar o score do jogador que a rodeou
                if (x == Piece.WHITE) whiteScore++
                else if (x == Piece.BLACK) blackScore++
            }
        }
    }
    //Dependendo do tamanho do tabuleiro iremos retirar pontos ao score do jogador preto
    when(BOARD_SIZE){
        9 -> blackScore -= 3.5
        13 -> blackScore -= 4.5
        19 -> blackScore -= 5.5
    }
    return Pair(whiteScore, blackScore)
}

fun Game.pass(): Game {
    // Check if the game is already finished
    if (isFinished) {
        return this
    }

    return if (lastWasPast) {
        Game (
            board = this.board,
            turn = turn.other,
            isFinished = true,
            whiteScore = whiteScore,
            blackScore = blackScore,
            lastWasPast = true
        )
    } else {
        Game (
            board = this.board,
            turn = turn.other,
            isFinished = false,
            whiteScore = whiteScore,
            blackScore = blackScore,
            lastWasPast = true
        )
    }
}