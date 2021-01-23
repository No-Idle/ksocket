package server

import java.io.*
import java.net.Socket

class ServerExecutor(private val socket: Socket, private val sockets: MutableMap<Int, Socket>) : Runnable {
    override fun run() {
        try {
            BufferedReader(InputStreamReader(socket.getInputStream())).use { reader ->
                while (true) {
                    while (!reader.ready()) Thread.sleep(100)

                    val request = reader.readLine()
                    println(request)
                    if (request == "Bye") break
                    val response = getResponse(request)

                    synchronized(sockets) {
                        val diedSockets = HashMap<Int, Socket>()
                        sockets.onEach { (id, socket) ->
                            try {
                                val writer = BufferedWriter(OutputStreamWriter(socket.getOutputStream()))
                                writer.write("Got: $request\n")
                                writer.write("$response\n")
                                writer.flush()
                            } catch (e: IOException) {
                                diedSockets[id] = socket
                            }
                        }

                        diedSockets.onEach { (id, socket) ->
                            sockets.remove(id)
                            socket.close()
                            //todo notify clients
                        }
                    }

                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun getResponse(request: String): String {
        return try {
            (request.toLong() * 2).toString()
        } catch (e: NumberFormatException) {
            "Bad request"
        }
    }
}