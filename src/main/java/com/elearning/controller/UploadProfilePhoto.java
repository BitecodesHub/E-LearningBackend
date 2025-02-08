package com.elearning.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/upload/profilephoto")
public class UploadProfilePhoto {

    private final Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
        "cloud_name", "dhzwaenxb",
        "api_key", "869376214487945",
        "api_secret", "U2rUyOpx7Fin-cRcWOmbgbnijCQ"
    ));

    @PostMapping
    public String uploadFile(@RequestParam("thumbnailUrl") MultipartFile file) {
        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            return uploadResult.get("secure_url").toString(); // Returns the image URL
        } catch (IOException e) {
            e.printStackTrace();
            return "File upload failed: " + e.getMessage();
        }
    }
}

//@RestController
//@RequestMapping("/upload/profilephoto")
//public class UploadProfilePhoto {
//
////    private static final String UPLOAD_DIR = "src/main/resources/static/profilephotos/";
//    private static final String UPLOAD_DIR = "target/classes/static/profilephotos/";
//
//    @PostConstruct
//    public void createUploadDir() {
//        File dir = new File(UPLOAD_DIR);
//        if (!dir.exists()) {
//            boolean isCreated = dir.mkdirs();
//            if (!isCreated) {
//                throw new RuntimeException("Failed to create upload directory: " + UPLOAD_DIR);
//            }
//        }
//    }
//
//    @PostMapping
//    public String uploadFile(@RequestParam("thumbnailUrl") MultipartFile file) {
//    	try {
//            // Ensure the upload directory exists
//            File dir = new File(UPLOAD_DIR);
//            if (!dir.exists()) {
//                boolean isCreated = dir.mkdirs(); // Create the directory if it doesn't exist
//                if (!isCreated) {
//                    throw new IOException("Failed to create directory: " + UPLOAD_DIR);
//                }
//            } 
//
//            // Get the file's original filename
//            String fileName = file.getOriginalFilename();
//            if (fileName == null || fileName.isEmpty()) {
//                return "Invalid file name";
//            }
//
//         // Generate a random string to prepend
//            String randomString = UUID.randomUUID().toString().substring(0, 8);
//            String newFileName = randomString + "_" + fileName;// Generate a random string to prepend
//            // Create a destination file in the uploads folder
//            Path destinationPath = Paths.get(UPLOAD_DIR, newFileName);
//
//            // Check if the file already exists
//            if (Files.exists(destinationPath)) {
//                return newFileName;
//            }
//  
//            // Access the temporary file location handled by Tomcat
//            File tempFile = File.createTempFile("upload-", file.getOriginalFilename());
//            file.transferTo(tempFile); // Transfer the uploaded file to the temporary file
//
//            // Move the file from the temporary location to the target directory
//            Files.move(tempFile.toPath(), destinationPath);
//
//            // Return a success message
//            return newFileName;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return "File upload failed: " + e.getMessage();
//        }
//    }
//}
