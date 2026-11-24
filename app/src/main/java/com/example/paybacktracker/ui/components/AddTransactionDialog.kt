package com.example.paybacktracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.paybacktracker.data.entity.TransactionType
import com.example.paybacktracker.ui.theme.GreenLight
import com.example.paybacktracker.ui.theme.GreenPrimary
import com.example.paybacktracker.ui.theme.RedLight
import com.example.paybacktracker.ui.theme.RedPrimary
import com.example.paybacktracker.ui.theme.Slate200
import com.example.paybacktracker.ui.theme.Slate500
import com.example.paybacktracker.ui.theme.Slate900

@Composable
fun AddTransactionDialog(
    initialType: TransactionType = TransactionType.EXPENSE,
    onDismiss: () -> Unit,
    onConfirm: (amount: Double, type: TransactionType) -> Unit
) {
    var selectedType by remember { mutableStateOf(initialType) }
    var amountText by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (selectedType == TransactionType.EXPENSE) "Расход" else "Доход",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Segmented control to switch between Expense and Income
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Slate200)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val isExpense = selectedType == TransactionType.EXPENSE
                    val isIncome = selectedType == TransactionType.INCOME

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isExpense) RedLight else Color.Transparent)
                            .clickable { selectedType = TransactionType.EXPENSE }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Потратил",
                            color = if (isExpense) RedPrimary else Slate500,
                            fontWeight = if (isExpense) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 15.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isIncome) GreenLight else Color.Transparent)
                            .clickable { selectedType = TransactionType.INCOME }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Получил",
                            color = if (isIncome) GreenPrimary else Slate500,
                            fontWeight = if (isIncome) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 15.sp
                        )
                    }
                }

                // Numeric input field
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { input ->
                        val sanitized = input.replace(',', '.')
                        if (sanitized.isEmpty() || sanitized.matches(Regex("""^\d*(\.\d{0,2})?$"""))) {
                            amountText = sanitized
                            if (isError) {
                                isError = false
                                errorMessage = ""
                            }
                        }
                    },
                    label = { Text("Сумма") },
                    placeholder = { Text("0") },
                    trailingIcon = {
                        Text(
                            text = "₽",
                            style = MaterialTheme.typography.titleMedium,
                            color = Slate500,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                    },
                    singleLine = true,
                    isError = isError,
                    supportingText = {
                        if (isError) {
                            Text(errorMessage, color = RedPrimary)
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (selectedType == TransactionType.EXPENSE) RedPrimary else GreenPrimary,
                        cursorColor = if (selectedType == TransactionType.EXPENSE) RedPrimary else GreenPrimary
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val parsed = amountText.toDoubleOrNull()
                    if (parsed == null || parsed <= 0) {
                        isError = true
                        errorMessage = "Введите корректную сумму больше нуля"
                    } else {
                        onConfirm(parsed, selectedType)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedType == TransactionType.EXPENSE) RedPrimary else GreenPrimary
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = if (selectedType == TransactionType.EXPENSE) "+ Потратил" else "+ Получил",
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
        shape = RoundedCornerShape(20.dp),
        containerColor = MaterialTheme.colorScheme.surface
    )
}
