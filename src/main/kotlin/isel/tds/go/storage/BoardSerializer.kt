package isel.tds.go.storage

import isel.tds.go.model.*

object BoardSerializer: Serializer<Board> {

    override fun serialize(data: Board): String =
        data.boardCells.entries.joinToString("|") { (position, piece) ->
            "${position.row}${position.col}:$piece"
        } + "|" + "${data.turn}" // TODO("Add white and black capture counts")

    override fun deserialize(data: String): Board {
        val plays = data.split("|")
        val newCells = mutableMapOf<Position, Piece?>()
        for (play in 0..plays.size-2) {
            val position = plays[play].split(":")[0]
            val piece = plays[play].split(":")[1]
            newCells[Position(position[0].toString().toInt(), position[1])] = Piece.valueOf(piece)
        }
        return Board(newCells, Piece.valueOf(plays[plays.size - 1]))
    }
}