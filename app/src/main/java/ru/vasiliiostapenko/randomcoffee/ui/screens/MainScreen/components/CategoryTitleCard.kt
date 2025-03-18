package ru.vasiliiostapenko.randomcoffee.ui.screens.MainScreen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Job
import ru.vasiliiostapenko.randomcoffee.ui.theme.TransparentColor

@Composable
fun CategoryTitleCard(title: String, isActive: Boolean, onClick: () -> Job) {
    Card(
        modifier = Modifier.padding(horizontal = 2.dp),
        colors = CardDefaults.cardColors(containerColor = TransparentColor)
    ) {
        Button(
            onClick = { onClick() },
            // shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isActive) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.tertiary,
            ),
            modifier = Modifier.background(TransparentColor),

            ) {
            Text(
                text = title,
                modifier = Modifier.padding(horizontal = 2.dp, vertical = 1.dp)
            )
        }
    }
}