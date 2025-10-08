package mpi.aweirdwhale;

import static mpi.aweirdwhale.login.Login.signIn;

public class Main {
    public static void main(String[] args) throws Exception {
        // Fait pour etre utilisé dans la cli : java -jar smpi.java + args :
        /**
         * --start username password  -> lance le jeu
         * --uninstall                -> supprime le dossier du jeu
         **/

        if (args.length > 0) {
            switch (args[0]) {
                case "--start" -> {
                    if(args.length == 3) {
                        signIn(args[1], args[3]);
                    } else {
                        throw new Exception("Spécifier pseudo & mot de passe.");
                    }
                }

                case "--uninstall" -> {
                    return;
                }
            }

        } else {
            throw new Exception("Not enough arguments");
        }


    }
}