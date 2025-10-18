// app/src/main/java/com/onnway/android/ui/screens/CitySelectionScreen.kt
package com.onnway.android.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.onnway.android.R
import com.onnway.android.data.models.*
import com.onnway.android.ui.theme.*

@Composable
fun CitySelectionScreen(
    selectedCity: City?,
    selectedActivity: ActivityType?,
    selectedBudget: BudgetRange?,
    selectedDuration: Duration?,
    onCitySelect: (City) -> Unit,
    onActivitySelect: (ActivityType) -> Unit,
    onBudgetSelect: (BudgetRange) -> Unit,
    onDurationSelect: (Duration) -> Unit,
    onApply: () -> Unit,
    isLoading: Boolean
) {
    // SCROLL STATE EKLE
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)  // ← SCROLL EKLE
            .padding(16.dp)
    ) {
        // City Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Tallinn Card
            CityCard(
                city = City.TALLINN,
                title = "🇪🇪 Tallinn",
                subtitle = "Explore the medieval charm of Estonia's capital",
                isSelected = selectedCity == City.TALLINN,
                isBlurred = selectedCity != null && selectedCity != City.TALLINN,
                backgroundColor = TallinnColor,
                onClick = { onCitySelect(City.TALLINN) },
                modifier = Modifier.weight(1f)
            )

            // Istanbul Card
            CityCard(
                city = City.ISTANBUL,
                title = "🇹🇷 Istanbul",
                subtitle = "Discover where Europe meets Asia",
                isSelected = selectedCity == City.ISTANBUL,
                isBlurred = selectedCity != null && selectedCity != City.ISTANBUL,
                backgroundColor = IstanbulColor,
                onClick = { onCitySelect(City.ISTANBUL) },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Parameters Section
        AnimatedVisibility(
            visible = selectedCity != null,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut()
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = "Choose Your Preferences",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Activity Type
                    ParameterGroup(
                        title = "Activity Type",
                        options = ActivityType.values().toList(),
                        selectedOption = selectedActivity,
                        onOptionSelect = onActivitySelect,
                        getLabel = { activity ->
                            when (activity) {
                                ActivityType.FOOD -> "Food & Restaurants"
                                ActivityType.ART_HISTORY -> "Art & History"
                                ActivityType.SOCIAL_MEDIA -> "Social Media Spots"
                                ActivityType.ADVENTURE -> "Adventure"
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Budget Range
                    ParameterGroup(
                        title = "Budget Range",
                        options = BudgetRange.values().toList(),
                        selectedOption = selectedBudget,
                        onOptionSelect = onBudgetSelect,
                        getLabel = { budget ->
                            when (budget) {
                                BudgetRange.BUDGET -> "Budget Friendly"
                                BudgetRange.MID_RANGE -> "Mid Range"
                                BudgetRange.PREMIUM -> "Premium"
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Duration
                    ParameterGroup(
                        title = "Trip Duration",
                        options = Duration.values().toList(),
                        selectedOption = selectedDuration,
                        onOptionSelect = onDurationSelect,
                        getLabel = { duration ->
                            when (duration) {
                                Duration.SHORT -> "Short (3-4 hours)"
                                Duration.MEDIUM -> "Medium (1 day)"
                                Duration.LONG -> "Long (2+ days)"
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Apply Button
                    Button(
                        onClick = onApply,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = selectedCity != null &&
                                selectedActivity != null &&
                                selectedBudget != null &&
                                selectedDuration != null &&
                                !isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SuccessColor,
                            disabledContainerColor = Gray
                        )
                    ) {
                        Text(
                            text = if (isLoading) "CREATING ROUTE..." else "CREATE MY ROUTE",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // BOTTOM PADDING - Scroll sonunda boşluk
        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
fun CityCard(
    city: City,
    title: String,
    subtitle: String,
    isSelected: Boolean,
    isBlurred: Boolean,
    backgroundColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val alpha = if (isBlurred) 0.4f else 1f

    // Şehre göre resim seç
    val backgroundImage = when (city) {
        City.TALLINN -> com.onnway.android.R.drawable.tallinn
        City.ISTANBUL -> com.onnway.android.R.drawable.istanbul
    }

    Card(
        modifier = modifier
            .height(300.dp)
            .alpha(alpha)
            .clickable(enabled = !isBlurred, onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        border = if (isSelected) BorderStroke(3.dp, backgroundColor) else null,
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 4.dp
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Background image
            Image(
                painter = painterResource(id = backgroundImage),
                contentDescription = "$title background",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Dark overlay for better text readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.4f),
                                Color.Black.copy(alpha = 0.7f)
                            )
                        )
                    )
            )

            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = White
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = subtitle,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = White,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun <T> ParameterGroup(
    title: String,
    options: List<T>,
    selectedOption: T?,
    onOptionSelect: (T) -> Unit,
    getLabel: (T) -> String
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            options.forEach { option ->
                ParameterOption(
                    label = getLabel(option),
                    isSelected = option == selectedOption,
                    onClick = { onOptionSelect(option) }
                )
            }
        }
    }
}

@Composable
fun ParameterOption(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        color = if (isSelected) {
            PrimaryColor.copy(alpha = 0.1f)
        } else {
            LightGray
        },
        border = if (isSelected) {
            BorderStroke(2.dp, PrimaryColor)
        } else {
            null
        }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = PrimaryColor
                )
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) PrimaryColor else DarkGray
            )
        }
    }
}