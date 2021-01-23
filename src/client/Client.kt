package client

import java.io.*
import java.net.Socket

object Client {
    private const val PORT = 8080

    @Throws(InterruptedException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        try {
            Socket("172.16.172.25", PORT).use { socket ->
//                val consoleReader = BufferedReader(InputStreamReader(System.`in`))
                val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
                val writer = BufferedWriter(OutputStreamWriter(socket.getOutputStream()))

                var id = "0"
                while (true) {
                    Thread.sleep(100)
                    while (reader.ready()) {
                        val cmd = Command(reader.readLine()!!.split(" "))
                        when (cmd.data) {
                            "started" -> id = cmd.id
                            "won" -> println("${cmd.id} won ${if (cmd.id == id) "YES!!!" else "NO :-("}")
                            else -> if (cmd.id == id && cmd.data != "1") {
                                writer.write("stop\n")
                                writer.flush()
                            }
                        }
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}