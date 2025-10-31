import java.io.BufferedReader;
import java.io.InputStreamReader;

public class GitVersion {
    private static String version = null;
    
    public static String getVersion() {
        if (version == null) {
            version = fetchGitVersion();
        }
        return version;
    }
    
    private static String fetchGitVersion() {
        try {
            ProcessBuilder pb = new ProcessBuilder("git", "describe", "--tags", "--always", "--dirty");
            pb.redirectErrorStream(true);
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();
            reader.close();
            process.waitFor();
            
            if (line != null && !line.trim().isEmpty()) {
                return line.trim();
            }
        } catch (Exception e) {
        }
        
        try {
            ProcessBuilder pb = new ProcessBuilder("git", "rev-parse", "--short", "HEAD");
            pb.redirectErrorStream(true);
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();
            reader.close();
            process.waitFor();
            
            if (line != null && !line.trim().isEmpty()) {
                return line.trim();
            }
        } catch (Exception e) {
        }
        
        return "dev-build";
    }
}

