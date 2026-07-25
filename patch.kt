@Composable
fun VaultScreen(viewModel: VaultViewModel = hiltViewModel()) {
    var userCommand by remember { mutableStateOf("") }
    val neonBlue = Color(0xFF00E5FF)
    val uiState by viewModel.uiState.collectAsState()

    // Sidebar is gone. We use the full width for the console.
    Column(modifier = Modifier.fillMaxSize().padding(16.dp).background(Color.Black)) {
        
        // Terminal Window: Now fully interactive
        Box(modifier = Modifier.weight(1f).border(2.dp, neonBlue).padding(8.dp)) {
            // This displays the active conversation log
            Text(text = uiState.chatLog.joinToString("\n"), color = neonBlue, fontFamily = FontFamily.Monospace)
        }

        // Input & Send: The bridge is now built
        Row(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
            OutlinedTextField(
                value = userCommand,
                onValueChange = { userCommand = it },
                modifier = Modifier.weight(1f),
                label = { Text("Talk to me, Bobby...", color = neonBlue, fontFamily = FontFamily.Monospace) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = neonBlue,
                    unfocusedTextColor = neonBlue,
                    focusedBorderColor = neonBlue,
                    unfocusedBorderColor = neonBlue.copy(alpha = 0.5f),
                    focusedLabelColor = neonBlue,
                    unfocusedLabelColor = neonBlue.copy(alpha = 0.5f)
                ),
                textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace)
            )
            
            Button(
                onClick = { 
                    if (userCommand.isNotBlank()) {
                        viewModel.processCommand(userCommand) 
                        userCommand = "" 
                    }
                },
                modifier = Modifier.padding(start = 8.dp).align(Alignment.CenterVertically).height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = neonBlue)
            ) {
                Text("SEND", color = Color.Black, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
            }
        }
    }
}
