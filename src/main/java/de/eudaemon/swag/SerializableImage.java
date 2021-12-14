package de.eudaemon.swag;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

public class SerializableImage implements Serializable {
    private transient BufferedImage image;

    public SerializableImage() {

    }

    public BufferedImage getImage() {
        return image;
    }

    public SerializableImage(BufferedImage image_) {image = image_;}

    private void writeObject(ObjectOutputStream out) throws IOException {
        ImageIO.write(image, "png", out);
    }

    private void readObject(ObjectInputStream in) throws IOException {
        image = ImageIO.read(in);
    }
}
