package server

import java.io.IOException
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket

object Server {
    private const val PORT = 8080

    private var id = 0

    @JvmStatic
    fun main(args: Array<String>) {
        Server.start()
    }

    private fun start() {

        try {
            ServerSocket(PORT).use { server ->
                println("Server has been started: " + getIpAddress())
                val sockets = HashMap<String, Socket>()
                while (true) {
                    val socket = server.accept()
                    synchronized(sockets) {
                        sockets[nextId()] = socket
                    }
                    Thread(ServerExecutor(socket, sockets)).start()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun getIpAddress(): String {
        val ipAddress = StringBuilder()
        for (b in InetAddress.getLocalHost().address) {
            ipAddress.append((b + 256) % 256).append('.')
        }
        return ipAddress.substring(0, ipAddress.length - 1)
    }

    @Synchronized
    fun nextId(): String = "${++id}"
}