package it.scoppelletti.spaceship.gradle.model

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import io.kotest.matchers.shouldBe
import kotlin.test.Test
import org.junit.jupiter.api.BeforeEach

class DokkaConfigModelTest {

    private lateinit var adapter: JsonAdapter<DokkaConfigModel>

    @BeforeEach
    fun setUp() {
        val moshi = Moshi.Builder().build()
        adapter = moshi.adapter(DokkaConfigModel::class.java)
    }

    @Test
    fun testFull() {
        val model = DokkaConfigModel(
            footerMessage = "Copyright(C) 2002",
            customStyleSheets = listOf("styles.css")
        )

        val json = adapter.toJson(model)
        json.shouldBe("""
            {"footerMessage":"Copyright(C) 2002","customStyleSheets":["styles.css"]}
            """.trimIndent())
    }

    @Test
    fun testFooterMessage() {
        val model = DokkaConfigModel(
            footerMessage = "Copyright(C) 2002")

        adapter.toJson(model).shouldBe("""
            {"footerMessage":"Copyright(C) 2002"}
            """.trimIndent())
    }

    @Test
    fun testFooterCustomStyleSheets() {
        val model = DokkaConfigModel(
            customStyleSheets = listOf("styles.css")
        )

        adapter.toJson(model).shouldBe("""
            {"customStyleSheets":["styles.css"]}
            """.trimIndent())
    }

    @Test
    fun testEmpty() {
        val model = DokkaConfigModel()
        adapter.toJson(model).shouldBe("{}")
    }
}