package com.example.dsamaster

import com.example.dsamaster.core.designsystem.LessonBlock
import com.example.dsamaster.core.designsystem.parseLessonBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LessonBodyParserTest {

    @Test
    fun `parses headings paragraphs bullets code and tables`() {
        val body = """
            # Title
            A paragraph.
            - one
            - two
            | Op | Time |
            | get | O(1) |
            ```
            val x = 1
            ```
            > A callout
        """.trimIndent()
        val blocks = parseLessonBody(body)
        assertTrue(blocks[0] is LessonBlock.Heading)
        assertTrue(blocks[1] is LessonBlock.Paragraph)
        assertEquals(listOf("one", "two"), (blocks[2] as LessonBlock.Bullets).items)
        assertEquals(2, (blocks[3] as LessonBlock.Table).rows.size)
        assertEquals("val x = 1", (blocks[4] as LessonBlock.Code).code)
        assertTrue(blocks[5] is LessonBlock.Callout)
    }
}
