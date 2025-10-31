import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class WindowDragger extends MouseAdapter {
    private Point initialClick;
    private JFrame frame;
    private boolean isDragging = false;
    private static final int DRAG_THRESHOLD = 10;
    
    public WindowDragger(JFrame frame) {
        this.frame = frame;
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        initialClick = e.getPoint();
        isDragging = false;
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        isDragging = false;
        initialClick = null;
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        if (initialClick != null) {
            int xMoved = e.getX() - initialClick.x;
            int yMoved = e.getY() - initialClick.y;
            
            if (!isDragging) {
                double distance = Math.sqrt(xMoved * xMoved + yMoved * yMoved);
                if (distance < DRAG_THRESHOLD) {
                    return;
                }
                isDragging = true;
            }
            
            int thisX = frame.getLocation().x;
            int thisY = frame.getLocation().y;
            
            int X = thisX + xMoved;
            int Y = thisY + yMoved;
            frame.setLocation(X, Y);
        }
    }
}

