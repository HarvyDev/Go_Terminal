package isel.tds.go.view

import isel.tds.go.model.Board
import isel.tds.go.model.pass
import isel.tds.go.model.play
import isel.tds.go.model.toPosition
import isel.tds.go.storage.BoardSerializer


abstract class Command {
    open fun execute(args:List<String>, board:Board?): Board = throw IllegalStateException("GameOver")
    open val isToFinish = false
}

object Play : Command() {
    override fun execute(args: List<String>, board: Board?): Board {
        checkNotNull(board) { "Game hasn't started" }

        val pos = requireNotNull(args.firstOrNull()) { "Missing index" }

        return board.play(pos.toPosition())
    }
}

object Save : Command() {
    override fun execute(args: List<String>, board: Board?): Board {
        checkNotNull(board) { "Game hasn't started" }

        val serializedBoard = BoardSerializer.serialize(board)

        // TODO("Store the board state in the database")

        return board
    }
}

object Load : Command() {
    override fun execute(args: List<String>, board: Board?): Board {
        val serializedBoard = requireNotNull(args.firstOrNull()) { "Missing board" }

        return BoardSerializer.deserialize(serializedBoard)
    }
}

object Pass : Command() {
    override fun execute(args: List<String>, board: Board?): Board {
        checkNotNull(board) { "Game hasn't started" }
        return board.pass()
    }
}

object Exit: Command() {
    override val isToFinish = true

}

fun getCommands(): Map<String, Command> {
    return mapOf(
        "PLAY" to Play,
        "NEW" to object : Command() {
            override fun execute(args: List<String>, board: Board?) = Board()
        },
        "PASS" to object : Command() {
            override fun execute(args: List<String>, board: Board?): Board = board ?: throw IllegalStateException("Game hasn't started")
        },
        "SAVE" to Save,
        "LOAD" to Load,
        "EXIT" to Exit
    )
}