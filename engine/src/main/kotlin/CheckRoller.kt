import kotlin.math.abs
import kotlin.math.max
import kotlin.random.Random

data class CheckInputs(val roll: Int, val threshold: Int, val margin: Int)

data class DramaticCheckResult(val inputs: CheckInputs, val didSucceed: Boolean, val successLevels: Int, val didCrit: Boolean)
data class SimpleCheckResult(val inputs: CheckInputs, val didSucceed: Boolean, val didCrit: Boolean)

enum class BodyLocation { HEAD, LEFT_ARM, RIGHT_ARM, BODY, LEFT_LEG, RIGHT_LEG }

internal val realDiceRoll = { (Random.nextDouble() * 100).toInt() }

@VisibleForTesting(otherwise = VisibleForTesting.INTERNAL)
class CheckRoller(private val d100Roll: () -> Int) {
    fun dramaticCheck(threshold: Int): DramaticCheckResult {
        val roll = d100Roll()
        val unmodifiedSuccessLevels = abs((roll / 10) - (threshold / 10))

        val (didSucceed, successLevels) = if (roll >= 96 && threshold >= 96) {
            false to max(1, unmodifiedSuccessLevels)
        } else if (roll <= 5 && threshold <= 5) {
            true to max(1, unmodifiedSuccessLevels)
        } else {
            (roll <= threshold) to unmodifiedSuccessLevels
        }
        val modifiedSL = if (didSucceed) successLevels else -successLevels

        val didCrit = roll == 100 ||
                (roll % 10) == (roll / 10)

        return DramaticCheckResult(CheckInputs(roll, threshold, threshold - roll), didSucceed, modifiedSL, didCrit)
    }

    fun simpleCheck(threshold: Int): SimpleCheckResult {
        val dramatic = dramaticCheck(threshold)
        return SimpleCheckResult(dramatic.inputs, dramatic.didSucceed, dramatic.didCrit)
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun flipDigits(roll: Int): Int = when (roll) {
        100 -> 100
        else -> (roll / 10) + ((roll % 10) * 10)
    }

    fun hitLocation(roll: Int): BodyLocation {
        val flippedRoll = flipDigits(roll)
        return when (flippedRoll) {
            in 1..9 -> BodyLocation.HEAD
            in 10..24 -> BodyLocation.LEFT_ARM
            in 25..44 -> BodyLocation.RIGHT_ARM
            in 45..79 -> BodyLocation.BODY
            in 80..89 -> BodyLocation.LEFT_LEG
            in 90..100 -> BodyLocation.RIGHT_LEG
            else -> throw IllegalArgumentException("Roll must be within 1-100")
        }
    }
}