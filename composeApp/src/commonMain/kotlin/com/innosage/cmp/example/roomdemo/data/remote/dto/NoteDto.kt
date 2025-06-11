package com.innosage.cmp.example.roomdemo.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class NoteDto(val id: Long, val title: String, val content: String)
