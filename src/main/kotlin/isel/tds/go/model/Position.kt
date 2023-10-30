package isel.tds.go.model
data class Position(val row:Int, val col:Char) {

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


fun String.toPosition(): Position {
    val letterIndex = this.indexOfFirst { !it.isDigit() } // Find the idx of the first char that is not a number

    val row = this.substring(0, letterIndex).toInt()
    val col = this.substring(letterIndex)[0].uppercaseChar()

    return Position(row, col)
}
