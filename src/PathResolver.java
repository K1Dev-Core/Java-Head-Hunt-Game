import java.io.File;

public class PathResolver {
    private static String projectRoot = null;

    static {
        String currentDir = System.getProperty("user.dir");
        File current = new File(currentDir);
        if (current.getName().equals("src")) {
            projectRoot = current.getParent();
        } else {
            projectRoot = currentDir;
        }
    }

    public static String resolve(String relativePath) {
        if (relativePath.startsWith("../")) {
            relativePath = relativePath.substring(3);
        }
        File file = new File(projectRoot, relativePath);
        return file.getAbsolutePath();
    }

    public static boolean exists(String relativePath) {
        File file = new File(resolve(relativePath));
        return file.exists();
    }

    public static File getFile(String relativePath) {
        return new File(resolve(relativePath));
    }
}
