package com.tal.cloud.storage.node.controller.image;

import java.awt.image.BufferedImage;

import com.mortennobel.imagescaling.ResampleOp;
import com.tal.cloud.storage.common.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 图片的缩略图生成规则进行了调整：
 * 1. 取消所有补偿操作，用缩放替代补偿
 * 即：之前所有做补偿操作的地方，现在变成缩放后，再裁剪
 * 这个调整主要涉及 specific 方法
 * 2. 改进了图片优化算法
 * 
 * @author liujt
 *
 */
public class ImageCore {

	protected static final Logger log = LoggerFactory.getLogger(ImageCore.class);

	// 尺寸较短
	private static final int SIZE_SHORTER = 1;
	// 尺寸匹配
	private static final int SIZE_MATCHABLE = 2;
	// 尺寸较长
	private static final int SIZE_LONGER = 3;

	// 高和高都较短
	private static final int SIZE_SHORTER_W_SHORTER_H = 11;
	// 宽较短，高匹配
	private static final int SIZE_SHORTER_W_MATCHABLE_H = 12;
	// 宽较短，高较长
	private static final int SIZE_SHORTER_W_LONGER_H = 13;

	// 高较短，宽匹配
	private static final int SIZE_MATCHABLE_W_SHORTER_H = 21;
	// 高匹配，宽匹配
	private static final int SIZE_MATCHABLE_W_MATCHABLE_H = 22;
	// 高较长，宽匹配
	private static final int SIZE_MATCHABLE_W_LONGER_H = 23;

	// 宽较长，高较短
	private static final int SIZE_LONGER_W_SHORTER_H = 31;
	// 宽较长，高匹配
	private static final int SIZE_LONGER_W_MATCHABLE_H = 32;
	// 宽较长，高较长
	private static final int SIZE_LONGER_W_LONGER_H = 33;

	// 内部缩放
	private static final int SCALE_INNER = 1;
	// 匹配不缩放
	private static final int SCALE_MATCHABLE = 2;
	// 外部缩放
	private static final int SCALE_OUTER = 3;

	public static final String KEY_SELF = "_self";
	public static final String KEY_OPTIMIZE = "_o";
	public static final String KEY_SCALE = "_r%dx%dz%dx%d";
	public static final String KEY_SPECIFIC = "_r%dx%d";
	public static final String KEY_META = "_meta";

	// 优化操作
	public final static int OPERATE_OPTIMIZE = 1;
	// 范围操作
	public final static int OPERATE_SCALE = 2;
	// 具体操作
	public final static int OPERATE_SPECIFIC = 3;

	public final static int OPERATE_SELF_SCALE  = 4;

	public static int getOp(String opCmd) {
		if(StringUtils.isNotBlank(opCmd)) {
			if("OPTIMIZE".equalsIgnoreCase(opCmd)) {
				return OPERATE_OPTIMIZE;

			} else if("SCALE".equalsIgnoreCase(opCmd)) {
				return OPERATE_SCALE;

			} else if("SPECIFIC".equalsIgnoreCase(opCmd)) {
				return OPERATE_SPECIFIC;
			} else if("SELF_SCALE".equalsIgnoreCase(opCmd)) {
				return OPERATE_SELF_SCALE;
			}

		}

		return 0;
	}

	protected int compareSize(int w, int h, int tarW, int tarH) {
		int widthType = SIZE_MATCHABLE;

		if(w < tarW) {
			widthType = SIZE_SHORTER;

		} else if(w > tarW) {
			widthType = SIZE_LONGER;
		}

		int heightType = SIZE_MATCHABLE;

		if(h < tarH) {
			heightType = SIZE_SHORTER;

		} else if(h > tarH) {
			heightType = SIZE_LONGER;
		}

		int result = widthType * 10 + heightType;

		log.info(String.format("   compareSize: ({%d, %d} <-> [%d, %d]) = %d", tarW, tarH, w, h, result));

		return result;
	}

