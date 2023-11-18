package isel.tds.go.view

import isel.tds.go.model.*
import isel.tds.go.storage.Storage


abstract class Command {
    open fun execute(args:List<String>, game:Game): Game = throw IllegalStateException("Game Over")
    open val isToFinish = false
}

object Play : Command() {
    override fun execute(args: List<String>, game: Game): Game {
        checkNotNull(game.board) { "Game hasn't started" }

        val pos = requireNotNull(args.firstOrNull()) { "Missing index" }

        return game.play(pos.toPosition())
    }
}

object Pass : Command() {
    override fun execute(args: List<String>, game: Game): Game {
        checkNotNull(game.board) { "Game hasn't started" }
        return game.pass()
    }
}

object Resign: Command() {
    override fun execute(args: List<String>, game: Game): Game {
        checkNotNull(game.board) { "Game hasn't started" }
        return game.resign()
    }
}

fun getCommands(storage: Storage<String, Game>): Map<String, Command> {
    return mapOf(
        "PLAY" to Play,
        "NEW" to object : Command() {
            override fun execute(args: List<String>, game: Game) = Game()
        },
        "PASS" to Pass,
        "SAVE" to object : Command() {
            override fun execute(args: List<String>, game: Game): Game {
                checkNotNull(game.board) { "Game hasn't started" }
                require(args.isNotEmpty()) { "Missing file name" }

                val name = args.first()
                require(name.isNotEmpty()) { "Missing file name" }

                storage.create(name, game)

                return game
            }
        },
        "LOAD" to object : Command() {
            override fun execute(args: List<String>, game: Game): Game {
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