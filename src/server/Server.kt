package server

import java.io.IOException
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.atomic.AtomicInteger

object Server {
    private const val PORT = 8080

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
                repeat(2) {
                    val socket = server.accept()
                    synchronized(sockets) {
                        sockets[id.incrementAndGet()] = socket
                    }
                    Thread(ServerExecutor(socket, sockets)).start()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
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