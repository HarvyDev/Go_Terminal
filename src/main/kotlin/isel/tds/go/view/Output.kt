package isel.tds.go.view

import isel.tds.go.model.BOARD_SIZE
import isel.tds.go.model.Board
import isel.tds.go.model.Game
import isel.tds.go.model.Position

fun Game.show() {
    if (isFinished) {
        println("Game is finished!")
        return
    }
    var firstLine = " "
    for (i in 0..<BOARD_SIZE) {
        firstLine += " " + ('A' + i) + " "
    }
    print(firstLine)
    Position.values.forEach { pos ->
        if (pos.col == 'A') {
            println()
            print(pos.row)
        }
        if (board.boardCells[pos] == null) {
            print(" . ")
        } else {
            print(" ${board.boardCells[pos]?.symbol} ")
        }
    }
    println()
    println("Turn: ${turn.symbol}(${turn}) Captures: #=${blackScore}  0=${whiteScore}")
}