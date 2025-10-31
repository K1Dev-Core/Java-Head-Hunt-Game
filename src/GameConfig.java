import java.awt.Color;

public class GameConfig {
    // ============================================
    // การตั้งค่าหน้าต่าง (Window Settings)
    // ============================================
  
    public static final int WINDOW_WIDTH = 1600;              // ความกว้างของหน้าต่าง
    public static final int WINDOW_HEIGHT = 900;             // ความสูงของหน้าต่าง
    public static final String GAME_TITLE = "Monster Pop Arena"; // ชื่อเกม
    
    // ============================================
    // การตั้งค่าเครือข่าย (Network Settings)
    // ============================================
    public static final String SERVER_IP = "127.0.0.1";      // IP ของเซิร์ฟเวอร์
    public static final int SERVER_PORT = 8888;              // พอร์ตของเซิร์ฟเวอร์
    
    // ============================================
    // การตั้งค่าเกม (Game Settings)
    // ============================================
    public static final int GAME_DURATION = 120;             // ระยะเวลาเกม (วินาที)
    public static final int GAME_RESET_DELAY = 10000;         // ดีเลย์ก่อนรีเซ็ตเกม (มิลลิวินาที)
    public static final int MAX_HEADS = 10;                  // จำนวนหัวสูงสุดในเกม
    public static final int SCORE_PER_HIT = 10;              // คะแนนต่อการกดหัว
    public static final int MAX_PLAYERS = 4;                 // จำนวนผู้เล่นสูงสุด
    public static final int MIN_PLAYERS = 2;                 // จำนวนผู้เล่นขั้นต่ำที่เกมจะเริ่ม
    public static final int GAME_START_DELAY = 15000;         // ดีเลย์ก่อนเริ่มเกม (มิลลิวินาที)
    
    // ============================================
    // รูปภาพหัว (Head Images - Animation Frames)
    // ============================================
    public static class AnimationConfig {
        public String folder;
        public int frameCount;
        
        public AnimationConfig(String folder, int frameCount) {
            this.folder = folder;
            this.frameCount = frameCount;
        }
    }
    
    public static final AnimationConfig[] HEAD_ANIMATIONS = {
        new AnimationConfig("res/head/AngryPig/", 16),
        new AnimationConfig("res/head/BlueBird/", 9),
        new AnimationConfig("res/head/Bunny/", 12),
        new AnimationConfig("res/head/Duck/", 10),
        new AnimationConfig("res/head/FatBird/", 8),
        new AnimationConfig("res/head/Slime/", 10),
        new AnimationConfig("res/head/Skull/", 4)
    };
    
    public static final int HEAD_ANIMATION_SPEED = 80;         // ความเร็วแอนิเมชั่น (มิลลิวินาที)
    public static final int SKULL_INDEX = 6;                   // ตำแหน่ง Skull ใน HEAD_ANIMATIONS
    public static final int SKULL_PENALTY = 30;                // คะแนนที่ถูกลดเมื่อคลิก Skull
    public static final double SKULL_SPAWN_CHANCE = 0.2;       // โอกาสเกิด Skull (20%)
    public static final long SKULL_LIFETIME = 3000;            // ระยะเวลาที่ Skull อยู่ (มิลลิวินาที) 3 วินาที
    
    // ============================================
    // การตั้งค่าการเกิดหัว (Head Spawn Settings)
    // ============================================
    public static final int HEAD_SPAWN_MARGIN = 50;          // ระยะขอบสำหรับการเกิดหัว
    public static final double HEAD_VELOCITY_RANGE = 6.0;    // ช่วงความเร็วของหัว
    public static final int HEAD_IMAGE_SCALE = 2;            // ขนาดการขยายรูปหัว
    public static final int HEAD_DEFAULT_SIZE = 72;          // ขนาดเริ่มต้นของหัว
    
    // ============================================
    // ฟิสิกส์ของหัว (Head Physics)
    // ============================================
    public static final double HEAD_GRAVITY = 0.0;           // แรงโน้มถ่วงของหัว
    public static final double HEAD_BOUNCE = 1.0;            // ค่าการตีกลับของหัว
    
    // ============================================
    // การตั้งค่าผู้เล่น (Player Settings)
    // ============================================
    public static final int HEAD_HITBOX_PADDING = 30;        // ระยะขยายของ hitbox หัว
    
    // ============================================
    // การตั้งค่าตัวจับเวลา (Timer Settings)
    // ============================================
    public static final int PHYSICS_UPDATE_INTERVAL = 33;    // ความถี่อัพเดทฟิสิกส์ (มิลลิวินาที)
    public static final int GAME_TIMER_INTERVAL = 1000;      // ความถี่อัพเดทเวลาเกม (มิลลิวินาที)
    public static final int RENDER_UPDATE_INTERVAL = 16;     // ความถี่การวาดภาพ (~60 FPS)
    public static final int POSITION_SEND_INTERVAL = 10;     // ความถี่ส่งตำแหน่ง (มิลลิวินาที)
    
