package com.luuzr.jielv.feature.notes

import androidx.compose.ui.text.AnnotatedString
import org.junit.Assert.assertEquals
import org.junit.Test

class NoteImageReferenceVisualTransformationTest {

    @Test
    fun `image markdown is shown as readable placeholder`() {
        val transformation = NoteImageReferenceVisualTransformation()

        val transformed = transformation.filter(
            AnnotatedString("你好 ![image](local://media/abc) 世界")
        )

        assertEquals("你好 〔图片1〕 世界", transformed.text.text)
    }
}
