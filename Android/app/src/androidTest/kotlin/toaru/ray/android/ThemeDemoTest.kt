package toaru.ray.android

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollTo
import org.junit.Rule
import org.junit.Test

class ThemeDemoTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun themeDemo_showsExpectedContent() {
        val title = composeTestRule.activity.getString(R.string.theme_demo_title)
        val switchLabel = composeTestRule.activity.getString(R.string.theme_demo_switch_label)
        val cta = composeTestRule.activity.getString(R.string.theme_demo_cta)

        composeTestRule.onNodeWithText(title).assertIsDisplayed()
        composeTestRule.onNodeWithText(switchLabel).assertIsDisplayed()
        composeTestRule.onNodeWithText(cta).performScrollTo().assertIsDisplayed()
    }
}
