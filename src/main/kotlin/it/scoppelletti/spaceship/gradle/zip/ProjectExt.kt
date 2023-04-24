/*
 * Copyright (C) 2023 Dario Scoppelletti, <http://www.scoppelletti.it/>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.scoppelletti.spaceship.gradle.zip

import java.io.File
import java.io.IOException
import java.lang.Exception
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.io.path.inputStream
import org.gradle.api.Project
import org.gradle.api.tasks.WorkResult
import org.gradle.api.tasks.WorkResults
import org.gradle.tooling.BuildException

/**
 * Zips a file tree in a archive.
 *
 * @receiver         Project.
 * @param    archive Archive to create.
 * @param    folder  Root folder of the file tree.
 * @return           Result.
 * @since            1.1.0
 */
public fun Project.zip(archive: File, folder: File): WorkResult {
    val root = folder.toPath()
    var visitEx: Exception? = null

    try {
        ZipOutputStream(archive.outputStream()).use { zip ->
            Files.walkFileTree(root, object : SimpleFileVisitor<Path>() {

                override fun visitFile(
                    file: Path?,
                    attrs: BasicFileAttributes?
                ): FileVisitResult {
                    if (attrs?.isSymbolicLink == true) {
                        // Exlcude symbolic links
                        logger.warn("Exclude symbolic link $file.")
                        return FileVisitResult.CONTINUE
                    }

                    file?.inputStream()?.use { entry ->
                        val target = root.relativize(file)
                        zip.putNextEntry(ZipEntry(target.toString()))

                        val buf = ByteArray(DEFAULT_BUFFER_SIZE)
                        var n = entry.read(buf)
                        while (n > 0) {
                            zip.write(buf, 0, n)
                            n = entry.read(buf)
                        }

                        zip.closeEntry()
                        logger.debug("Zip file {}.", file)
                    }

                    return FileVisitResult.CONTINUE
                }

                override fun visitFileFailed(
                    file: Path?,
                    exc: IOException?
                ): FileVisitResult {
                    visitEx = exc
                    logger.error("Failed to zip $file.", exc)
                    return FileVisitResult.TERMINATE
                }
            })
        }
    } catch (ex: Exception) {
        logger.error("Failed to create $archive.", ex)
        throw BuildException("Failed to create $archive.", ex)
    }

    visitEx?.let {
        throw BuildException("Failed to create $archive.", it)
    }

    return WorkResults.didWork(true)
}