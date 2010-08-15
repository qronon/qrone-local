package org.qrone.r7.app;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.qrone.img.ImageBuffer;
import org.qrone.img.ImageBufferService;

public class AwtImageBufferService implements ImageBufferService{

	@Override
	public ImageBuffer createImage(int width, int height) {
		return new AwtImageBuffer(new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR));
	}

	@Override
	public ImageBuffer createImage(InputStream in) {
		try {
			return new AwtImageBuffer(ImageIO.read(in));
		} catch (IOException e) {
			return null;
		}
	}

}
