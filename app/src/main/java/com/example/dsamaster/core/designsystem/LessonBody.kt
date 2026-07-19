package com.example.dsamaster.core.designsystem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Renders the lightweight lesson markup stored in LessonEntity.body:
 *   "# "  heading   ·  "- " bullet  ·  "> " callout  ·  "| " table row  ·  ``` code fence
 * Anything else is a paragraph. Blank lines separate blocks.
 */
sealed interface LessonBlock {
    data class Heading(val text: String) : LessonBlock
    data class Paragraph(val text: String) : LessonBlock
    data class Bullets(val items: List<String>) : LessonBlock
    data class Callout(val text: String) : LessonBlock
    data class Code(val code: String) : LessonBlock
    data class Table(val rows: List<List<String>>) : LessonBlock
}

fun parseLessonBody(body: String): List<LessonBlock> {
    val blocks = mutableListOf<LessonBlock>()
    val lines = body.lines()
    var i = 0
    val bullets = mutableListOf<String>()
    val tableRows = mutableListOf<List<String>>()

    fun flush() {
        if (bullets.isNotEmpty()) { blocks += LessonBlock.Bullets(bullets.toList()); bullets.clear() }
        if (tableRows.isNotEmpty()) { blocks += LessonBlock.Table(tableRows.toList()); tableRows.clear() }
    }

    while (i < lines.size) {
        val line = lines[i]
        when {
            line.startsWith("```") -> {
                flush()
                val code = StringBuilder(); i++
                while (i < lines.size && !lines[i].startsWith("```")) { code.appendLine(lines[i]); i++ }
                blocks += LessonBlock.Code(code.toString().trimEnd())
            }
            line.startsWith("# ") -> { flush(); blocks += LessonBlock.Heading(line.removePrefix("# ")) }
            line.startsWith("- ") -> bullets += line.removePrefix("- ")
            line.startsWith("> ") -> { flush(); blocks += LessonBlock.Callout(line.removePrefix("> ")) }
            line.startsWith("| ") -> tableRows += line.trim('|', ' ').split("|").map { it.trim() }
            line.isBlank() -> flush()
            else -> { flush(); blocks += LessonBlock.Paragraph(line) }
        }
        i++
    }
    flush()
    return blocks
}

@Composable
fun LessonBodyView(body: String, modifier: Modifier = Modifier) {
    val blocks = remember(body) { parseLessonBody(body) }
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        blocks.forEach { block ->
            when (block) {
                is LessonBlock.Heading -> Text(block.text, style = MaterialTheme.typography.titleLarge)
                is LessonBlock.Paragraph -> Text(block.text, style = MaterialTheme.typography.bodyLarge)
                is LessonBlock.Bullets -> Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    block.items.forEach { Text("•  $it", style = MaterialTheme.typography.bodyLarge) }
                }
                is LessonBlock.Callout -> Surface(
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    shape = MaterialTheme.shapes.medium,
                ) {
                    Text(
                        block.text,
                        Modifier.padding(12.dp).fillMaxWidth(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                    )
                }
                is LessonBlock.Code -> CodeBlock(block.code)
                is LessonBlock.Table -> Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.medium,
                ) {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        block.rows.forEachIndexed { index, row ->
                            Row(Modifier.fillMaxWidth()) {
                                row.forEach { cell ->
                                    Text(
                                        cell,
                                        Modifier.weight(1f),
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = if (index == 0) FontWeight.Bold else FontWeight.Normal,
                                        ),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
