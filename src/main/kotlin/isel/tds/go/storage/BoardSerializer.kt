package isel.tds.go.storage

import isel.tds.go.model.*

object BoardSerializer: Serializer<Board> {
    override fun serialize(data: Board): String =
        data.boardCells.entries.joinToString("|") { (position, piece) ->
            "${position.row}${position.col}:$piece"
        } + "|" + "${data.turn}:${data.isFinished}:${data.whiteCaptures}:${data.blackCaptures}:${data.consecutivePasses}"

    override fun deserialize(data: String): Board {
        val plays = data.split("|")
        val newCells = mutableMapOf<Position, Piece?>()
        for (play in 0..plays.size-2) {
            val position = plays[play].split(":")[0]
            val piece = plays[play].split(":")[1]
            if (piece != "null") {
                newCells[Position(position[0].toString().toInt(), position[1])] = Piece.valueOf(piece)
            }
        }
        val gameInfo = plays[plays.size - 1].split(':')
        return Board(
            boardCells = newCells,
            turn = Piece.valueOf(gameInfo[0]),
            isFinished = gameInfo[1].toBoolean(),
            whiteCaptures = gameInfo[2].toInt(),
            blackCaptures = gameInfo[3].toInt(),
            consecutivePasses = gameInfo[4].toInt()
        )
    }
}