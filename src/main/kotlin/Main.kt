import isel.tds.go.model.*
import isel.tds.go.view.getCommands
import isel.tds.go.view.readCommandLine
import java.lang.IllegalArgumentException

fun main() {
    var board: Board? = null
    val commands = getCommands()
    while(true){
        val (name, args) = readCommandLine()
        val cmd = commands[name]
        if (cmd == null) {
            println("Invalid Command $name")
        }
        else {
            try {
                if (cmd.isToFinish) break
                board = cmd.execute(args, board)
                board = board.clean()
                println(board.countLiberties(args[0].toPosition()))
            }
            catch (e: Throwable) {
                println(e.message)
            }
            board?.show()
            println()
        }
    }
}