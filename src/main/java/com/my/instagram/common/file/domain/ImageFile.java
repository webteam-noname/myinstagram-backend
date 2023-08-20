package com.my.instagram.common.file.domain;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ImageFile implements FileSearchType{

    private Files files;

    public ImageFile(Files files) {
        this.files = files;
    }

    @Override
    public String get() {
        if(files == null){
            return "no-image.jpg";
        }

        return files.getFile();
    }
}
