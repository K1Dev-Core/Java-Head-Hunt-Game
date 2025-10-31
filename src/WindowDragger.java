import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class WindowDragger extends MouseAdapter {
    private Point initialClick;
    private JFrame frame;
    
    public WindowDragger(JFrame frame) {
        this.frame = frame;
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        initialClick = e.getPoint();
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        if (initialClick != null) {
            int thisX = frame.getLocation().x;
            int thisY = frame.getLocation().y;
            
            int xMoved = e.getX() - initialClick.x;
            int yMoved = e.getY() - initialClick.y;
            
            int X = thisX + xMoved;
            int Y = thisY + yMoved;
            frame.setLocation(X, Y);
        }
    }
}

