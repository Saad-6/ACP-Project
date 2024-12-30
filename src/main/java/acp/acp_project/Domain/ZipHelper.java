package acp.acp_project.Domain;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipHelper {
    Response response = new Response();

    public Response Compress (List<File> files,Path outputZipFilePath){
        HashMap<String, File> fileMap = new HashMap<>();

        try (FileOutputStream fos = new FileOutputStream(outputZipFilePath.toFile());
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            // Iterate through the provided files
            for (File file : files) {
                boolean compressable = file.exists() && file.isFile() && !fileMap.containsKey(file.getName()) && !file.getName().endsWith(".zip");
                if (compressable) {

                    addFileToZip(file, zos);
                    fileMap.put(file.getName(), file);
                } else {
                    System.out.println("Skipping invalid file: " + file.getName());
                }
            }

            response.Message = files.size() + "Files compressed successfully into " + outputZipFilePath;
            response.success = true;

        } catch (IOException e) {
            response.Message = "Error during file compression: " + e.getMessage();
            response.success = false;
        }
        response.Message = files.size() + "Files Compressed";
        return response;
    }

    public Response Extract(List<File> files, Path outputFolder,String outputFolderPath ){

        for (File zipFile : files) {

            try (FileInputStream fis = new FileInputStream(zipFile);
                 ZipInputStream zis = new ZipInputStream(fis)) {

                ZipEntry entry;
                while ((entry = zis.getNextEntry()) != null) {
                    Path outputFilePath = outputFolder.resolve(entry.getName());

                    // Ensure parent directories exist for nested files
                    if (entry.isDirectory()) {
                        Files.createDirectories(outputFilePath);
                    } else {
                        Files.createDirectories(outputFilePath.getParent());
                        extractZipEntry(zis, outputFilePath);
                    }

                    zis.closeEntry();
                }

            } catch (IOException e) {
                response.Message = "Error extracting file " + zipFile.getName() + ": " + e.getMessage();
                response.success = false;
                return response;
            }
        }

        response.Message = "Files extracted successfully into " + outputFolderPath;
        response.success = true;
        return response;
    }



    public  void extractZipEntry(ZipInputStream zis, Path outputFilePath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(outputFilePath.toFile())) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = zis.read(buffer)) >= 0) {
                fos.write(buffer, 0, length);
            }
        }
    }
    public  void addFileToZip(File file, ZipOutputStream zos) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            ZipEntry zipEntry = new ZipEntry(file.getName());
            zos.putNextEntry(zipEntry);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) >= 0) {
                zos.write(buffer, 0, length);
            }

            zos.closeEntry();
        }
    }

}
