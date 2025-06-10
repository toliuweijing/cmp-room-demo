package org.example.project.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class NoteDto(val id: Long, val title: String, val content: String)