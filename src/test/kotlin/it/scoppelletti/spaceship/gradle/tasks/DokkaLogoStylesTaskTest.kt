package it.scoppelletti.spaceship.gradle.tasks

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import java.io.File
import kotlin.test.Test
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.io.TempDir

private const val TEST_URL = "http://www.scoppelletti.it/images/logo.png"

class DokkaLogoStylesTaskTest {

    @Test
    fun testTask(@TempDir tmpDir: File) {
        val outFile = File(tmpDir, "styles.css")

        File(tmpDir, "build.gradle").run {
            writeText("""
                plugins {
                    id "it.scoppelletti.spaceship.java-library"
                }

                tasks.create("testTask",
                    ${DokkaLogoStylesTask::class.java.name}) {
                    logoUrl = "$TEST_URL"
                    outputFile = file("${outFile.path}")
                }
                """.trimIndent()
            )
        }

        val result = GradleRunner.create()
            .withProjectDir(tmpDir)
            .withPluginClasspath()
            .withArguments("testTask")
            .build()
        result.task(":testTask")!!.outcome.shouldBe(TaskOutcome.SUCCESS)

        outFile.readText().shouldContain(TEST_URL)
    }
}