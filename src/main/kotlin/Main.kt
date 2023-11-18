import isel.tds.go.model.*
import isel.tds.go.mongo.MongoDriver
import isel.tds.go.storage.GameSerializer
import isel.tds.go.storage.MongoStorage
import isel.tds.go.view.getCommands
import isel.tds.go.view.readCommandLine
import isel.tds.go.view.show

fun main() {
    MongoDriver("Go").use { driver ->
        var game: Game = Game()
        val storage = MongoStorage<String, Game>("saves", driver, GameSerializer)
        val commands = getCommands(storage)

        while(true){
            game = game.end()
            val (name, args) = readCommandLine()
            val cmd = commands[name]
            if (cmd == null) {
                println("Invalid Command $name")
            }
            else {
                try {
                    if (cmd.isToFinish) break
                    game = cmd.execute(args, game)

                }
                catch (e: Throwable) {
                    println(e.message)
                }
                game.board.show()
                println("Turn: ${game.turn.symbol}(${game.turn}) Captures: #=${game.blackScore}  0=${game.whiteScore}")
                println()
            }
        }
    }
}

