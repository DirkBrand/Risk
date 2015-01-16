package risk.gui.clientGui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import risk.commonObjects.Territory;

public class MapBackgroundPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7693033290557968951L;

	public BufferedImage originalImage;

	private File imgFile;
	double factor;

	/**
	 * Create the panel.
	 * 
	 * @param factor
	 */

	public MapBackgroundPanel(String filename, double factor)
			throws IOException {
		this.factor = factor;

		imgFile = new File(filename);

		originalImage = ImageIO.read(imgFile);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		// Draw the background image.
		if (originalImage != null) {
			g.drawImage(originalImage, 0, 0, this.getWidth(), this.getHeight(),
					this);
			// g.drawImage(originalImage, 0,0,null);
		}

	}

	public int getImageHeight() {
		return originalImage.getHeight(null);
	}

	public int getImageWidth() {
		return originalImage.getWidth(null);
	}

	/* private static BufferedImage resizeImage(BufferedImage originalImage,
			int type, Integer img_width, Integer img_height) {
		BufferedImage resizedImage = new BufferedImage(img_width, img_height,
				type);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, img_width, img_height, null);
		g.dispose();

		return resizedImage;
	} */

	public void updateMap(HashMap<String, Territory> hashMap, HashMap<String, Territory> hashMap2, Color c1, Color c2) {
		Graphics2D g2d = originalImage.createGraphics();

 
		g2d.setBackground(Color.white);
		Iterator<Territory> it =  hashMap.values().iterator();
		while (it.hasNext()) {
			Territory t = it.next();
			g2d.fillRoundRect(t.getXCoordinate(), t.getYCoordinate(), 18, 11, 8, 12);
		}
		it =  hashMap2.values().iterator();
		while (it.hasNext()) {
			Territory t = it.next();
			g2d.fillRoundRect(t.getXCoordinate(), t.getYCoordinate(), 18, 11, 8, 12);
		}
		

		g2d.setColor(c1);
		g2d.setFont(new Font("Ariel", Font.PLAIN, 11));
		it =  hashMap.values().iterator();
		while (it.hasNext()) {
			Territory t = it.next();
			g2d.drawRoundRect(t.getXCoordinate(), t.getYCoordinate(), 18, 11, 5, 5);
			g2d.drawString(t.getNrTroops() + "", t.getXCoordinate() + 4,
					t.getYCoordinate() + 10);			
		}
		
		g2d.setColor(c2);
		it =  hashMap2.values().iterator();
		while (it.hasNext()) {
			Territory t = it.next();
			g2d.drawRoundRect(t.getXCoordinate(), t.getYCoordinate(), 18, 11, 5, 5);
			g2d.drawString(t.getNrTroops() + "", t.getXCoordinate() + 4,
					t.getYCoordinate() + 10);				
		}		
	}

}
