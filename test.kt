import com.google.ai.client.generativeai.type.FunctionDeclaration
import com.google.ai.client.generativeai.type.Schema
import com.google.ai.client.generativeai.type.Tool
import com.google.ai.client.generativeai.type.FunctionType

fun test() {
    val obdScannerTool = Tool(
        functionDeclarations = listOf(
            FunctionDeclaration(
                name = "scan_obd_codes",
                description = "Pulls diagnostic trouble codes from the vehicle's OBD2 port via Bluetooth",
                parameters = listOf(
                    Schema(
                        name = "module",
                        description = "The specific vehicle module to scan (e.g., PCM, BCM, ABS, or ALL)",
                        type = FunctionType.STRING
                    )
                ),
                requiredParameters = listOf("module")
            )
        )
    )
}
