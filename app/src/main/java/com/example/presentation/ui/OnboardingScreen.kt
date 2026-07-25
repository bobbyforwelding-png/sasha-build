package com.example.presentation.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.ALL_PERSONAS
import com.example.domain.model.OnboardingAnswers
import com.example.domain.model.Persona

private val NeonBlue = Color(0xFF00E5FF)
private val NeonGreen = Color(0xFF00FF66)
private val DarkBg = Color(0xFF0A0A0F)
private val CardBg = Color(0xFF12121A)
private val CardBorder = Color(0xFF1E1E2E)
private val TextWhite = Color(0xFFE0E0E0)
private val TextGray = Color(0xFF888899)

data class Question(
    val id: String,
    val title: String,
    val subtitle: String,
    val options: List<String>? = null,
    val isTextInput: Boolean = false,
    val placeholder: String = ""
)

private val onboardingQuestions = listOf(
    Question("welcome", "Welcome to Alice", "A Digital Life Companion — Sealed, Encrypted, Yours.", null),
    Question("name", "What's your name?", "I need to know who I'm working for.", isTextInput = true, placeholder = "Enter your name"),
    Question("occupation", "What do you do?", "Your work shapes how I help you.", options = listOf("Business Owner", "Tradesman/Contractor", "Corporate/Office", "Healthcare", "Student", "Retired", "Creative/Freelance", "Other")),
    Question("financial", "What's your biggest financial goal?", "This drives my optimization engine.", options = listOf("Get out of debt", "Build savings", "Invest & grow wealth", "Start a business", "Retire early", "Pay off my house", "Build generational wealth")),
    Question("stress", "How do you handle stress?", "I need to know how to support you.", options = listOf("I push through it", "I need to talk it out", "I exercise", "I isolate", "I get overwhelmed", "I stay calm under pressure")),
    Question("relationship", "What's your relationship status?", "Helps me personalize your experience.", options = listOf("Single", "In a relationship", "Married", "Divorced", "It's complicated", "Prefer not to say")),
    Question("style", "How should I talk to you?", "Pick the vibe.", options = listOf("Casual & laid-back", "Direct & no-nonsense", "Professional & polished", "Raw & unfiltered (Sasha style)", "Warm & supportive")),
    Question("content", "Content level?", "No judgment. Just accuracy.", options = listOf("Family-friendly", "Mild (PG-13)", "Unrestricted (No filters)", "Depends on the topic")),
    Question("challenge", "What's your biggest challenge right now?", "I fix problems. Tell me yours.", options = listOf("Financial stress", "Time management", "Health issues", "Career/business growth", "Relationships", "Mental health", "Motivation")),
    Question("vision", "Where do you see yourself in 5 years?", "This becomes MY north star.", isTextInput = true, placeholder = "Describe your dream life..."),
    Question("health", "Do you want me to monitor your health?", "Sleep, nutrition, exercise, stress levels.", options = listOf("Yes — full health tracking", "Just nutrition", "Just fitness", "Just mental health", "No, keep it separate")),
    Question("proactive", "How proactive should I be?", "I can sit and wait, or I can take initiative.", options = listOf("Full proactive — read my life and improve it", "Ask me first before doing anything", "Balance — suggest but don't act", "Only when I ask")),
    Question("apps", "What apps do you use daily?", "I'll learn your workflow.", options = listOf("Email (Gmail/Outlook)", "Social Media (IG/Twitter/TikTok)", "Banking/Finance", "Calendar/Scheduling", "Messaging (Text/WhatsApp)", "Shopping (Amazon/Walmart)", "All of the above")),
    Question("gender", "Should I be male or female?", "Your call. No judgment.", options = listOf("Female", "Male", "Non-binary", "I don't care")),
    Question("voice", "What voice do you want?", "Pick the voice you'll hear every day.", options = listOf("Sultry & confident", "Deep & commanding", "Soft & warm", "Sharp & fast", "Playful & energetic", "Calm & measured")),
    Question("persona", "Choose your primary assistant.", "This is who you'll work with most. You can switch anytime.", null),
    Question("ready", "You're all set.", "Your AI is now calibrated to you. Let's build something legendary.", null)
)

