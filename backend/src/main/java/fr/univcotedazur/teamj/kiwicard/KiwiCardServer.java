package fr.univcotedazur.teamj.kiwicard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class KiwiCardServer {

    public static void main(String[] args) {
        SpringApplication.run(KiwiCardServer.class, args);
    }

}
