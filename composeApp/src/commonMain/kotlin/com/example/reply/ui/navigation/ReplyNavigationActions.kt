package com.example.reply.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import compose.icons.FeatherIcons
import compose.icons.feathericons.FileText
import compose.icons.feathericons.MessageCircle
import compose.icons.feathericons.Users
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource
import reply.composeapp.generated.resources.Res
import reply.composeapp.generated.resources.tab_article
import reply.composeapp.generated.resources.tab_inbox

sealed interface Route {
    @Serializable data object Inbox : Route
    @Serializable data object Articles : Route
    @Serializable data object DirectMessages : Route
    @Serializable data object Groups : Route
}

data class ReplyTopLevelDestination(
    val route: Route,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val iconTextId: StringResource // FIXME: iconTextId -> iconTextRes
)

class ReplyNavigationActions(private val navController: NavHostController) {

    fun navigateTo(destination: ReplyTopLevelDestination) {
        navController.navigate(destination.route) {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            // Avoid multiple copies of the same destination when
            // reselecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
            restoreState = true
        }
    }
}

val TOP_LEVEL_DESTINATIONS = listOf(
    ReplyTopLevelDestination(
        route = Route.Inbox,
        selectedIcon = Icons.Default.Email,
        unselectedIcon = Icons.Default.Email,
        iconTextId = Res.string.tab_inbox
    ),
    ReplyTopLevelDestination(
        route = Route.Articles,
        selectedIcon = FeatherIcons.FileText,
        unselectedIcon = FeatherIcons.FileText,
        iconTextId = Res.string.tab_article
    ),
    ReplyTopLevelDestination(
        route = Route.DirectMessages,
        selectedIcon = FeatherIcons.MessageCircle,
        unselectedIcon = FeatherIcons.MessageCircle,
        iconTextId = Res.string.tab_inbox
    ),
    ReplyTopLevelDestination(
        route = Route.Groups,
        selectedIcon = FeatherIcons.Users,
        unselectedIcon = FeatherIcons.Users,
        iconTextId = Res.string.tab_article
    )

)
