package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.Biotech
import androidx.compose.material.icons.rounded.Calculate
import androidx.compose.material.icons.rounded.Computer
import androidx.compose.material.icons.rounded.DesignServices
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Gavel
import androidx.compose.material.icons.rounded.HistoryEdu
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.Mosque
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.RocketLaunch
import androidx.compose.material.icons.rounded.Science
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.curriculum.CurriculumCatalog
import com.example.data.model.RecommendedQuiz
import com.example.data.model.SubjectCategory
import com.example.data.model.SubjectDomain
import com.example.data.model.SubjectItem
import com.example.data.model.SubjectMasteryInfo
import com.example.ui.theme.JetBrainsMonoFamily
import com.example.ui.theme.LocalAppStrings
import com.example.ui.theme.LocalMaarifColors

@Composable
fun CoursesScreen(
    selectedGrade: Int,
    selectedCategory: SubjectCategory,
    subjects: List<SubjectItem>,
    subjectMasteryMap: Map<String, SubjectMasteryInfo>,
    topRecommendation: RecommendedQuiz?,
    onSelectGrade: (Int) -> Unit,
    onSelectCategory: (SubjectCategory) -> Unit,
    onSubjectClick: (SubjectItem) -> Unit,
    onQuickRecommendationClick: (RecommendedQuiz) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalMaarifColors.current
    val strings = LocalAppStrings.current
    val allGradeSubjects = CurriculumCatalog.getSubjects(selectedGrade)

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isWide = maxWidth >= 600.dp
        val columns = if (isWide) 2 else 1

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Grade Selector Section (5 - 12. Sınıflar)
            item(key = "grade_selector") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(top = 14.dp, bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = strings.coursesSelectGradeTitle,
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "${allGradeSubjects.size} ${strings.coursesSubjectWord} • MEB Maarif",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(CurriculumCatalog.allGrades, key = { it }) { grade ->
                            val isSelected = grade == selectedGrade
                            val examSuffix = when (grade) {
                                8 -> " LGS"
                                12 -> " YKS"
                                else -> ""
                            }
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) colors.headerNavy else colors.subtleSurface,
                                border = BorderStroke(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) colors.headerCrimsonBorder else colors.cardBorder
                                ),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { onSelectGrade(grade) }
                                    .testTag("grade_chip_$grade")
                            ) {
                                Text(
                                    text = "$grade${strings.leaderboardGradeSuffix}$examSuffix",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    ),
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Category Filter Pills
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(SubjectCategory.entries, key = { it.name }) { category ->
                            val isSelected = category == selectedCategory
                            val catCount = allGradeSubjects.count { it.category == category }
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                                border = BorderStroke(
                                    width = 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else colors.cardBorder
                                ),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .clickable { onSelectCategory(category) }
                                    .testTag("category_chip_${category.name}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 13.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = strings.categoryName(category),
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                        )
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .background(
                                                if (isSelected) Color.White.copy(alpha = 0.22f) else colors.subtleSurface
                                            )
                                            .padding(horizontal = 6.dp, vertical = 1.dp)
                                    ) {
                                        Text(
                                            text = "$catCount",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontFamily = JetBrainsMonoFamily,
                                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Personalized Next-Step Spotlight Card
            if (topRecommendation != null) {
                item(key = "quick_rec_banner") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF0F172A), Color(0xFF1E293B))
                                )
                            )
                            .clickable { onQuickRecommendationClick(topRecommendation) }
                            .padding(16.dp)
                            .testTag("home_spotlight_recommendation")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Rounded.AutoAwesome,
                                        contentDescription = null,
                                        tint = colors.goldColor,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = "${strings.coursesRecommendedPrefix} • ${topRecommendation.reasonTag.uppercase()}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = colors.goldColor,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${topRecommendation.subject.grade}${strings.leaderboardGradeSuffix} ${topRecommendation.subject.name}",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    text = topRecommendation.reasonDetail,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFFCBD5E1)
                                    ),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                                    contentDescription = "Önerilen sınavı başlat",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Section Title
            item(key = "subjects_header") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$selectedGrade${strings.leaderboardGradeSuffix} • ${strings.categoryName(selectedCategory)}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "${subjects.size} ${strings.coursesSubjectWordLower}",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }

            // Adaptive Subject Cards (1 or 2 columns)
            val rows = subjects.chunked(columns)
            items(rows, key = { row -> row.joinToString("_") { it.id } }) { rowItems ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowItems.forEach { subject ->
                        SubjectCard(
                            subject = subject,
                            mastery = subjectMasteryMap[subject.id],
                            onClick = { onSubjectClick(subject) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (rowItems.size < columns) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun SubjectCard(
    subject: SubjectItem,
    mastery: SubjectMasteryInfo?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalMaarifColors.current
    val strings = LocalAppStrings.current
    val accent = subject.accentColor

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, colors.cardBorder),
        shadowElevation = 1.dp,
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag("subject_card_${subject.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(accent.copy(alpha = 0.13f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = domainIcon(subject.domain),
                    contentDescription = subject.name,
                    tint = accent,
                    modifier = Modifier.size(25.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = subject.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    subject.badgeTag?.let { badge ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(colors.goldBg)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = badge,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = colors.goldDark,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = subject.topics.take(2).joinToString(" • "),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "${subject.topics.size} ${strings.coursesUnitsWord}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = accent,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    if (mastery != null) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(colors.emeraldBg.copy(alpha = 0.7f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${strings.coursesAvgPrefix} ${mastery.averageAccuracy}%",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = JetBrainsMonoFamily,
                                    color = colors.emerald,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                contentDescription = "Sınavı oluştur",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

private fun domainIcon(domain: SubjectDomain): ImageVector = when (domain) {
    SubjectDomain.TURKISH, SubjectDomain.LITERATURE, SubjectDomain.FOREIGN_LIT -> Icons.Rounded.AutoStories
    SubjectDomain.MATH -> Icons.Rounded.Calculate
    SubjectDomain.SCIENCE, SubjectDomain.CHEMISTRY -> Icons.Rounded.Science
    SubjectDomain.PHYSICS -> Icons.Rounded.RocketLaunch
    SubjectDomain.BIOLOGY -> Icons.Rounded.Biotech
    SubjectDomain.HISTORY, SubjectDomain.REVOLUTION_HISTORY, SubjectDomain.SOCIAL_STUDIES -> Icons.Rounded.HistoryEdu
    SubjectDomain.GEOGRAPHY -> Icons.Rounded.Public
    SubjectDomain.PHILOSOPHY, SubjectDomain.PSYCHOLOGY, SubjectDomain.SOCIOLOGY, SubjectDomain.LOGIC -> Icons.Rounded.Psychology
    SubjectDomain.RELIGION_ETHICS, SubjectDomain.QURAN, SubjectDomain.PROPHET_LIFE, SubjectDomain.ISLAMIC_CULTURE -> Icons.Rounded.Mosque
    SubjectDomain.ENGLISH, SubjectDomain.GERMAN, SubjectDomain.ARABIC, SubjectDomain.TURKIC_LANGUAGES, SubjectDomain.OTTOMAN_TURKISH -> Icons.Rounded.Language
    SubjectDomain.COMPUTER_SCIENCE -> Icons.Rounded.Computer
    SubjectDomain.TECH_DESIGN -> Icons.Rounded.DesignServices
    SubjectDomain.ARTS_MUSIC -> Icons.Rounded.Palette
    SubjectDomain.PHYSICAL_ED -> Icons.Rounded.FitnessCenter
    SubjectDomain.MEDIA_LAW_DEMOCRACY -> Icons.Rounded.Gavel
    SubjectDomain.ECONOMY_ENTREPRENEURSHIP -> Icons.Rounded.TrendingUp
    SubjectDomain.ASTRONOMY -> Icons.Rounded.RocketLaunch
    else -> Icons.Rounded.MenuBook
}
