package com.jobquest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * JobQuest — Internship & Job Application Tracker
 * 
 * Main entry point for the Spring Boot application.
 * Bootstraps the embedded server, auto-configures JPA with SQLite,
 * and serves Thymeleaf templates + REST APIs.
 */
@SpringBootApplication
public class JobQuestApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobQuestApplication.class, args);
    }
}
