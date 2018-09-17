/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pacssid.view;

/**
 *
 * @author roberto
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.JOptionPane;
import org.dcm4che2.imageio.plugins.dcm.DicomImageReadParam;

/**
 *
 * @author roberto
 */
public class DicomParaJpeg {

    public void Converte(String path, int nomeArquivo) {
        File imagemDicom = new File("O:\\" + path);
        BufferedImage meuJpeg = null;
        Iterator<ImageReader> iter = ImageIO.getImageReadersByFormatName("DICOM");
        ImageReader reader = (ImageReader) iter.next();
        DicomImageReadParam param = (DicomImageReadParam) reader.getDefaultReadParam();
        try {
            ImageInputStream iis = ImageIO.createImageInputStream(imagemDicom);
            reader.setInput(iis, false);
            meuJpeg = reader.read(0, param);
            iis.close();

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, ex);
            Logger.getLogger(DicomParaJpeg.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (meuJpeg == null) {
            System.out.println("\nError: não foi possível ler a imagem dicom !");
            return;
        }

        File localJPG = new File("src/pacssid/temp/" + nomeArquivo + ".jpg");
        try {
            OutputStream output = new BufferedOutputStream(new FileOutputStream(localJPG));
            try {
                ImageIO.write(meuJpeg, "jpg", output);
                output.close();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, ex);
                Logger.getLogger(DicomParaJpeg.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DicomParaJpeg.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
