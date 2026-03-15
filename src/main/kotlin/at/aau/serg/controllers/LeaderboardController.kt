package at.aau.serg.controllers

import at.aau.serg.models.GameResult
import at.aau.serg.services.GameResultService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import kotlin.math.max
import kotlin.math.min

@RestController
@RequestMapping("/leaderboard")
class LeaderboardController(
    private val gameResultService: GameResultService
) {

    @GetMapping
    fun getLeaderboard(@RequestParam(required = false) rank: Int?): List<GameResult> {
        // Sort by score descending, then time ascending
        val sorted = gameResultService.getGameResults()
            .sortedWith(compareByDescending<GameResult> { it.score }
                .thenBy { it.timeInSeconds })

        if (rank == null) {
            // No rank specified → return full leaderboard
            return sorted
        }

        // Invalid rank → throw exception
        if (rank < 1 || rank > sorted.size) {
            throw IllegalArgumentException("Invalid rank")
        }

        // Calculate start/end indices for 3 above + player + 3 below
        val start = max(rank - 4, 0)               // rank is 1-based
        val end = min(rank + 3, sorted.size)

        return sorted.subList(start, end)
    }
}