Игра

Количество участников равно двум

К серверу подсоединяются участники. После соединения первый и второй участники получают id для игры.<br>
`{id} started`<br>
Как только первые два участника подсоединятся, следующие участники не будут допущены сервером.

Сразу после подсоединения двух участников игра начинается. Сервер выбирает случайно первого игрока и кидает кубик с шестью гранями.
Выпавшее число сервер сообщает обоим клиентам вместе с id:<br>
`{id} {[1-6]}`<br>
Если выпало число от 2 до 6, клиент, чей id указан в ответе сервера, должен ответить<br>
`play/stop`<br>
Если игрок ответит `play`, сервер повторно бросает кубик для него, иначе сервер бросает кубик для другого клиента.
Если клиент ответ `stop`, сервер учитывает все очки за этот ход и суммирует их в таблице результатов.
Если выпало число 1, клиент не должен отвечать, при этом все очки за этот ход у клиента будут сброшены.

Кто первый наберёт 1000 очков или больше, тот победил.
В этом случае сервер сообщает, кто победил и результат игры:<br>
`{id} won`<br>
