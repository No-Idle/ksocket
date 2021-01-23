package server

import java.io.*
import java.net.Socket
import java.net.SocketTimeoutException

data class Player(val id: Int, val socket: Socket) {
    private val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
    private val writer = BufferedWriter(OutputStreamWriter(socket.getOutputStream()))
    var score = 0
    var current = 0

    fun notifyDiceRolled(id: Int, attempt: Int) = notify("$id $attempt")
    fun notifyWon(id: Int) = notify("$id won")
    fun notifyStart() = notify("$id started")

    private fun notify(msg: String) {
        writer.write("$msg\n")
        writer.flush()
    }

    fun getResponse(): String {
        repeat(50) {
            if (reader.ready()) return reader.readLine()
            Thread.sleep(100)
        }
        throw SocketTimeoutException("Response Timeout")
    }
}