package com.technohertz;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.core.io.ClassPathResource;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.technohertz.util.FileStorageProperties;

@EnableConfigurationProperties({FileStorageProperties.class})
@SpringBootApplication(scanBasePackages = { "com.technohertz" })
@EnableCaching
public class Application {
	//JsonNode mySchema = JsonLoader.fromFile(file);
	static String FB_BASE_URL="https://craziapp-3c02b.firebaseio.com";
	
	public static void main(String[] args) {
		
		/*
		 * ClassLoader classLoader = getClass().getClassLoader(); File file = new
		 * File(classLoader.getResource(
		 * "craziapp-firebase-adminsdk-yjg03-7e139e861a.json").getFile());
		 */
		SpringApplication.run(Application.class, args);
		
		/*
		 * FileInputStream serviceAccount = new FileInputStream(file);
		 * 
		 * FirebaseOptions options = new FirebaseOptions.Builder()
		 * .setCredentials(GoogleCredentials.fromStream(serviceAccount))
		 * .setDatabaseUrl("https://craziapp.firebaseio.com") .build();
		 * FirebaseApp.initializeApp(options);
		 */
		
		
		try {
			FirebaseOptions options = new FirebaseOptions.Builder()
					.setCredentials(GoogleCredentials
							.fromStream(new ClassPathResource("/craziapp-3c02b-firebase-adminsdk-rrs6o-3add9ace15.json").getInputStream()))
					.setDatabaseUrl(FB_BASE_URL).build();
			if (FirebaseApp.getApps().isEmpty()) {
				FirebaseApp.initializeApp(options);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}

	
	
}
