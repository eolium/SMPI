package mpi.aweirdwhale;

import mpi.aweirdwhale.installer.SetUp;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static mpi.aweirdwhale.installer.SetUp.downloadFiles;

public class LaunchGame {
    public static void getDir(String username) {
        /**
         * On suppose que le dossier du jeu est bien mit en place
         */

        String dir = SetUp.createGameDirectory(); // mettre à jour plus tard

        String classpathpath;
        String os = System.getProperty("os.name").toLowerCase();


        if (os.contains("win")) {
            classpathpath = dir+"/win_classpath.txt";
        } else {
            classpathpath = dir+"/unix_classpath.txt";
        }


        launchMinecraft("4", "2", classpathpath, username, dir, dir + "/uuid.txt");


    }


    public static void launchMinecraft(String maxRam, String minRam, String ClassPaths, String username, String gameDir, String uuid_path) {
        try {



            List<String> command = new ArrayList<>();
            command.add("java");
            command.add("-Xmx"+maxRam+"G");
            command.add("-Xms"+minRam+"G");
            command.add("-Djava.net.preferIPv6Addresses=system");
            command.add("-cp");


            String classpathContent = new String(Files.readAllBytes(Paths.get(ClassPaths)));
            classpathContent = gameDir + "/versions/1.20.4-forge-49.2.0/1.20.4-forge-49.2.0.jar:" + classpathContent.trim();

            String separator = System.getProperty("os.name").toLowerCase().contains("win") ? ";" : ":";

            String[] paths = classpathContent.split(":");
            StringBuilder newClasspath = new StringBuilder();

            for (int i = 0; i < paths.length; i++) {
                String absPath = new File(gameDir, paths[i]).getAbsolutePath();
                newClasspath.append(absPath);
                if (i < paths.length - 1) {
                    newClasspath.append(separator);
                }
            }

            classpathContent = newClasspath.toString();

            String uuid = new String(Files.readAllBytes((Paths.get(uuid_path))));

            command.add(classpathContent);

            command.add("net.minecraftforge.bootstrap.ForgeBootstrap");

            command.add("--launchTarget");
            command.add("forge_client");

            command.add("--version");
            command.add("1.20.4-forge-49.2.0");

            command.add("--accessToken");
            command.add("000");

            command.add("--username");
            command.add(username);

            command.add("--gameDir");
            command.add(gameDir);
            command.add("--assetsDir");
            command.add(gameDir+"/assets");
            command.add("--assetIndex");
            command.add("26");

            command.add("--uuid");
            command.add(uuid.trim());

            command.add("--userType");
            command.add("msa");

            command.add("--versionType");
            command.add("Forge");
            command.add("--verbose");


            ProcessBuilder builder = new ProcessBuilder(command);
            builder.inheritIO();
            Process process = builder.start();
            process.waitFor();
        } catch (InterruptedException | IOException _) {
            downloadFiles();
            launchMinecraft(maxRam, minRam, ClassPaths, username, gameDir, uuid_path); // idée de merde je le dis haut et fort
            throw new RuntimeException("x Erreur lors du lancement du jeu.");
        }
    }



}
