package com.milbar

import com.google.gson.GsonBuilder
import com.milbar.model.CipherConfig
import java.io.File
import java.io.FileNotFoundException
import java.io.FileReader
import java.io.PrintWriter

object ConfigManager {
    private val gson = GsonBuilder().setPrettyPrinting().create()
    @JvmStatic
    fun saveConfig(file: File, config: CipherConfig) = try {
        val nameAndExtension = file.name.split(".")
        val outputFile : File
        outputFile = if (nameAndExtension.size == 1)
            File(file.parent, file.name + ".json")
        else
            File(file.parent, nameAndExtension[0] + ".json")

        PrintWriter(outputFile).use { writer ->
            writer.write(gson.toJson(config))
        }
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    }

    @JvmStatic
    fun loadConfig(inputFile: File): CipherConfig = FileReader(inputFile).use { reader ->
        return gson.fromJson(reader.readText(), CipherConfig::class.java)
    }

}