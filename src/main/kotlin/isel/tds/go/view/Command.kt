package isel.tds.go.view

import isel.tds.go.model.*

abstract class Command(val argSyntax: String = "") {
    open fun execute(args:List<String>, board:Board?): Board = throw IllegalStateException("GameOver")
    open val isToFinish = false
}

object Play : Command("pos") {
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

object Exit: Command() {
    override val isToFinish = true

}

//object Save : Command() {
//    TODO()
//}
//object Load : Command() {
//    TODO()
//}


fun getCommands(): Map<String, Command> {
    return mapOf(
        "PLAY" to Play,
        "NEW" to object : Command() {
            override fun execute(args: List<String>, board: Board?) = Board()
        },
        "PASS" to Pass,
//        "SAVE" to Save,
//        "LOAD" to Load,
        "EXIT" to Exit
    )
}