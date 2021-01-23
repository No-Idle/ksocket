package server

import java.io.IOException
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketTimeoutException
import java.util.concurrent.atomic.AtomicInteger

object Server {
    private const val PORT = 8080
    private const val PLAYER_COUNT = 2
    private const val WIN_SCORE = 100

    private var id = AtomicInteger()

    @JvmStatic
    fun main(args: Array<String>) {
        start()
    }

    private fun start() {
        try {
            ServerSocket(PORT).use { server ->
                println("Server has been started: " + getIpAddress())
                val sockets = HashMap<Int, Socket>()
                repeat(PLAYER_COUNT) {
                    val socket = server.accept()
                    synchronized(sockets) {
                        sockets[id.incrementAndGet()] = socket
                        println("Player $id joined the room")
                    }
                }

                val players = sockets.map { (k, v) -> k to v }.shuffled().map { (id, socket) -> Player(id, socket) }.toMutableList()
                notifyAll(players) {player -> player.notifyStart()}
                println("The game started with players ${players.map { it.id }}")
                var idx = 0
                while (players.isNotEmpty() && players.all { it.score < WIN_SCORE }) {
                    val currentPlayer = players[idx++ % players.size]
                    currentPlayer.current = 0

                    do {
                        val attempt = (1..6).random()
                        notifyAll(players) { player -> player.notifyDiceRolled(currentPlayer.id, attempt) }

                        val response = if (attempt > 1) {
                            currentPlayer.current += attempt
                            try {
                                currentPlayer.getResponse()
                            } catch (e: SocketTimeoutException) {
                                players.remove(currentPlayer)
                                idx--
                                "stop"
                            }
                        } else {
                            currentPlayer.current = 0
                            "stop"
                        }
                    } while (response == "play")
                    currentPlayer.score += currentPlayer.current
                    println("player ${currentPlayer.id} got ${currentPlayer.current}")
                }
                val winner = players.find { it.score >= WIN_SCORE }
                if (winner != null) {
                    notifyAll(players) { player -> player.notifyWon(winner.id) }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun notifyAll(players: MutableList<Player>, notify: (Player) -> Unit) {
        val it = players.iterator()
        while (it.hasNext()) {
            val player = it.next()
            try {
                notify(player)
            } catch (e: IOException) {
                it.remove()
            }
        }
    }

    private fun getIpAddress(): String {
        val ipAddress = StringBuilder()
        for (b in InetAddress.getLocalHost().address) {
            ipAddress.append((b + 256) % 256).append('.')
        }
        return ipAddress.substring(0, ipAddress.length - 1)
    }
}