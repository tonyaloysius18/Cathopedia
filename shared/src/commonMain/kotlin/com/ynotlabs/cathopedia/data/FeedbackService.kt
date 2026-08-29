package com.ynotlabs.cathopedia.data

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * Feedback delivery endpoint. Paste the deployment URL of your web endpoint
 * (e.g. a Google Apps Script Web App) here. Until it is set, [FeedbackService.send]
 * fails fast with a clear "endpoint not configured" error rather than posting nowhere.
 *
 * The endpoint receives [FeedbackPayload] as JSON and must reply `{"ok": true}`
 * (or `{"ok": false, "error": "..."}`). Google Apps Script deployments answer with a
 * 302 redirect to a script.googleusercontent.com URL, which this client follows.
 */
private const val FEEDBACK_ENDPOINT =
    "https://script.google.com/macros/s/AKfycbzYsrUy-CFHfE7PhrtDgbqA75z1qyyKMtsPr9UL0E1F60UHCZMk1kIze_cVtOGm5h4O/exec"

private const val FEEDBACK_APP_ID = "cathopedia"

enum class FeedbackCategory(val wireValue: String) {
    PROBLEM("problem"),
    SUGGESTION("suggestion"),
    GENERAL("general"),
}

data class FeedbackAttachment(
    val bytes: ByteArray,
    val fileName: String,
    val mimeType: String = "image/jpeg",
)

data class FeedbackSubmission(
    val category: FeedbackCategory,
    val message: String,
    val includeAppDetails: Boolean,
    val appVersion: String,
    val platform: String,
    val attachments: List<FeedbackAttachment>,
)

@Serializable
internal data class FeedbackPayload(
    val app: String,
    val category: String,
    val message: String,
    val includeAppDetails: Boolean,
    val appVersion: String,
    val platform: String,
    val attachments: List<FeedbackAttachmentPayload>,
)

@Serializable
internal data class FeedbackAttachmentPayload(
    val fileName: String,
    val mimeType: String,
    val contentBase64: String,
)

@Serializable
private data class FeedbackResponse(
    val ok: Boolean = false,
    val error: String? = null,
)

/**
 * Sends feedback to the configured web endpoint (see [FEEDBACK_ENDPOINT]).
 * Unlike Itinera's service this carries no auth token — the endpoint should
 * distinguish apps by the [FeedbackPayload.app] field ("cathopedia").
 */
class FeedbackService(
    private val client: HttpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    },
) {
    suspend fun send(submission: FeedbackSubmission) {
        check(!FEEDBACK_ENDPOINT.contains("REPLACE_WITH")) {
            "The feedback endpoint has not been configured."
        }

        val initialResponse = client.post(FEEDBACK_ENDPOINT) {
            contentType(ContentType.Application.Json)
            setBody(submission.toPayload())
        }

        // Google Apps Script Web Apps answer a POST with a 302 to a
        // script.googleusercontent.com URL that carries the real body.
        val redirectLocation = initialResponse.headers[HttpHeaders.Location]
        val finalResponse = if (initialResponse.status.value in 300..399 && redirectLocation != null) {
            client.get(redirectLocation)
        } else {
            initialResponse
        }

        val responseText = finalResponse.bodyAsText()
        if (!finalResponse.status.isSuccess()) {
            error("Feedback service returned HTTP ${finalResponse.status.value}: $responseText")
        }

        val result = try {
            Json { ignoreUnknownKeys = true }.decodeFromString<FeedbackResponse>(responseText)
        } catch (e: Exception) {
            throw IllegalStateException("The feedback server returned an invalid response.", e)
        }

        if (!result.ok) {
            error(result.error ?: "Feedback delivery failed.")
        }
    }
}

@OptIn(ExperimentalEncodingApi::class)
internal fun FeedbackSubmission.toPayload(): FeedbackPayload = FeedbackPayload(
    app = FEEDBACK_APP_ID,
    category = category.wireValue,
    message = message.trim(),
    includeAppDetails = includeAppDetails,
    appVersion = appVersion,
    platform = platform,
    attachments = attachments.mapIndexed { index, attachment ->
        FeedbackAttachmentPayload(
            fileName = attachment.fileName.ifBlank { "cathopedia-feedback-${index + 1}.jpg" },
            mimeType = attachment.mimeType,
            contentBase64 = Base64.Default.encode(attachment.bytes),
        )
    },
)
