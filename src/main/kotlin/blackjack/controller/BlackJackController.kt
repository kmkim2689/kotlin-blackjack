package blackjack.controller

import blackjack.model.CardDeck
import blackjack.model.Dealer
import blackjack.model.GameInfo
import blackjack.model.Judge
import blackjack.model.Players
import blackjack.view.InputView
import blackjack.view.OutputView

object BlackJackController {
    private lateinit var players: Players
    private val dealer: Dealer = Dealer()

    fun startGame() {
        initializePlayers()
        initializeParticipantsCards()
        playRound()
        displayResult()
    }

    private fun initializePlayers() {
        runCatching {
            val playerNames = getPlayerNames()
            players = Players.of(playerNames, ::askPlayerHit, CardDeck::pick)
        }.onFailure {
            println(it.message)
            initializePlayers()
        }
    }

    private fun getPlayerNames(): List<String> {
        return runCatching {
            InputView.readPlayerNames()
        }.onFailure {
            println(it.message)
            return getPlayerNames()
        }.getOrThrow()
    }

    private fun initializeParticipantsCards() {
        dealer.drawSingleCard(CardDeck::pick)
        displayInitializedCards(dealer.gameInfo, players.value.map { it.gameInfo })
    }

    private fun playRound() {
        players.value.forEach { player ->
            player.drawCardsUntilStand(CardDeck::pick, OutputView::printSinglePlayerCards)
        }
        OutputView.printNewLine()
        dealer.drawCardsUntilStand(CardDeck::pick, OutputView::printDealerHit)
    }

    private fun displayResult() {
        val dealerInfo = dealer.gameInfo
        val playersInfo = players.value.map { player -> player.gameInfo }
        val judge = Judge(dealerInfo, playersInfo)
        with(OutputView) {
            printFinalCards(dealerInfo, playersInfo)
            printResult(judge)
        }
    }

    private fun askPlayerHit(playerName: String): String =
        runCatching {
            InputView.readContinueInput(playerName)
        }.onFailure {
            return askPlayerHit(playerName)
        }.getOrThrow()

    private fun displayInitializedCards(
        dealerInfo: GameInfo,
        playersInfo: List<GameInfo>,
    ) {
        OutputView.printInitialStats(dealerInfo, playersInfo)
    }
}
