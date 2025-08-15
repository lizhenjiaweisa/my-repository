package com.github.app.data.model

import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class Issue(
    val id: Long,
    val number: Int,
    val title: String,
    val body: String?,
    val state: String,
    val user: Owner,
    val assignee: Owner?,
    val assignees: List<Owner> = emptyList(),
    val labels: List<Label> = emptyList(),
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("closed_at")
    val closedAt: String?,
    @SerializedName("comments")
    val commentsCount: Int = 0
) : Parcelable

@Parcelize
data class Label(
    val id: Long,
    val name: String,
    val color: String,
    val description: String?
) : Parcelable

data class CreateIssueRequest(
    val title: String,
    val body: String? = null,
    val labels: List<String>? = null,
    val assignees: List<String>? = null
)

data class IssueListResponse(
    val items: List<Issue>
)