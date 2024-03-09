package blackjack.model

class Players(val value: List<Player>) {
    init {
        require(value.map { it.gameInfo.name }.distinct().size == value.size) { EXCEPTION_DUPLICATED_PLAYERS }
        require(value.size in PLAYER_NUM_RANGE) { EXCEPTION_NUMBER_OF_PLAYERS.format(value.size) }
        initializeCards()
    }

    private fun initializeCards() {
        value.forEach { player ->
            player.initializeCards(CardDeck::pick)
        }
    }

    companion object {
        private const val MINIMUM_NUMBER_OF_PLAYERS = 2
        private const val MAXIMUM_NUMBER_OF_PLAYERS = 8
        private val PLAYER_NUM_RANGE = MINIMUM_NUMBER_OF_PLAYERS..MAXIMUM_NUMBER_OF_PLAYERS
        private const val EXCEPTION_NUMBER_OF_PLAYERS =
            "플레이어의 수로 %d를 입력했습니다. 플레이어 수는 ${MINIMUM_NUMBER_OF_PLAYERS}부터 ${MAXIMUM_NUMBER_OF_PLAYERS}까지 가능합니다."
        private const val EXCEPTION_DUPLICATED_PLAYERS =
            "중복된 플레이어의 이름을 입력하셨습니다."

        fun of(
            playerNames: List<String>,
            onInputDecision: (String) -> String,
        ): Players {
            return playerNames
                .map { name ->
                    Player(gameInfo = GameInfo(name), onInputDecision = { onInputDecision(name) })
                }
                .run { Players(this) }
        }
    }
}