package isel.tds.go.view

import isel.tds.go.model.Board
import isel.tds.go.model.play
import isel.tds.go.model.toPosition

//interface Command {
//    fun newBoard(): Board
//    fun play(): Board
//    fun pass(): Board
//    fun save()
//    fun load(): Board
//    fun exit()
//}


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
        "PASS" to object : Command() {
            override fun execute(args: List<String>, board: Board?): Board = board ?: throw IllegalStateException("Game hasn't started")
        },
//        "SAVE" to Save,
//        "LOAD" to Load,
        "EXIT" to object : Command() {
            override val isToFinish = true
        }
    )
}