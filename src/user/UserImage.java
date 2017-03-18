package user;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class UserImage {

	ArrayList<String> imagesMale = new ArrayList<String>();
	ArrayList<String> imagesFemale = new ArrayList<String>();

	Random rand = new Random();

	private String base64Image;

	public UserImage(String base64Image){
		base64Image = base64Image.replace(' ', '+');
		this.base64Image = base64Image;
	}
	
	public UserImage(UserImage image){
		this.base64Image = image.base64Image;
	}

	//getters
	public String getBase64Image(){
		return this.base64Image;
	}

	public void resizeImage(int newH, int newW){

		String[] tokens = this.base64Image.split(",");
		String base64ImageData = tokens[1];

		// create a buffered image
		BufferedImage image = null;
		byte[] imageByte;


		try {
			BASE64Decoder decoder = new BASE64Decoder();
			imageByte = decoder.decodeBuffer(base64ImageData);
			ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);

			image = ImageIO.read(bis);
			Image tmp = image.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
			BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

			Graphics2D g2d = dimg.createGraphics();
			g2d.drawImage(tmp, 0, 0, null);
			g2d.dispose();

			String imageString = null;
			ByteArrayOutputStream bos = new ByteArrayOutputStream();


			ImageIO.write(dimg, "png", bos);

			byte[] imageBytes = bos.toByteArray();

			BASE64Encoder encoder = new BASE64Encoder();
			imageString = encoder.encode(imageBytes);
			imageString = imageString.replace("\n", "");

			this.base64Image = "data:image/png;base64,"+imageString;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();



		}
	}
	
}
