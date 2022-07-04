package it.scoppelletti.spaceship.gradle

import io.kotest.matchers.string.shouldContain
import it.scoppelletti.spaceship.gradle.model.LibraryExtension
import java.io.File
import kotlin.test.Test
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.io.TempDir

class ExtensionTest {

    @Test
    fun testDefault(@TempDir tmpDir: File) {
        File(tmpDir, "build.gradle").run {
            writeText("""
                plugins {
                    id "it.scoppelletti.spaceship.java-library"
                }

                tasks.register("testExtension") {
                    doLast {
                        println("${'$'}spaceship")
                    }
                }
                """.trimIndent()
            )

            val result = GradleRunner.create()
                .withProjectDir(tmpDir)
                .withPluginClasspath()
                .withArguments("testExtension")
                .build()

            result.output.run {
                shouldContain(LibraryExtension::class.java.name)
                shouldContain("url=<null>")
                shouldContain("scmUrl=<null>")
                shouldContain("inceptionYear=<null>")
                shouldContain("logoUrl=http://www.scoppelletti.it/spaceship/images/spaceship-logo/ic_launcher-web.png")
                shouldContain("developer=it.scoppelletti.spaceship.gradle.model.DeveloperModel")
                shouldContain("name=Dario Scoppelletti")
                shouldContain("email=dario@scoppelletti.it")
                shouldContain("url=http://www.scoppelletti.it")
                shouldContain("license=it.scoppelletti.spaceship.gradle.model.LicenseModel")
                shouldContain("name=The Apache License, Version 2.0")
                shouldContain("url=http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
    }

    @Test
    fun testExtension(@TempDir tmpDir: File) {
        File(tmpDir, "build.gradle").run {
            writeText("""
                plugins {
                    id "it.scoppelletti.spaceship.java-library"
                }

                spaceship {
                    url = "http://github.com/dscoppelletti/spaceship"
                    scmUrl = "git@github.com:dscoppelletti/spaceship.git"
                    inceptionYear = "2020"
                    developer {
                        name = "Joe Doe"
                        email = "joe.doe@mail.com"
                        url = "http://www.contoso.com"
                    }
                    license {
                        name = "MIT License"
                        url = "http://opensource.org/licenses/MIT"
                    }
                }

                tasks.register("testExtension") {
                    doLast {
                        println("${'$'}spaceship")
                    }
                }
                """.trimIndent()
            )

            val result = GradleRunner.create()
                .withProjectDir(tmpDir)
                .withPluginClasspath()
                .withArguments("testExtension")
                .build()

            result.output.run {
                shouldContain(LibraryExtension::class.java.name)
                shouldContain("url=http://github.com/dscoppelletti/spaceship")
                shouldContain("scmUrl=git@github.com:dscoppelletti/spaceship.git")
                shouldContain("inceptionYear=2020")
                shouldContain("logoUrl=http://www.scoppelletti.it/spaceship/images/spaceship-logo/ic_launcher-web.png")
                shouldContain("developer=it.scoppelletti.spaceship.gradle.model.DeveloperModel")
                shouldContain("name=Joe Doe")
                shouldContain("email=joe.doe@mail.com")
                shouldContain("url=http://www.contoso.com")
                shouldContain("license=it.scoppelletti.spaceship.gradle.model.LicenseModel")
                shouldContain("name=MIT License")
                shouldContain("url=http://opensource.org/licenses/MIT")
            }
        }
    }
}