import java.io.File;

/**
 * คลาสจัดการ path ของ resources ให้ทำงานได้ทั้งเมื่อรันจาก IDE และ command line
 */
public class PathResolver {
    private static String projectRoot = null;
    
    static {
        // หา project root directory
        String currentDir = System.getProperty("user.dir");
        File current = new File(currentDir);
        
        // ถ้ารันจาก src/ ให้ขึ้นไป parent
        if (current.getName().equals("src")) {
            projectRoot = current.getParent();
        } else {
            projectRoot = currentDir;
        }
        
        System.out.println("📁 Project Root: " + projectRoot);
    }
    
    /**
     * แปลง relative path เป็น absolute path
     * @param relativePath path เริ่มต้นจาก project root (เช่น "res/head/BlueBird/1.png")
     * @return absolute path
     */
    public static String resolve(String relativePath) {
        // ลบ "../" ถ้ามี
        if (relativePath.startsWith("../")) {
            relativePath = relativePath.substring(3);
        }
        
        File file = new File(projectRoot, relativePath);
        return file.getAbsolutePath();
    }
    
    /**
     * ตรวจสอบว่าไฟล์มีอยู่จริงหรือไม่
     */
    public static boolean exists(String relativePath) {
        File file = new File(resolve(relativePath));
        return file.exists();
    }
    
    /**
     * สร้าง File object จาก relative path
     */
    public static File getFile(String relativePath) {
        return new File(resolve(relativePath));
    }
}

