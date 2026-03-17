package com.luuzr.jielv

import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import org.junit.Test

class HabitCrudSmokeTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun opensCreateHabitScreen() {
        val title = "HabitSmoke${System.currentTimeMillis()}"

        composeRule.onNodeWithTag("nav_habits").performClick()
        composeRule.onNodeWithTag("habits_fab").performClick()
        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodes(hasTestTag("habit_editor_title_input"))
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithTag("habit_editor_title_input").performTextInput(title)
    }
}
