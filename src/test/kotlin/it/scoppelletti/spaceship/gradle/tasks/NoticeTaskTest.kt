package it.scoppelletti.spaceship.gradle.tasks

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import java.io.File
import kotlin.test.Test
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.io.TempDir

class NoticeTaskTest {

    @Test
    fun testTask(@TempDir tmpDir: File) {
        val outFile = File(tmpDir, NoticeTask.NOTICE_NAME)

        File(tmpDir, "build.gradle").run {
            writeText("""
                plugins {
                    id "it.scoppelletti.spaceship.java-library"
                }

                description 'Spaceship Gradle Plugins Test'
                
                spaceship {
                    inceptionYear = "2020"
                    developer {
                        name = "Joe Doe"
                        email = "joe.doe@mail.com"
                        url = "http://www.contoso.com"
                    }
                }
                
                tasks.create("testTask", ${NoticeTask::class.java.name}) {
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

        outFile.readText().run {
            shouldContain("Spaceship Gradle Plugins Test")
            shouldContain("2020")
            shouldContain("Joe Doe")
            shouldContain("http://www.contoso.com")
        }
    }
}