	protected int compareSize(int w, int h, int minW, int minH, int maxW, int maxH) {
		int widthType = SIZE_MATCHABLE;

		if(w < minW) {
			widthType = SIZE_SHORTER;

		} else if(w > maxW) {
			widthType = SIZE_LONGER;
		}

		int heightType = SIZE_MATCHABLE;

		if(h < minH) {
			heightType = SIZE_SHORTER;

		} else if(h > maxH) {
			heightType = SIZE_LONGER;
		}

		int result = widthType * 10 + heightType;

		log.info(String.format("   compareSize: ({%d, %d} < [%d, %d] < {%d, %d}) = %d", minW, minH, w, h, maxW, maxH, result));

		return result;
	}

	protected int compareScale(int w, int h, int tarW, int tarH) {
		double tarScale = (double)tarW / tarH;
		double scale = (double)w / h;
		int result = (tarScale == scale) ? SCALE_MATCHABLE : SCALE_OUTER;

		log.info(String.format("   compareScale: ([%.04f] == %.04f) = %d", scale, tarScale, result));

		return result;
	}

	protected int compareScale(int w, int h, int minW, int minH, int maxW, int maxH) {
		double minScale = (double)minW / maxH, maxScale = (double)maxW / minH;
		double scale = (double)w / h;

		int result = SCALE_OUTER;

		if(scale >= minScale && scale <= maxScale) {
			result = SCALE_INNER;
		}

		log.info(String.format("   compareScale: (%.04f < [%.04f] < %.04f) = %d", minScale, scale, maxScale, result));

		return result;
	}

	public BufferedImage optimize(BufferedImage img) {
		long startTime = System.currentTimeMillis();
		BufferedImage r = img;

		log.info(String.format("// optimize cost: %d ms", System.currentTimeMillis() - startTime));

		return r;
	}

	public BufferedImage optimize(BufferedImage img, int w, int h, int limitW, int limitH) {
		long startTime = System.currentTimeMillis();
		BufferedImage dest = img;

		if(w > limitW) {
			double scale = (double)limitW / (double)w;
			int xw = limitW, xh = (int)Math.round(h * scale);

			if(xh >= limitH) {
				ResampleOp resampleOp = new ResampleOp(xw, xh);

				dest = resampleOp.filter(img, null);
			}
		}

		log.info(String.format("// optimize cost: %d ms", System.currentTimeMillis() - startTime));

		return dest;
	}

	public BufferedImage scale(BufferedImage img, int w, int h, int minW, int minH, int maxW, int maxH) {
		long startTime = System.currentTimeMillis();
		int xw = 0, xh = 0, dw = 0, dh = 0;
		int x = 0, y = 0;
		double scale = 0;
		ResampleOp resampleOp = null;
		BufferedImage tmp = null;
		BufferedImage dest = null;

		switch(compareScale(w, h, minW, minH, maxW, maxH)) {
		/**
		 * 图片宽高比在指定范围内
		 */
		case SCALE_INNER:
			switch(compareSize(w, h, minW, minH, maxW, maxH)) {
			/**
			 * 图片尺寸小于最大尺寸或小于最小尺寸
			 * 以短边为准，按比例缩放至同边方向的最小值。
			 */
			case SIZE_SHORTER_W_SHORTER_H:
			case SIZE_SHORTER_W_MATCHABLE_H:
			case SIZE_MATCHABLE_W_SHORTER_H:
				if(w > h) {
					scale = (double)minH / h;
					xw = (int)Math.round(w * scale);
					xh = minH;

				} else {
					xw = minW;
					scale = (double)minW / w;
					xh = (int)Math.round(h * scale);
				}

				resampleOp = new ResampleOp(xw, xh);
				dest = resampleOp.filter(img, null);
				break;

			/**
			 * 又长又短的情况？
			 */
			case SIZE_SHORTER_W_LONGER_H: break;
			case SIZE_LONGER_W_SHORTER_H: break;

			/**
			 * 图片尺寸在指定范围内
			 * 原图输出
			 */
			case SIZE_MATCHABLE_W_MATCHABLE_H:
				dest = img;
				break;

			/**
			 * 图片尺寸大于最小尺寸或大于最大尺寸
			 * 以长边为准，按比例缩放至同边方向的最大值。
			 */
			case SIZE_MATCHABLE_W_LONGER_H:
			case SIZE_LONGER_W_MATCHABLE_H:
			case SIZE_LONGER_W_LONGER_H:
				if(w > h) {
					xw = maxW;
					scale = (double)maxW / w;
					xh = (int)Math.round(h * scale);

				} else {
					scale = (double)maxH / h;
					xw = (int)Math.round(w * scale);
					xh = maxH;
				}

				resampleOp = new ResampleOp(xw, xh);
				dest = resampleOp.filter(img, null);
				break;
			}
			break;

		/**
		 * 图片宽高比不在指定范围内
		 * 以短边为准，按比例缩放至同边方向的最小值，然后裁剪掉长边方向的多余部分。
		 */
		case SCALE_OUTER:
			/**
			 * 这是一张宽图，截取水平居中的区域
			 */
			if(w > h) {
				scale = (double)minH / h;
				xw = (int)Math.round(w * scale);
				xh = minH;

				resampleOp = new ResampleOp(xw, xh);
				tmp = resampleOp.filter(img, null);

				if(xw > maxW) {
					dw = maxW;
					dh = xh;
					x = (xw - dw) / 2;
					y = 0;

					dest = new BufferedImage(dw, dh, BufferedImage.TYPE_INT_RGB);

					dest.getGraphics().drawImage(tmp, 0, 0, dw, dh, x, y, x + dw, y + dh, null);  

				} else {
					dest = tmp;
				}

			} else {
				/**
				 * 这是一张长图，截取垂直居中的区域
				 */
				xw = minW;
				scale = (double)minW / w;
				xh = (int)Math.round(h * scale);

				resampleOp = new ResampleOp(xw, xh);
				tmp = resampleOp.filter(img, null);

				if(xh > maxH) {
					dw = xw;
					dh = maxH;
					x = 0;
					y = (xh - dh) / 2;

					dest = new BufferedImage(dw, dh, BufferedImage.TYPE_INT_RGB);

					dest.getGraphics().drawImage(tmp, 0, 0, dw, dh, x, y, x + dw, y + dh, null);  

				} else {
					dest = tmp;
				}
			}
			break;
		}

		log.info(String.format("// scale cost: %d ms", System.currentTimeMillis() - startTime));

		return dest;
	}

