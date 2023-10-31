package isel.tds.go.view

data class CommandLine(
    val name:String,
    val args: List<String>
)

fun readCommandLine(): CommandLine {
    print("> ")
    val line = readln().split(" ").filter{ it.isNotBlank() }
    return if (line.isEmpty() || line.size > 2) readCommandLine()
        else CommandLine(line.first().uppercase(), line.drop(1))
}