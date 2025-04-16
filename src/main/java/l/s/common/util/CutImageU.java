package l.s.common.util;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;


@Deprecated
public class CutImageU { 
	/**
	 * 
	 * @param args
	 * 			args[0] methodName. 
	 * 			args[1] filePath. 
	 * 			args[...] methodArgs.
	 */
	public static void main(String[] args) throws Exception{
		ImageReader reader = null;
		ImageInputStream imageInputStream = null;
		try{
			reader = ImageIO.getImageReadersBySuffix("jpg").next();
			imageInputStream = ImageIO.createImageInputStream(new FileInputStream("c:/ttt.jpg"));
			reader.setInput(imageInputStream);

			CutImageU cutImageU = new CutImageU();
			WritableRaster writableRaster = cutImageU.createWritableRaster(reader,1000, 1000);
			cutImageU.doAllCutImage(reader, writableRaster, 0, 0, 50, 300, 0, 0, 30, 50);
			cutImageU.outWrite(reader, writableRaster,"c:/output/output.jpg");
		} finally {
            if (reader != null) {
                reader.dispose();
            }
			l.s.common.util.IoUtil.close(imageInputStream);
        }


	}
	public int getImageWidth(File image) throws Exception{
		ImageReader reader = null;
		ImageInputStream imageInputStream = null;
		try{
			reader = ImageIO.getImageReadersBySuffix("jpg").next();
			imageInputStream = ImageIO.createImageInputStream(new FileInputStream(image));
			reader.setInput(imageInputStream);
            return reader.getWidth(0);
		}finally {
			if (reader != null) {
				reader.dispose();
			}
			l.s.common.util.IoUtil.close(imageInputStream);
		}

	}
	
	public int getImageHeight(File image) throws Exception{
		ImageReader reader = null;
		ImageInputStream imageInputStream = null;
		try{
			reader = ImageIO.getImageReadersBySuffix("jpg").next();
			imageInputStream = ImageIO.createImageInputStream(new FileInputStream(image));
			reader.setInput(imageInputStream);
            return reader.getHeight(0);
		}finally {
			if (reader != null) {
				reader.dispose();
			}
			l.s.common.util.IoUtil.close(imageInputStream);
		}
	}
	
	public WritableRaster createWritableRaster(ImageReader reader,int width,int height) throws Exception{
		ImageReadParam param = reader.getDefaultReadParam();
		param.setSourceRegion(new Rectangle(0,0,1,1));
		SampleModel model = reader.readRaster(0, param).getSampleModel();
		
		WritableRaster writableRaster = Raster.createWritableRaster(model.createCompatibleSampleModel(width, height) , new Point(0,0));

		//System.out.println(reader.read(0).getRGB(15, 15));
		DataBuffer buffer = writableRaster.getDataBuffer();
		//System.out.println(buffer.getElem(0));
		int i=0;
		for(int x=0;x<width;x++){
			for(int y=0;y<height;y++){
				buffer.setElem(i++, 255);
				buffer.setElem(i++, 128);
				buffer.setElem(i++, 128);
			}
		}
		
		return writableRaster;
	}
	
	/**
	 * @param x               left-top x
	 * @param y				  left-top y
	 * @param w				  width
	 * @param h				  height
	 * @param toX			  Copy to（left-top x）
	 * @param toY			  Copy to（left-top y）
	 * @param minW			  Copy an image block, width of the image block, while small value to use then the less memory to use and longer time to use (Vice versa).
	 * @param minH			  Copy an image block, width of the image block, while small value to use then the less memory to use and longer time to use (Vice versa).
	 * @throws Exception	  Exceptions.
	 */
	public WritableRaster doAllCutImage(ImageReader reader,WritableRaster writableRaster,int x,int y,int w,int h,int toX,int toY,int minW,int minH)throws Exception{
		
		
		
		
	
		ImageReadParam param = reader.getDefaultReadParam();
//		param.setSourceRegion(new Rectangle(x,y,1,1));
//		SampleModel model = reader.readRaster(0, param).getSampleModel();
		
//		SampleModel model = new SampleModel(1,1,1,1);
//		WritableRaster writableRaster = Raster.createWritableRaster(model.createCompatibleSampleModel(w+toX, h+toY) , new Point(0,0));
//		reader.read(0);
//		DataBuffer buffer = raster.getDataBuffer();
//		int x = 0,y=0;
		int copyX = x,copyY = y,copyToX = toX,/*copyToY = toY,*/copyMinW = minW,copyMinH = minH;
		for(y=copyY;y<copyY+h;y+=minH){
			if(y+minH>copyY+h){
				minH = copyY+h-y;
			}
			for(x=copyX;x<copyX+w;x+=minW){
				if(x+minW>copyX+w){
					minW = copyX+w-x;
				}
				
				param.setSourceRegion(new Rectangle(x,y,minW,minH));
				Raster raster = reader.readRaster(0, param);
				Object o = raster.getDataElements(0, 0, minW, minH, null);
				writableRaster.setDataElements(toX, toY,minW,minH, o);
				
				minW = copyMinW;
				toX += minW;
				
				//System.out.println("over,"+x+" "+y);
			}
			
			minH = copyMinH;
			toX = copyToX;
			toY += minH;
		}
		return writableRaster;

	}
	
	public void outWrite(ImageReader reader,WritableRaster writableRaster,String outFilePath)throws Exception{
		File outFile = new File(outFilePath);
		this.outWrite(reader, writableRaster, outFile);
	}

	public void outWrite(ImageReader reader,WritableRaster writableRaster,File outFile)throws Exception{
		IIOImage image = new IIOImage(writableRaster,null,null);
		ImageWriter writer = null;
		ImageOutputStream imageOutputStream = null;
		try{
			writer = ImageIO.getImageWriter(reader);
			imageOutputStream = ImageIO.createImageOutputStream(new FileOutputStream(outFile));
			writer.setOutput(imageOutputStream);
			writer.write(image);
			//System.out.println(writableRaster.getBounds());

		}finally {
			l.s.common.util.IoUtil.close(imageOutputStream);
            if (writer != null) {
                writer.dispose();
            }
        }

		

	}
	
	/**
	 * Image scaling jpg format
	 *
	 * @param filePath
	 *     Original image file path
	 * @param outfilePath
	 *     Generated thumbnail image file path
	 * @param width
	 *     Generated image width
	 * @param height
	 *     Generated image height
     */  
    public WritableRaster reduceImg(ImageReader reader,String filePath,String outfilePath,int width,int height)throws Exception{
		
       
		Image image = reader.read(0);  

        BufferedImage tag = new BufferedImage(width,  
                 height, BufferedImage.TYPE_INT_RGB);

		/*
		 * Image.SCALE_SMOOTH 's thumbnail algorithm generates thumbnail images with smoothness
		 * It has a higher priority than speed. The generated image quality is better but the speed is slow
		 */
        tag.getGraphics().drawImage(  
        		image.getScaledInstance(width, height,Image.SCALE_SMOOTH), 0, 0, null);
        return tag.getRaster();
	}
    
   
	
	public void destroy(){
		
	}
	
}
