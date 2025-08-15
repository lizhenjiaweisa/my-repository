package com.github.app.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageFilter(
    selectedLanguage: String?,
    onLanguageSelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    
    val languages = listOf(
        null to "All Languages",
        "Kotlin" to "Kotlin",
        "Java" to "Java", 
        "JavaScript" to "JavaScript",
        "Python" to "Python",
        "TypeScript" to "TypeScript",
        "C++" to "C++",
        "C" to "C",
        "C#" to "C#",
        "PHP" to "PHP",
        "Ruby" to "Ruby",
        "Go" to "Go",
        "Rust" to "Rust",
        "Swift" to "Swift",
        "Dart" to "Dart",
        "Shell" to "Shell",
        "HTML" to "HTML",
        "CSS" to "CSS"
    )

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedLanguage ?: "All Languages",
            onValueChange = {},
            readOnly = true,
            label = { Text("Language") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            languages.forEach { (language, displayName) ->
                DropdownMenuItem(
                    text = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            language?.let { lang ->
                                val color =  MaterialTheme.colorScheme.primary
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .background(color, shape = MaterialTheme.shapes.small)
                                )
                            }
                            Text(displayName)
                        }
                    },
                    onClick = {
                        onLanguageSelected(language)
                        expanded = false
                    }
                )
            }
        }
    }
}