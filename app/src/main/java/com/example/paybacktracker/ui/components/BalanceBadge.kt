package com.example.paybacktracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.paybacktracker.ui.theme.GreenLight
import com.example.paybacktracker.ui.theme.GreenPrimary
import com.example.paybacktracker.ui.theme.RedLight
import com.example.paybacktracker.ui.theme.RedPrimary
import com.example.paybacktracker.ui.theme.Slate200
import com.example.paybacktracker.ui.theme.Slate600

@Composable
fun BalanceBadge(
    balance: Double,
    modifier: Modifier = Modifier
) {
    val (text, bgColor, textColor) = when {
        balance > 0 -> Triple("В плюсе", GreenLight, GreenPrimary)
        balance < 0 -> Triple("В минусе", RedLight, RedPrimary)
        else -> Triple("В нуле", Slate200, Slate600)
    }

    Text(
        text = text,
        color = textColor,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}
