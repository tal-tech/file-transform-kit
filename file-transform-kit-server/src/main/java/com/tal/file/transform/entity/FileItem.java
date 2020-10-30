package com.tal.file.transform.entity;

import java.io.File;

/**
 * Description
 * <p>
 * </p>
 * DATE 2019/6/19.
 *
 * @author 刘江涛.
 */
public class FileItem {

    public FileItem(final File file, final String fileName, final String filedName) {
        this.file = file;
        this.fileName = fileName;
        this.filedName = filedName;
    }

    private File file;

    private String fileName;

    private String filedName;

}
