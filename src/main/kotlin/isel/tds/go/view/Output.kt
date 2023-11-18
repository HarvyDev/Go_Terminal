package isel.tds.go.view

import isel.tds.go.model.BOARD_SIZE
import isel.tds.go.model.Board
import isel.tds.go.model.Position

fun Board.show() {
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
        if (boardCells[pos] == null) {
            print(" . ")
        } else {
            print(" ${boardCells[pos]?.symbol} ")
        }
    }
    println()
}