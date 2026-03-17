package com.luuzr.jielv.domain.model

data class NoteDetail(
    val note: Note,
    val images: List<NoteImage> = emptyList(),
)
