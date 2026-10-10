package com.ynotlabs.cathopedia.speech

/**
 * The key a recorded part is stored under: 64-bit FNV-1a over the UTF-8 of
 * "<language>\n<text>", as 16 hex digits. tools/audio/generate_prayer_audio.py
 * computes the same key, so a part whose text changed after the audio was made
 * simply has no recording and falls back to the device voice.
 */
fun speechKey(language: String, text: String): String {
    var hash = 0xcbf29ce484222325UL
    for (byte in "$language\n$text".encodeToByteArray()) {
        hash = hash xor (byte.toUByte().toULong())
        hash *= 0x100000001b3UL
    }
    return hash.toString(16).padStart(16, '0')
}
