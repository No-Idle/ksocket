package client

data class Command(val cmd: List<String>) {
    val id = cmd[0]
    val data = cmd[1]
}
