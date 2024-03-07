package blackjack.controller

import blackjack.model.CardDeck
import blackjack.model.Dealer
import blackjack.model.PickingState
import blackjack.model.Player
import blackjack.view.InputView
import blackjack.view.OutputView

class BlackJackController(
    private val inputView: InputView,
    private val outputView: OutputView,
    private val cardDeck: CardDeck,
) {
    private lateinit var dealer: Dealer
    private lateinit var players: List<Player>

    fun startGame() {
        val playerNames = getPlayerNames()
        dealer = Dealer(playerNames.toSet())
        players =
            List(playerNames.size) { Player(playerNames[it], onInputDecision = { askPlayerHit(playerNames[it]) }) }
        dealer.drawCard { cardDeck.pick() }
        initializePlayerCards()
        displayInitializedCards()

        players.forEach { player ->
            drawSinglePlayer(player)
        }

        println()

        while (true) {
            val pickingState = dealer.drawCard { cardDeck.pick() }
            when (pickingState) {
                PickingState.CONTINUE -> {
                    outputView.printDealerHit(dealer.getStat())
                }
                PickingState.STOP -> break
            }
        }

        outputView.printFinalCards(dealer.getStat(), players.map { it.getStat() })
    }

    private fun drawSinglePlayer(player: Player) {
        while (true) {
            val pickingState = player.drawCard { cardDeck.pick() }
            when (pickingState) {
                PickingState.CONTINUE -> {
                    outputView.printSinglePlayerCards(playerStat = player.getStat())
                }

                PickingState.STOP -> break
            }
        }
    }

    private fun askPlayerHit(playerName: String): String = inputView.readContinueInput(playerName) ?: askPlayerHit(playerName)

    private fun displayInitializedCards() {
        val dealerStat = dealer.getStat()
        val playersStats = players.map { it.getStat() }
        outputView.printInitialStats(dealerStat, playersStats)
    }

    private fun initializePlayerCards() {
        players.forEach { player ->
            player.initializeCards {
                cardDeck.pick()
            }
        }
    }

    private fun getPlayerNames(): List<String> {
        return runCatching {
            inputView.readPlayerNames() ?: getPlayerNames()
        }.onFailure {
            println(it.message)
            return getPlayerNames()
        }.getOrThrow()
    }
}
