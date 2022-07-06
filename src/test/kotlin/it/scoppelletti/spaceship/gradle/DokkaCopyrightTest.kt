package it.scoppelletti.spaceship.gradle

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import java.io.File
import kotlin.test.Test
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.io.TempDir

class DokkaCopyrightTest {

    @Test
    fun testTask(@TempDir tmpDir: File) {
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
                
                tasks.register("testTask") {
                    doLast {
                        def tools = ${DokkaTools::class.java.name}.create(project)
                        println(tools.buildCopyright())
                    }
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

        result.output.run {
            shouldContain("2020")
            shouldContain("http://www.contoso.com")
            shouldContain("Joe Doe")
        }
    }
}