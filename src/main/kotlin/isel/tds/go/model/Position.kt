package isel.tds.go.model
class Position(val row:Int, val col:Char) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Position) return false
        return row == other.row && col == other.col
    }

    override fun hashCode(): Int {
        var result = row
        result = 31 * result + col.hashCode()
        return result
    }

    companion object {
        val values = List(BOARD_CELLS) { idx ->
            Position(idx / BOARD_SIZE + 1, 'A' + idx % BOARD_SIZE)
        }
        operator fun invoke(row: Int, col: Char): Position {
            require(row in 1..BOARD_SIZE && col in 'A'..<'A' + BOARD_SIZE) { "Invalid position" }
            return values[row * BOARD_SIZE + (col - 'A')]
        }
    }
}

fun Position.getAdjacentPositions(): List<Position> {
    return listOf(
        Position(this.row, this.col - 1),
        Position(this.row, this.col + 1),
        Position(this.row - 1, this.col),
        Position(this.row + 1, this.col)
    )
}

fun Position.isValidPosition(): Boolean {
    return (this.row in 1..BOARD_SIZE && this.col in 'A'..<'A' + BOARD_SIZE)
}

fun String.toPosition(): Position {
    val letterIndex = this.indexOfFirst { !it.isDigit() }

    val row = this.substring(0, letterIndex).toIntOrNull()
    val col = this.substring(letterIndex)[0].uppercaseChar()

    return Position(row ?: -1, col)
}


fun Position.isSurrounded(board: Board): Piece? {

    val visited = mutableSetOf<Position>()
    val piece = mutableSetOf<Piece>()


    fun search(p: Position) {
        visited.add(p)
        //Vamos buscar as posições adjacentes
        val adjacentPos = p.getAdjacentPositions()

        //Para cada posição adjacente iremos verificar se esta já foi visitada e se é válida
        for (adjacent in adjacentPos) {
            if (!visited.contains(adjacent) && adjacent.isValidPosition()) {

                //Se esta for null iremos chamar a função search para continuar a verificação
                if (board.boardCells[adjacent] == null) search(adjacent)
                //Se esta for uma peça iremos adicionar ao set de peças
                else if (board.boardCells[adjacent] == Piece.BLACK) piece += Piece.BLACK
                else if (board.boardCells[adjacent] == Piece.WHITE) piece += Piece.WHITE
            }
        }
    }
    search(this)

    //Vamos verificar se o espaço está redeado por 1 só tipo de peça
    return if(piece.contains(Piece.BLACK) && !piece.contains(Piece.WHITE)) Piece.BLACK
    else if(piece.contains(Piece.WHITE) && !piece.contains(Piece.BLACK)) Piece.WHITE
    else null
}