    // ============================================
    // การตั้งค่า UI - สีพื้นหลัง (Background Colors)
    // ============================================
    public static final Color GAME_BACKGROUND_COLOR = new Color(60, 60, 60);   // สีพื้นหลังเกม
    public static final Color MENU_BACKGROUND_COLOR = new Color(102, 102, 102); // สีพื้นหลังเมนู
    
    // ============================================
    // การตั้งค่า UI - เคอร์เซอร์ (Cursor)
    // ============================================
    public static final int CURSOR_SIZE = 48;                           // ขนาดเคอร์เซอร์
    public static final String CURSOR_IMAGE = "res/crosshair182.png";  // รูปเคอร์เซอร์
    
    // ============================================
    // การตั้งค่า UI - การแสดงเวลา (Timer Display)
    // ============================================
    public static final int TIMER_FONT_SIZE = 48;            // ขนาดฟอนต์ตัวจับเวลา
    public static final int TIMER_WARNING_THRESHOLD = 30;    // เวลาเริ่มเตือน (วินาที)
    public static final int TIMER_CRITICAL_THRESHOLD = 10;   // เวลาวิกฤต (วินาที)
    
    // ============================================
    // การตั้งค่า UI - กระดานคะแนน (Scoreboard)
    // ============================================
    public static final int SCOREBOARD_X = 10;                         // ตำแหน่ง X ของกระดานคะแนน
    public static final int SCOREBOARD_Y = 10;                         // ตำแหน่ง Y ของกระดานคะแนน
    public static final int SCOREBOARD_WIDTH = 220;                    // ความกว้างกระดานคะแนน
    public static final int SCOREBOARD_HEIGHT_PER_PLAYER = 30;         // ความสูงต่อผู้เล่น
    public static final int SCOREBOARD_TITLE_SIZE = 18;                // ขนาดฟอนต์หัวข้อ
    public static final int SCOREBOARD_TEXT_SIZE = 14;                 // ขนาดฟอนต์ข้อความ
    
    // ============================================
    // การตั้งค่าระเบิด (Explosion Settings)
    // ============================================
    public static final long EXPLOSION_DURATION = 500;       // ระยะเวลาระเบิด (มิลลิวินาที)
    public static final int EXPLOSION_SIZE = 64;             // ขนาดระเบิด
    public static final String[] EXPLOSION_FRAMES = {        // เฟรมแอนิเมชั่นระเบิด
        "res/particles/ranged_particles-sheet0-0.png",
        "res/particles/ranged_particles-sheet0-1.png",
        "res/particles/ranged_particles-sheet0-2.png"
    };
    public static final int EXPLOSION_FRAME_DURATION = 166;  // ระยะเวลาต่อเฟรม (มิลลิวินาที)
    
    // ============================================
    // การตั้งค่าข้อความคอมโบ (Combo Text Settings)
    // ============================================
    public static final long COMBO_TEXT_DURATION = 1000;     // ระยะเวลาแสดงข้อความคอมโบ (มิลลิวินาที)
    public static final Color COMBO_TEXT_COLOR = new Color(255, 215, 0); // สีข้อความคอมโบ (ทอง)
    public static final int COMBO_TEXT_FONT_SIZE = 32;       // ขนาดฟอนต์ข้อความคอมโบ
    public static final float COMBO_TEXT_Y_OFFSET = 50.0f;   // ระยะเลื่อนขึ้นของข้อความ
    public static final float COMBO_TEXT_SCALE = 0.5f;       // อัตราการขยายข้อความ
    
    // ============================================
    // การตั้งค่าหน้าจบเกม (Game Over Screen Settings)
    // ============================================
    public static final int[] GAMEOVER_Y_POSITIONS = {299, 372, 444, 517}; // ตำแหน่ง Y ของแต่ละผู้เล่น
    public static final int GAMEOVER_ANIMATION_INTERVAL = 20;               // ความถี่แอนิเมชั่น (มิลลิวินาที)
    public static final int GAMEOVER_MAX_DISPLAY_PLAYERS = 4;               // จำนวนผู้เล่นสูงสุดที่แสดง
    
    // ============================================
    // การตั้งค่าเคอร์เซอร์ผู้เล่นอื่น (Other Player Cursor Settings)
    // ============================================
    public static final int OTHER_CURSOR_SIZE = 15;          // ขนาดเส้นกากบาทเคอร์เซอร์
    public static final int OTHER_CURSOR_CIRCLE_SIZE = 20;   // รัศมีวงกลมเคอร์เซอร์
    public static final int OTHER_CURSOR_STROKE_WIDTH = 3;   // ความหนาเส้นเคอร์เซอร์
    
    // ============================================
    // ทรัพยากร (Resources)
    // ============================================
    public static final String BACKGROUND_IMAGE = "res/map_bg_.png"; // รูปพื้นหลังเกม
}
