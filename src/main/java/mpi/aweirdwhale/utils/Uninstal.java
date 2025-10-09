package mpi.aweirdwhale.utils;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class Uninstal {
    public static void clean() {
        // supprime tout le dossier ~/.smp2ix/ ou %appdata%/.smp2ix/
        String osName = System.getProperty("os.name").toLowerCase();
        String userHome = System.getProperty("user.home");


        Path targetDir;
            if (osName.contains("win")) {
                String appData = System.getenv("APPDATA");
                if (appData != null) {
                    targetDir = Paths.get(appData, ".smp2ix");
                } else {
                    targetDir = Paths.get(userHome, ".smp2ix");
                }
            } else {
                targetDir = Paths.get(userHome, ".smp2ix");
            }

            // Vérifier si le dossier existe
            if (!Files.exists(targetDir)) {

                System.out.println("Aucun dossier à supprimer : " + targetDir);
                return;
            }

            // Supprimer récursivement le dossier
            try {
                Files.walkFileTree(targetDir, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                            throws IOException {
                        Files.deleteIfExists(file);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc)
                            throws IOException {
                        Files.deleteIfExists(dir);
                        return FileVisitResult.CONTINUE;
                    }
                });
                System.out.println("Dossier supprimé avec succès : " + targetDir);
            } catch (IOException e) {
                System.err.println("Erreur lors de la suppression du dossier : " + e.getMessage());
                e.printStackTrace();
            }

    }
}
