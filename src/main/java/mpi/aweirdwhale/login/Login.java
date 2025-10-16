package mpi.aweirdwhale.login;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


import static mpi.aweirdwhale.LaunchGame.getDir;
import static mpi.aweirdwhale.utils.Specs.PORT;
import static mpi.aweirdwhale.utils.Specs.SERVER_URL;

public class Login {

    public static HttpURLConnection getHttpURLConnection(String address, String request, String httpMethode) throws IOException, URISyntaxException {

        // Handle URIs
        URI uri = new URI(address);
        URL url = uri.toURL();


        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Configure the request
        /* y'a pas une méthode httpsMethode ? */
        connection.setRequestMethod(httpMethode);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        // Maximum connection delay in ms
        int DELAY = 10000;
        connection.setConnectTimeout(DELAY);
        connection.setReadTimeout(DELAY);

        // Send the JSON
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = request.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        return connection;
    }



    private static String digest(String password) {
        // Hash the password using SHA-256
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return password; /* Tu retournes le mdp en clair si pas d'algo sha256 ??? */

    }

    public static void signIn(String username, String password) {
        // digest password :
        /*
            hasher le mdp avant de l'envoyer n'a aucun intérêt (ça n'améliore pas du tout la sécurité), tu dois le hasher sur le serveur
            en revanche tu dois le chiffrer, au moins en AES, si possible en RSA avec une clé sur le serveur (aller voir la lib openssl pour chiffrer)
            A la rigueur, tu peux peut-être ajouter au hash un code généré pour le rendre unique si t'as la flm, en mode JCS de wish
        */
        String pwd = digest(password);

        String url = SERVER_URL + PORT;

        String request_body = "{\"user\":\"" + username + "\",\"mdp\":\"" + pwd + "\"}";

        try {

            String TARGET = "/login";
            String METHOD = "POST";

            HttpURLConnection connection = getHttpURLConnection(url + TARGET, request_body, METHOD);

            int code = connection.getResponseCode();

            if (code == 200){
                getDir(username); // launch mc
            } else {
                /* Pourquoi t'as mis un caractère spécial sur le x ? */
                System.out.println("⤫ Erreur lors de la connexion (probablement mauvais mdp)");
            }


        } catch (IOException | URISyntaxException _) {
            System.out.println("⤫ Erreur de connexion (pas de wifi ou serv éteint demande @Aweirdwhale en cas de doute)");
        }


    }

}
