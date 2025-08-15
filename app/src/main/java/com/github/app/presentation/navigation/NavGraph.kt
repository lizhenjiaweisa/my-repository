package com.github.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.github.app.data.model.Repository
import com.github.app.data.model.User
import com.github.app.presentation.screen.*

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object RepositoryDetail : Screen("repository/{owner}/{repo}") {
        fun createRoute(owner: String, repo: String) = "repository/$owner/$repo"
    }
    object UserProfile : Screen("user/{username}") {
        fun createRoute(username: String) = "user/$username"
    }
    object Auth : Screen("auth")
    object Issues : Screen("issues/{owner}/{repo}") {
        fun createRoute(owner: String, repo: String) = "issues/$owner/$repo"
    }
    object CreateIssue : Screen("create-issue/{owner}/{repo}") {
        fun createRoute(owner: String, repo: String) = "create-issue/$owner/$repo"
    }
}

@Composable
fun GitHubNavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Home.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onRepositoryClick = { owner, repo ->
                    navController.navigate(Screen.RepositoryDetail.createRoute(owner, repo))
                },
                onUserProfileClick = { username ->
                    navController.navigate(Screen.UserProfile.createRoute(username))
                },
                onAuthClick = {
                    navController.navigate(Screen.Auth.route)
                },
                onIssuesClick = { owner, repo ->
                    navController.navigate(Screen.Issues.createRoute(owner, repo))
                }
            )
        }

        composable(Screen.RepositoryDetail.route) { backStackEntry ->
            val owner = backStackEntry.arguments?.getString("owner") ?: ""
            val repo = backStackEntry.arguments?.getString("repo") ?: ""

            // TODO
            RepositoryDetailScreen(
                owner = owner,
                repoName = repo,
                onBackClick = { navController.popBackStack() },
                onUserClick = { username ->
                    navController.navigate(Screen.UserProfile.createRoute(username))
                },
                onIssuesClick = { repoOwner, name ->
                    navController.navigate(Screen.Issues.createRoute(repoOwner, name))
                }
            )
        }

        composable(Screen.UserProfile.route) { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username")
            
            UserProfileScreen(
                username = username ?: "",
                onBackClick = { navController.popBackStack() },
                onRepositoryClick = { owner, repo ->
                    navController.navigate(Screen.RepositoryDetail.createRoute(owner, repo))
                },
                onIssuesClick = { owner,name ->

                }
            )
        }

        composable(Screen.Auth.route) {
            AuthScreen(
                onAuthSuccess = {
                    navController.popBackStack(Screen.Home.route, inclusive = false)
                },
                onCancel = { navController.popBackStack() }
            )
        }

        composable(Screen.Issues.route) { backStackEntry ->
            val owner = backStackEntry.arguments?.getString("owner") ?: ""
            val repo = backStackEntry.arguments?.getString("repo") ?: ""
            
            IssuesScreen(
                owner = owner,
                repoName = repo,
                onBackClick = { navController.popBackStack() },
                onCreateIssue = {
                    // TODO
//                    navController.navigate(Screen.CreateIssue.createRoute(repoOwner, repoName))
                }
            )
        }

        composable(Screen.CreateIssue.route) { backStackEntry ->
            val owner = backStackEntry.arguments?.getString("owner") ?: ""
            val repo = backStackEntry.arguments?.getString("repo") ?: ""
            
            CreateIssueScreen(
                owner = owner,
                repoName = repo,
                onBackClick = { navController.popBackStack() },
                onIssueCreated = {
                    navController.popBackStack()
                }
            )
        }
    }
}