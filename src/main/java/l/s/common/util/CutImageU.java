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



public class CutImageU { 
	/**
	 * 
	 * @param args
	 * 			args[0] methodName. 
	 * 			args[1] filePath. 
	 * 			args[...] methodArgs.
	 */
	public static void main(String[] args) throws Exception{
		ImageReader reader = ImageIO.getImageReadersBySuffix("jpg").next();
		ImageInputStream imageInputStream = ImageIO.createImageInputStream(new FileInputStream("c:/ttt.jpg"));
		reader.setInput(imageInputStream);
		
		CutImageU cutImageU = new CutImageU();
		WritableRaster writableRaster = cutImageU.createWritableRaster(reader,1000, 1000);
		cutImageU.doAllCutImage(reader, writableRaster, 0, 0, 50, 300, 0, 0, 30, 50);
		cutImageU.outWrite(reader, writableRaster,"c:/output/output.jpg");
		
		reader.dispose();
		imageInputStream.close();
	}
	public int getImageWidth(File image) throws Exception{
		ImageReader reader = ImageIO.getImageReadersBySuffix("jpg").next();
		ImageInputStream in = ImageIO.createImageInputStream(new FileInputStream(image));
		reader.setInput(in);
		int w = reader.getWidth(0);
		reader.dispose();
		in.close();
		return w;
	}
	
	public int getImageHeight(File image) throws Exception{
		ImageReader reader = ImageIO.getImageReadersBySuffix("jpg").next();
		ImageInputStream in = ImageIO.createImageInputStream(new FileInputStream(image));
		reader.setInput(in);
		int h = reader.getHeight(0);
		reader.dispose();
		in.close();
		return h;
	}
	
	public WritableRaster createWritableRaster(ImageReader reader,int width,int height) throws Exception{
		ImageReadParam param = reader.getDefaultReadParam();
		param.setSourceRegion(new Rectangle(0,0,1,1));
		SampleModel model = reader.readRaster(0, param).getSampleModel();
		
		WritableRaster writableRaster = Raster.createWritableRaster(model.createCompatibleSampleModel(width, height) , new Point(0,0));
//
		System.out.println(reader.read(0).getRGB(15, 15));
		DataBuffer buffer = writableRaster.getDataBuffer();
		System.out.println(buffer.getElem(0));
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
	 * xiaobao
	 * @param x               截取图片的左上角x坐标
	 * @param y				  截取图片的左上角y坐标
	 * @param w				  截取图片的宽度
	 * @param h				  截取图片的高度
	 * @param toX			  复制截取的图片到新图片的位置（左上角x的坐标）
	 * @param toY			  复制截取的图片到新图片的位置（左上角y的坐标）
	 * @param minW			  截取图片的过程中使用分块截取，每小块的宽度，数值越小，使用内存越小，效率越低，反之亦然
	 * @param minH			  截取图片的过程中使用分块截取，没小块的高度，数值越小，使用内存越小，效率越低，反之亦然
	 * @throws Exception	  截取图片过程中抛出异常
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
				
				System.out.println("over,"+x+" "+y);
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
		
		ImageWriter writer = ImageIO.getImageWriter(reader);
		ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(new FileOutputStream(outFile));
		writer.setOutput(imageOutputStream);
		writer.write(image);
		System.out.println(writableRaster.getBounds());
		
		imageOutputStream.close();
		writer.dispose();
	}
	
	/** 
     * 图像缩放 jpg格式 
     *  
     * @param filePath
     *            :原图片文件路径 
     * @param outfilePath
     *            :生成的缩略图片文件路径 
     * @param width
     *            :生成图片的宽度 
     * @param height
     *            :生成图片的高度 
     */  
    public WritableRaster reduceImg(ImageReader reader,String filePath,String outfilePath,int width,int height)throws Exception{
		
       
		Image image = reader.read(0);  

        BufferedImage tag = new BufferedImage(width,  
                 height, BufferedImage.TYPE_INT_RGB);  
         
        /* 
         * Image.SCALE_SMOOTH 的缩略算法  生成缩略图片的平滑度的 
         * 优先级比速度高 生成的图片质量比较好 但速度慢 
         */  
        tag.getGraphics().drawImage(  
        		image.getScaledInstance(width, height,Image.SCALE_SMOOTH), 0, 0, null);
        return tag.getRaster();
	}
    
   
	
	public void destroy(){
		
	}
	
}
