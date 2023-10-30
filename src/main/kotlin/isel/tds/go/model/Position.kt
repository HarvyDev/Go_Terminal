package isel.tds.go.model
class Position(val row:Int, val col:Char)


fun String.toPosition(): Position {
    val letterIndex = this.indexOfFirst { !it.isDigit() } // Find the idx of the first char that is not a number

    val row = this.substring(0, letterIndex).toInt()
    val col = this.substring(letterIndex)[0].uppercaseChar()

    return Position(row, col)
}