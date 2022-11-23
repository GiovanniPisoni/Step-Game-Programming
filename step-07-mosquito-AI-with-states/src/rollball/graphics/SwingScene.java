package rollball.graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.KeyEventDispatcher;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.*;

import rollball.common.P2d;
import rollball.input.*;
import rollball.model.*;

public class SwingScene implements Scene {
    
	private JFrame frame;
    private ScenePanel panel;
    private KeyboardInputController controller;
    private GameState gameState;
    
    public SwingScene(GameState gameState, KeyboardInputController controller, int w, int h, double width, double height){
    	frame = new JFrame("Roll A Ball");
    	frame.setSize(w,h);
    	frame.setMinimumSize(new Dimension(w,h));
        frame.setResizable(false);
        // frame.setUndecorated(true); // Remove title bar
        this.gameState = gameState;
        this.controller = controller;
        panel = new ScenePanel(w,h, width, height);
        frame.getContentPane().add(panel);
        frame.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent ev){
				System.exit(-1);
			}
			public void windowClosed(WindowEvent ev){
				System.exit(-1);
			}
		});
        frame.pack();
        frame.setVisible(true);
    }
    
    public void render(){
    	try {
	    	frame.repaint();
    	} catch (Exception ex){
    		ex.printStackTrace();
    	}
    }
    
    public void renderGameOver(){
    	try {
	    	frame.repaint();
    	} catch (Exception ex){
    		ex.printStackTrace();
    	}
    }
    
    public class ScenePanel extends JPanel implements KeyListener {

    	private int centerX;
    	private int centerY;
    	private double ratioX;
    	private double ratioY;
        private Font scoreFont, gameOverFont;
		private Stroke strokeBorder = new BasicStroke(2f);

    	public ScenePanel(int w, int h, double width, double height){
            setSize(w,h);
            centerX = w/2;
            centerY = h/2;
            ratioX = w/width;
            ratioY = h/height;
            
            scoreFont = new Font("Verdana", Font.PLAIN, 36);
            gameOverFont = new Font("Verdana", Font.PLAIN, 88);
            
            this.addKeyListener(this);
            setFocusable(true);
            setFocusTraversalKeysEnabled(false);
            requestFocusInWindow(); 
            
        }
        
        public void paint(Graphics g){
    		Graphics2D g2 = (Graphics2D) g;
    		
    		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
    		          RenderingHints.VALUE_ANTIALIAS_ON);
    		g2.setRenderingHint(RenderingHints.KEY_RENDERING,
    		          RenderingHints.VALUE_RENDER_QUALITY);
    		g2.clearRect(0,0,this.getWidth(),this.getHeight());
            
    		if (gameState.isGameOver()){

    			/* drawing the score */
				g2.setFont(gameOverFont);
				g2.setColor(Color.BLACK);
				g2.drawString("GAME OVER ", 30, centerY - 50);
				g2.setFont(scoreFont);
				g2.setColor(Color.GREEN);
				g2.drawString("Final score "+gameState.getScore(), 180, centerY+50);
       			
    		} else {
	    		/* drawing the borders */
    			
	    		World scene = gameState.getWorld();
				RectBoundingBox bbox = scene.getBBox();
				int x0 = getXinPixel(bbox.getULCorner());
				int y0 = getYinPixel(bbox.getULCorner());
				int x1 = getXinPixel(bbox.getBRCorner());
				int y1 = getYinPixel(bbox.getBRCorner());
				
				g2.setColor(Color.BLACK);
				g2.setStroke(strokeBorder);			
				g2.drawRect(x0, y0, x1-x0, y1-y0);
				
				/* drawing the game objects */
				
				SwingGraphics gr = new SwingGraphics(g2, centerX, centerY, ratioX, ratioY);
				gameState.getWorld().getSceneEntities().forEach( e -> {
					e.updateGraphics(gr);
				});
				
				/* drawing the score */
				g2.setFont(scoreFont);
				g2.setColor(Color.GREEN);
				g2.drawString("SCORE "+gameState.getScore(), 30, 50);
    		}
        }

    	private int getXinPixel(P2d p){
    		return (int) Math.round(centerX + p.x * ratioX);
    	}

    	private int getYinPixel(P2d p){
    		return (int)  Math.round(centerY - p.y * ratioY);
    	}
    	
    	@Override
		public void keyPressed(KeyEvent e) {
	     	if (e.getKeyCode() == 38){
	     		controller.notifyMoveUp();
	     	} else if (e.getKeyCode() == 40){
	     		controller.notifyMoveDown();
	     	} else if (e.getKeyCode() == 39){
	     		controller.notifyMoveRight();
	     	} else if (e.getKeyCode() == 37){
	     		controller.notifyMoveLeft();
	     	}
		}

		@Override
		public void keyTyped(KeyEvent e) {}

		@Override
		public void keyReleased(KeyEvent e) {
	     	if (e.getKeyCode() == 38){
	     		controller.notifyNoMoreMoveUp();
	     	} else if (e.getKeyCode() == 40){
	     		controller.notifyNoMoreMoveDown();
	     	} else if (e.getKeyCode() == 39){
	     		controller.notifyNoMoreMoveRight();
	     	} else if (e.getKeyCode() == 37){
	     		controller.notifyNoMoreMoveLeft();
	     	}
		}
        
    }
}