	public BufferedImage specific(final BufferedImage img, int w, int h, int specW, int specH) {
		long startTime = System.currentTimeMillis();
		int xw = specW, xh = specH;
		int x = 0, y = 0;
		double scale = 0, scaleX = 0, scaleY = 0;
		ResampleOp resampleOp = null;
		BufferedImage tmp = null;
		BufferedImage dest = null;

		switch(compareSize(w, h, specW, specH)) {
		/**
		 * 宽高均小于指定宽高
		 * 以短边为准，等比缩放到同边宽度，再裁剪长边的超出部分
		 */
		case SIZE_SHORTER_W_SHORTER_H:
			scaleX = (double)specW / w;
			scaleY = (double)specH / h;

			if(scaleX > scaleY) {
				xw = specW;
				xh = (int)Math.round(h * scaleX);

			} else if(scaleX < scaleY) {
				xw = (int)Math.round(w * scaleY);
				xh = specH;
			}

			resampleOp = new ResampleOp(xw, xh);

			tmp = resampleOp.filter(img, null);

			if(xw == specW && xh == specH) {
				dest = tmp;

			} else {
				dest = new BufferedImage(specW, specH, BufferedImage.TYPE_INT_RGB);

				if(dest != null) {
					x = (xw - specW) / 2;
					y = (xh - specH) / 2;

					dest.getGraphics().drawImage(tmp, 0, 0, specW, specH, x, y, x + specW, y + specH, null);  
				}
			}
			break;

		/**
		 * 宽度小于指定宽度，高度合适或大于指定高度
		 * 先根据宽度进行等比缩放，然后裁剪高出的部分
		 */
		case SIZE_SHORTER_W_MATCHABLE_H:
		case SIZE_SHORTER_W_LONGER_H:
			scale = (double)specW / w;
			xw = specW;
			xh = (int)Math.round(h * scale);

			resampleOp = new ResampleOp(xw, xh);

			tmp = resampleOp.filter(img, null);

			x = 0;
			y = (xh - specH) / 2;

			dest = new BufferedImage(specW, specH, BufferedImage.TYPE_INT_RGB);

			dest.getGraphics().drawImage(tmp, 0, 0, specW, specH, x, y, x + specW, y + specH, null);  
			break;

		/**
		 * 高度不够，宽度合适或宽度大于指定宽度
		 * 先根据高度进行等比缩放，然后裁剪宽出的部分
		 */
		case SIZE_MATCHABLE_W_SHORTER_H:
		case SIZE_LONGER_W_SHORTER_H:
			scale = (double)specH / h;
			xw = (int)Math.round(w * scale);
			xh = specH;

			resampleOp = new ResampleOp(xw, xh);

			tmp = resampleOp.filter(img, null);

			x = (xw - specW) / 2;
			y = 0;

			dest = new BufferedImage(specW, specH, BufferedImage.TYPE_INT_RGB);

			dest.getGraphics().drawImage(tmp, 0, 0, specW, specH, x, y, x + specW, y + specH, null);  
			break;

		/**
		 * 宽高均合适
		 * 直接原图
		 */
		case SIZE_MATCHABLE_W_MATCHABLE_H:
			dest = img;
			break;

		/**
		 * 宽度合适，高度大于指定高度
		 * 裁剪高出的部分
		 */
		case SIZE_MATCHABLE_W_LONGER_H:
			x = 0;
			y = (h - specH) / 2;

			dest = new BufferedImage(specW, specH, BufferedImage.TYPE_INT_RGB);

			dest.getGraphics().drawImage(img, 0, 0, specW, specH, x, y, x + specW, y + specH, null);  
			break;

		/**
		 * 宽度大于指定宽度，高度合适
		 * 裁剪宽出的部分
		 */
		case SIZE_LONGER_W_MATCHABLE_H:
			x = (w - specW) / 2;
			y = 0;

			dest = new BufferedImage(specW, specH, BufferedImage.TYPE_INT_RGB);

			dest.getGraphics().drawImage(img, 0, 0, specW, specH, x, y, x + specW, y + specH, null);  
			break;

		/**
		 * 宽高均大于指定宽高
		 * 先根据指定尺寸按比例放大进行裁剪，再缩放
		 */
		case SIZE_LONGER_W_LONGER_H:
			scaleX = (double)w / specW;
			scaleY = (double)h / specH;

			if(scaleX > scaleY) {
				xw = (int)Math.round(specW * scaleY);
				xh = h;

			} else if(scaleX < scaleY) {
				xw = w;
				xh = (int)Math.round(specH * scaleX);

			} else {
				// liujt, 2015.8.28
				// 如果比例一致，则只需要缩放即可
				resampleOp = new ResampleOp(specW, specH);

				dest = resampleOp.filter(img, null);

				break;
			}

			tmp = new BufferedImage(xw, xh, BufferedImage.TYPE_INT_RGB);

			if(tmp != null) {
				x = (w - xw) / 2;
				y = (h - xh) / 2;

				tmp.getGraphics().drawImage(img, 0, 0, xw, xh, x, y, x + xw, y + xh, null);  

				resampleOp = new ResampleOp(specW, specH);

				dest = resampleOp.filter(tmp, null);
			}
			break;
		}

		log.info(String.format("// specific cost: %d ms", System.currentTimeMillis() - startTime));

		return dest;
	}

	/**
	 * 直接缩放成指定大小图片
	 * @param img
	 * @param specW
	 * @param specH
	 * @return
	 */
	public BufferedImage specific(BufferedImage img, int specW, int specH) {
		ResampleOp resampleOp = new ResampleOp(specW, specH);

		return resampleOp.filter(img, null);
	}

	/**
	 * 为5.2以下的BOSS圈单图做兼容处理
	 * @param img
	 * @param w
	 * @param h
	 * @param minW
	 * @param minH
	 * @param maxW
	 * @param maxH
	 * @param specMinW
	 * @param specMinH
	 * @param specMaxW
	 * @param specMaxH
	 * @return
	 */
	public BufferedImage compatible(BufferedImage img, int w, int h, int minW, int minH, int maxW, int maxH, int specMinW, int specMinH, int specMaxW, int specMaxH) {
		double scaleX = (double)specMaxW / maxW, scaleY = (double)specMaxH / maxH;
		int xw = (int)(w * scaleX), xh = (int)(h * scaleY);

		if(w != xw || h != xh) {
			ResampleOp resampleOp = new ResampleOp(xw, xh);

			return resampleOp.filter(img, null);
		}

		return img;
	}

}
