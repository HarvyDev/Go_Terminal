package isel.tds.go.view

import isel.tds.go.model.*
import isel.tds.go.storage.Storage


abstract class Command {
    open fun execute(args:List<String>, board:Board?): Board = throw IllegalStateException("Game Over")
    open val isToFinish = false
}

object Play : Command() {
    override fun execute(args: List<String>, board: Board?): Board {
        checkNotNull(board) { "Game hasn't started" }

        val pos = requireNotNull(args.firstOrNull()) { "Missing index" }

        return board.play(pos.toPosition())
    }
}

object Pass : Command() {
    override fun execute(args: List<String>, board: Board?): Board {
        checkNotNull(board) { "Game hasn't started" }
        return board.pass()
    }
}

object Resign: Command() {
    override fun execute(args: List<String>, board: Board?): Board {
        checkNotNull(board) { "Game hasn't started" }
        return board.resign()
    }
}

fun getCommands(storage: Storage<String, Board>): Map<String, Command> {
    return mapOf(
        "PLAY" to Play,
        "NEW" to object : Command() {
            override fun execute(args: List<String>, board: Board?) = Board()
        },
        "PASS" to Pass,
        "SAVE" to object : Command() {
            override fun execute(args: List<String>, board: Board?): Board {
                checkNotNull(board) { "Game hasn't started" }
                require(args.isNotEmpty()) { "Missing file name" }

                val name = args.first()
                require(name.isNotEmpty()) { "Missing file name" }

                storage.create(name, board)

                return board
            }
        },
        "LOAD" to object : Command() {
            override fun execute(args: List<String>, board: Board?): Board {
                val name = requireNotNull(args.firstOrNull()) { "Missing file name" }
                return checkNotNull(storage.read(name)) { "Game $name not found" }
            }
        },
        "EXIT" to object : Command() {
            override val isToFinish = true
        },
        "RESIGN" to Resign
    )
}