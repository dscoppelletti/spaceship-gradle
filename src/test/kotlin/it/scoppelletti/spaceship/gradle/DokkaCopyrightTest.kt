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
    fun testDefault(@TempDir tmpDir: File) {
        File(tmpDir, "build.gradle").run {
            writeText("""
                plugins {
                    id "it.scoppelletti.spaceship.java-library"
                }

                tasks.register("testCopyright") {
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
            .withArguments("testCopyright")
            .build()
        result.task(":testCopyright")!!.outcome.shouldBe(TaskOutcome.SUCCESS)

        result.output.run {
            shouldContain("http://www.scoppelletti.it")
            shouldContain("Dario Scoppelletti")
        }
    }

    @Test
    fun testExtension(@TempDir tmpDir: File) {
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
                
                tasks.register("testCopyright") {
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
            .withArguments("testCopyright")
            .build()
        result.task(":testCopyright")!!.outcome.shouldBe(TaskOutcome.SUCCESS)

        result.output.run {
            shouldContain("2020")
            shouldContain("http://www.contoso.com")
            shouldContain("Joe Doe")
        }
    }
}