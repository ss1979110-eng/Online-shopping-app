package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OrderStatus
import com.example.ui.theme.CoralAccent
import com.example.ui.theme.DangerRed
import com.example.ui.theme.MintSuccess
import com.example.ui.theme.RoyalBluePrimary

@Composable
fun StatusChip(
    status: OrderStatus,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (status) {
        OrderStatus.PENDING -> Color(0xFFFFEDD5) to CoralAccent
        OrderStatus.CONFIRMED -> Color(0xFFDBEAFE) to RoyalBluePrimary
        OrderStatus.SHIPPED -> Color(0xFFE0E7FF) to Color(0xFF4338CA)
        OrderStatus.OUT_FOR_DELIVERY -> Color(0xFFFEF3C7) to Color(0xFFD97706)
        OrderStatus.DELIVERED -> Color(0xFFD1FAE5) to MintSuccess
        OrderStatus.CANCELLED -> Color(0xFFFEE2E2) to DangerRed
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = status.label,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
