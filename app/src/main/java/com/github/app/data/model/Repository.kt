package com.github.app.data.model

import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class Repository(
    val id: Long,
    val name: String,
    @SerializedName("full_name")
    val fullName: String,
    val description: String?,
    @SerializedName("html_url")
    val htmlUrl: String,
    @SerializedName("stargazers_count")
    val stars: Int,
    @SerializedName("forks_count")
    val forks: Int,
    @SerializedName("language")
    val language: String?,
    @SerializedName("updated_at")
    val updatedAt: String,
    val owner: Owner,
    val topics: List<String> = emptyList(),
    val private: Boolean = false,
    val fork: Boolean = false,
    val size: Int = 0,
    @SerializedName("open_issues_count")
    val openIssues: Int = 0,
    @SerializedName("default_branch")
    val defaultBranch: String = "main"
) : Parcelable

@Parcelize
data class Owner(
    val login: String,
    val id: Long,
    @SerializedName("avatar_url")
    val avatarUrl: String,
    @SerializedName("html_url")
    val htmlUrl: String,
    val type: String = "User"
) : Parcelable

data class RepositorySearchResponse(
    @SerializedName("total_count")
    val totalCount: Int,
    @SerializedName("incomplete_results")
    val incompleteResults: Boolean,
    val items: List<Repository>
)