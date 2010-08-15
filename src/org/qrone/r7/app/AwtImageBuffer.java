package org.qrone.r7.app;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import org.qrone.img.ImageBuffer;
import org.qrone.img.ImageRect;

public class AwtImageBuffer implements ImageBuffer{
	private BufferedImage image;
	private Graphics g;
	public AwtImageBuffer(BufferedImage image){
		this.image = image;
	}

	@Override
	public void drawImage(ImageBuffer img, ImageRect to, ImageRect from) {
		if(g == null) g = image.getGraphics();
		
		AwtImageBuffer buf = (AwtImageBuffer)img;
		g.drawImage(buf.image, to.x, to.y, to.x + to.w, to.y + to.h, from.x, from.y, from.x + from.w, from.y + from.h, null);
	}

	@Override
	public int getHeight() {
		return image.getHeight();
	}

	@Override
	public int getWidth() {
		return image.getWidth();
	}

	@Override
	public void writeTo(OutputStream out) {
		try {
			ImageIO.write(image, "png", out);
		} catch (IOException e) {
		}
	}
	
}
