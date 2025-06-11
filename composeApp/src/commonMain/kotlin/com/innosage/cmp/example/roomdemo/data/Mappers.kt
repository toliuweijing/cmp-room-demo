package com.innosage.cmp.example.roomdemo.data

import com.innosage.cmp.example.roomdemo.data.local.NoteEntity
import com.innosage.cmp.example.roomdemo.domain.model.Note

fun NoteEntity.toDomainModel(): Note {
    return Note(id = this.id, title = this.title, content = this.content)
}
