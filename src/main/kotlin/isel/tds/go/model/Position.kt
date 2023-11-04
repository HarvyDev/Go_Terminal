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

    val row = this.substring(0, letterIndex).toInt()
    val col = this.substring(letterIndex)[0].uppercaseChar()

    return Position(row, col)
}
