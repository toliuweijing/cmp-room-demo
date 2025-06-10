package org.example.project.data

import org.example.project.data.local.NoteEntity
import org.example.project.domain.model.Note

fun NoteEntity.toDomainModel(): Note {
    return Note(id = this.id, title = this.title, content = this.content)
}