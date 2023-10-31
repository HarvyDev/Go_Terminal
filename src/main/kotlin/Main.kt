import isel.tds.go.model.Board
import isel.tds.go.model.play
import isel.tds.go.model.show
import isel.tds.go.model.toPosition
import isel.tds.go.view.getCommands
import isel.tds.go.view.readCommandLine
import java.lang.IllegalArgumentException

fun main() {
//    val (cmd, args) = readCommandLine()
//    val position = args[0].toPosition()
//    println(cmd)
//    println(args)
//    println(position)
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
            }
            catch (e: Throwable) {
                println(e.message)
            }
            board?.show()
        }
    }
//    while(true){
//        val (name, args) = readCommandLine()
//        val cmd = commands[name]
//        if (cmd == null) {
//            println("Invalid Command $name")
//        }
//        else {
//            if (cmd.isToFinish) break
//            board = cmd.execute(args, board)
//        }
//        board?.show()
//    }
}