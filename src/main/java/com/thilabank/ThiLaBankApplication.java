package com.thilabank;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ThiLaBankApplication {

    public static void main(String[] args) {

        String dbUrl = System.getenv("DB_URL");
        String dbUser = System.getenv("DB_USER");
        String dbPassword = System.getenv("DB_PASSWORD");
        String corsAllowedOrigin = System.getenv("CORS_ALLOWED_ORIGIN");


        if (dbUrl != null) System.setProperty("DB_URL", dbUrl);
        if (dbUser != null) System.setProperty("DB_USER", dbUser);
        if (dbPassword != null) System.setProperty("DB_PASSWORD", dbPassword);
        if (corsAllowedOrigin != null) System.setProperty("CORS_ALLOWED_ORIGIN", corsAllowedOrigin);

        if (dbUrl == null && dbUser == null && dbPassword == null) {
            Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
            System.setProperty("DB_URL", dotenv.get("DB_URL"));
            System.setProperty("DB_USER", dotenv.get("DB_USER"));
            System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
            System.setProperty("CORS_ALLOWED_ORIGIN", dotenv.get("CORS_ALLOWED_ORIGIN"));
        }

        SpringApplication.run(ThiLaBankApplication.class, args);
    }
}
