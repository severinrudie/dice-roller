import components.PagerTabsDisplay
import data.flow.Dispatcher
import data.flow.State
import components.CheckResultDisplay
import components.InputDisplay
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.KeyboardEvent
import kotlin.browser.document
import kotlin.browser.window

private lateinit var dispatcher: Dispatcher

fun main() {
    window.onload = {
        val serviceLocator = ServiceLocator(::display)
        dispatcher = serviceLocator.dispatcher
        dispatcher.dispatchInit()
    }
}

private fun display(state: State, previousState: State?) {


    if (state.selectedPager != previousState?.selectedPager) {
        pagerTabContainer.replaceWith(PagerTabsDisplay(state.selectedPager, dispatcher))
        inputContainer.replaceWith(InputDisplay(state.selectedPager, dispatcher))
        HtmlHacks.initInputs()
    }
    if (state.currentRollResults != previousState?.currentRollResults) {
        checkResultContainer.replaceWith(CheckResultDisplay(state.currentRollResults))
    }
}
