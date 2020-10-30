package com.tal.file.transform.controller.media;

import com.tal.file.transform.common.StringUtils;
import org.apache.log4j.Logger;


/**
 * 
 * @author liujt
 *
 */
public abstract class MediaCore {

	protected static final Logger log = Logger.getLogger(MediaCore.class);

	public static int getOp(String opCmd) {
		if(StringUtils.isNotBlank(opCmd)) {
			if("TRANSFER_AUDIO".equalsIgnoreCase(opCmd)) {
				return Mct.OPERATE_TRANSFER_AUDIO;

			} else if("TRANSFER_VIDEO".equalsIgnoreCase(opCmd)) {
				return Mct.OPERATE_TRANSFER_VIDEO;

			} else if("THUMBNAIL".equalsIgnoreCase(opCmd)) {
				return Mct.OPERATE_THUMBNAIL;
			}
		}

		return 0;
	}

}
