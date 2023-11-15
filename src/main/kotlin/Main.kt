import isel.tds.go.model.*
import isel.tds.go.mongo.MongoDriver
import isel.tds.go.storage.BoardSerializer
import isel.tds.go.storage.MongoStorage
import isel.tds.go.view.getCommands
import isel.tds.go.view.readCommandLine

fun main() {
    MongoDriver("Go").use { driver ->
        var board: Board? = Board()
        val storage = MongoStorage<String, Board>("saves", driver, BoardSerializer)
        val commands = getCommands(storage)

        while(true){
            board?.score()
            board = board?.end()
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
                println("It's" + " ${board?.turn?.symbol}" + "'s" + " turn")
                println("Captures: Black: ${board?.blackCaptures}, White: ${board?.whiteCaptures}")
                println()
            }
        }
    }
}