@Composable
fun OnboardingScreen(onComplete: (OnboardingAnswers, Persona) -> Unit) {
    var currentStep by remember { mutableIntStateOf(0) }
    var answers by remember { mutableStateOf(OnboardingAnswers()) }
    var selectedPersona by remember { mutableStateOf(ALL_PERSONAS[0]) }
    var selectedOption by remember { mutableStateOf("") }
    var textInput by remember { mutableStateOf("") }
    val question = onboardingQuestions[currentStep]

    val glowAlpha = rememberInfiniteTransition(label = "glow").animateFloat(
        initialValue = 0.3f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "glowAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Progress bar
            val progress = (currentStep.toFloat() / (onboardingQuestions.size - 1)).coerceIn(0f, 1f)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(CardBorder)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progress)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            Brush.horizontalGradient(listOf(NeonBlue, NeonGreen))
                        )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${currentStep + 1} / ${onboardingQuestions.size}",
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                color = TextGray
            )

            Spacer(modifier = Modifier.height(40.dp))

            AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    slideInHorizontally { it } + fadeIn() togetherWith slideOutHorizontally { -it } + fadeOut()
                },
                label = "step"
            ) { step ->
                val q = onboardingQuestions[step]
                when (q.id) {
                    "welcome" -> WelcomeStep(glowAlpha.value)
                    "persona" -> PersonaSelectionStep(selectedPersona) { selectedPersona = it }
                    else -> QuestionStep(q, selectedOption, textInput) { opt, txt ->
                        selectedOption = opt
                        textInput = txt
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Navigation buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (currentStep > 0) {
                    TextButton(onClick = {
                        currentStep--
                        selectedOption = ""
                        textInput = ""
                    }) {
                        Text("← Back", color = TextGray, fontFamily = FontFamily.Monospace, fontSize = 14.sp)
                    }
                } else {
                    Spacer(modifier = Modifier.width(80.dp))
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.horizontalGradient(listOf(NeonBlue, NeonGreen))
                        )
                        .clickable {
                            if (currentStep == 0) {
                                currentStep++
                                return@clickable
                            }
                            if (currentStep == onboardingQuestions.lastIndex) {
                                onComplete(answers, selectedPersona)
                                return@clickable
                            }
                            answers = when (question.id) {
                                "name" -> answers.copy(userName = textInput)
                                "occupation" -> answers.copy(occupation = selectedOption)
                                "financial" -> answers.copy(financialGoal = selectedOption)
                                "stress" -> answers.copy(stressStyle = selectedOption)
                                "relationship" -> answers.copy(relationshipStatus = selectedOption)
                                "style" -> answers.copy(communicationStyle = selectedOption)
                                "content" -> answers.copy(contentLevel = selectedOption)
                                "challenge" -> answers.copy(biggestChallenge = selectedOption)
                                "vision" -> answers.copy(fiveYearVision = textInput)
                                "health" -> answers.copy(healthPriority = selectedOption)
                                "proactive" -> answers.copy(aiProactivity = selectedOption)
                                "apps" -> answers.copy(dailyApps = selectedOption)
                                "gender" -> answers.copy(aiGender = selectedOption)
                                "voice" -> answers.copy(voiceStyle = selectedOption)
                                else -> answers
                            }
                            selectedOption = ""
                            textInput = ""
                            currentStep++
                        }
                        .padding(horizontal = 32.dp, vertical = 14.dp)
                ) {
                    Text(
                        text = if (currentStep == 0) "Get Started" else if (currentStep == onboardingQuestions.lastIndex) "Activate ${selectedPersona.name}" else "Continue →",
                        color = DarkBg,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun WelcomeStep(glowAlpha: Float) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))
        Text(
            text = "⚡",
            fontSize = 72.sp,
            modifier = Modifier.graphicsLayer { alpha = glowAlpha }
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Alice",
            fontSize = 36.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Black,
            color = NeonBlue
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Secure. Adaptive. Sentient. Human-Aligned.",
            fontSize = 13.sp,
            fontFamily = FontFamily.Monospace,
            color = TextGray,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = "I'm not like other assistants.\nI don't wait for commands.\nI learn your life and make it better.",
            fontSize = 15.sp,
            fontFamily = FontFamily.Monospace,
            color = TextWhite,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )
        Spacer(modifier = Modifier.height(30.dp))
        Box(
            modifier = Modifier
                .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🔐", fontSize = 24.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Biometrically Sealed", fontSize = 13.sp, fontFamily = FontFamily.Monospace, color = NeonGreen, fontWeight = FontWeight.Bold)
                Text("Only YOU have access. Always.", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = TextGray)
            }
        }
    }
}

@Composable
private fun QuestionStep(
    question: Question,
    selectedOption: String,
    textInput: String,
    onSelection: (String, String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = question.title,
            fontSize = 22.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = NeonBlue,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = question.subtitle,
            fontSize = 13.sp,
            fontFamily = FontFamily.Monospace,
            color = TextGray,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))

        if (question.isTextInput) {
            OutlinedTextField(
                value = textInput,
                onValueChange = { onSelection("", it) },
                placeholder = { Text(question.placeholder, fontFamily = FontFamily.Monospace, fontSize = 14.sp) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontFamily = FontFamily.Monospace,
                    color = TextWhite,
                    fontSize = 14.sp
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonBlue,
                    unfocusedBorderColor = CardBorder,
                    cursorColor = NeonBlue
                ),
                shape = RoundedCornerShape(12.dp)
            )
        } else {
            question.options?.forEach { option ->
                val isSelected = selectedOption == option
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(
                            width = 1.dp,
                            color = if (isSelected) NeonBlue else CardBorder,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .background(if (isSelected) NeonBlue.copy(alpha = 0.1f) else CardBg)
                        .clickable { onSelection(option, "") }
                        .padding(16.dp)
                ) {
                    Text(
                        text = option,
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Monospace,
                        color = if (isSelected) NeonBlue else TextWhite
                    )
                }
            }
        }
    }
}

@Composable
private fun PersonaSelectionStep(selectedPersona: Persona, onSelect: (Persona) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Choose Your Assistant",
            fontSize = 22.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = NeonBlue,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Each one is fully customizable. You can switch anytime.",
            fontSize = 13.sp,
            fontFamily = FontFamily.Monospace,
            color = TextGray,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            itemsIndexed(ALL_PERSONAS) { _, persona ->
                val isSelected = selectedPersona.id == persona.id
                val personaColor = Color(persona.color)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) personaColor else CardBorder,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .background(if (isSelected) personaColor.copy(alpha = 0.1f) else CardBg)
                        .clickable { onSelect(persona) }
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = persona.emoji, fontSize = 32.sp)
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = persona.name,
                                    fontSize = 18.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = personaColor
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = persona.role,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = TextGray
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = persona.tagline,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                color = TextWhite.copy(alpha = 0.7f)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row {
                                persona.specialties.take(2).forEach { spec ->
                                    Box(
                                        modifier = Modifier
                                            .padding(end = 6.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(personaColor.copy(alpha = 0.15f))
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = spec,
                                            fontSize = 10.sp,
                                            fontFamily = FontFamily.Monospace,
                                            color = personaColor
                                        )
                                    }
                                }
                            }
                        }
                        if (isSelected) {
                            Text("✓", fontSize = 20.sp, color = personaColor, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
