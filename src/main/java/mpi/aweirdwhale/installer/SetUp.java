package mpi.aweirdwhale.installer;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static mpi.aweirdwhale.utils.Specs.GAMEFILES;

public class SetUp {

    public static void downloadFiles() {
        // download le zip à l'url GAMEFILES
        // le dezip dans ~/.smp2ix ou %appdata%/.smp2ix

        try {
            // Déterminer le dossier cible (~/.smp2ix ou %AppData%/.smp2ix)
            String userHome = System.getProperty("user.home");
            String os = System.getProperty("os.name").toLowerCase();

            Path targetDir;
            if (os.contains("win")) {
                String appData = System.getenv("APPDATA");
                targetDir = (appData != null)
                        ? Paths.get(appData, ".smp2ix")
                        : Paths.get(userHome, ".smp2ix");
            } else {
                targetDir = Paths.get(userHome, ".smp2ix");
            }

            // Créer le dossier s’il n’existe pas
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
            }

            // Fichier temporaire ZIP
            Path tempZip = Files.createTempFile("smp2ix_", ".zip");

            System.out.println("Téléchargement du jeu depuis : " + GAMEFILES);

            // Télécharger le fichier ZIP
            try (InputStream in = new URL(GAMEFILES).openStream()) {
                Files.copy(in, tempZip, StandardCopyOption.REPLACE_EXISTING);
            }

            /* Si pas de HTTPS, ça pourrait peut-être être une bonne idée de faire un hash du zip pour éviter un man in the middle */

            System.out.println("Fichier téléchargé : " + tempZip);

            // Décompresser le ZIP dans le dossier cible
            unzip(tempZip, targetDir);

            System.out.println("Décompression terminée dans : " + targetDir);

            // Supprimer le fichier temporaire
            Files.deleteIfExists(tempZip);

        } catch (Exception e) {
            System.err.println("Erreur lors du téléchargement : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void unzip(Path zipFilePath, Path destDir) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFilePath.toFile()))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                Path newPath = destDir.resolve(entry.getName()).normalize();

                // Empêche les attaques Zip Slip
                if (!newPath.startsWith(destDir)) {
                    throw new IOException("Fichier ZIP corrompu : tentative d'évasion de répertoire (" + entry.getName() + ")");
                }

                if (entry.isDirectory()) {
                    Files.createDirectories(newPath);
                } else {
                    // Créer les dossiers nécessaires
                    Files.createDirectories(newPath.getParent());
                    // Copier le contenu du fichier
                    try (OutputStream out = Files.newOutputStream(newPath)) {
                        zis.transferTo(out);
                    }
                }
                zis.closeEntry();
            }
        }
    }


    public static String getGameDirectory() {
        String path = createGameDirectory();

        // crée les sous dossiers
        createSubDirectories(path, "mods");
        createSubDirectories(path, "assets");
        createSubDirectories(path, "libraries");
        createSubDirectories(path, "versions");
        createSubDirectories(path+"/versions", "1.21.4");
        createSubDirectories(path+"/versions", "fabric-loader");

        return path;
    }

    public static String createGameDirectory() { /* mal nommée, il faudrait échanger de nom getGameDirectory et createGameDirectory (oui je sais) */
        String home = System.getProperty("user.home"); // root
        String os = System.getProperty("os.name").toLowerCase(); // diff unix / windows

        String path; // gameDir

        if (os.contains("win")) {
            path = System.getenv("APPDATA") + "/.smp2ix";
        } else {
            path = home + "/.smp2ix";
        }

        return path;
    }

    public static void createSubDirectories(String parent, String child) {
        File dir = new File(parent + "/" + child);

        if (!dir.exists()) { // Si ça existe on s'en fiche
            dir.mkdirs();
            System.out.println(dir.getAbsolutePath());
        }
    }
}
