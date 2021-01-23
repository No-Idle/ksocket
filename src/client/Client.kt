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

                while (true) {
//                    writer.write("$cmd\n")
//                    writer.flush()

                    Thread.sleep(100)
                    while (reader.ready()) {
                        println(reader.readLine())
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}